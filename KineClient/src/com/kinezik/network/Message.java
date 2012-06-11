package com.kinezik.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.net.http.AndroidHttpClient;
import android.util.Log;

//TODO Implement error handling
/**
 * A class for sending HttpPosts over the network.
 * @author victormours
 *
 */
public class Message {
	
	private static final String SERVLET = "/KineServ/GetSession";
	private static final String SERVER = "192.168.0.10:8080";
	private static String UUID;
	private static final String USER_AGENT = "KineZik";
	
	
	private List<NameValuePair> nameValuePairs;
	private HttpPost post;
	
	/**
	 * Should be called before any message is created.
	 * @param uuid
	 */
	public static void setUUID(String uuid){
		UUID = uuid;
	}
	
	public Message(){
		nameValuePairs = new ArrayList<NameValuePair>();
		post = new HttpPost("http://" + SERVER + SERVLET);
		add("UUID", UUID);
	}
	
	/**
	 * Adds a key-value pair to the entity of the post.
	 * @param key
	 * @param value
	 */
	public void add(String key, String value){
        nameValuePairs.add(new BasicNameValuePair(key, value));
	}

	/**Sends the message in a separate thread. The method onResponseReceived will be called when the response is received.
	 * The sender may be null. In this case, the response (if any) will be lost.
	 * 
	 */
	public String send(){
		AndroidHttpClient client;
		String responsebody = null;
		client = AndroidHttpClient.newInstance(USER_AGENT);
		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(post);
			responsebody =  getResponseBody(response);
		} catch (UnsupportedEncodingException e) {
			Log.d("ERROR", "Encoding error while preparing a message");
			e.printStackTrace();
		} catch (IOException e) {
			Log.d("ERROR", "Error while sending a message");
			e.printStackTrace();
		} finally {
			client.close();
		}
		return responsebody;
	}

	
	
	
	/**
	 * Two methods found on Stack Overflow to get the body of an HttpResponse. 
	 * Seems to be the easiest way to do it.
	 * The tricky part is to get the proper charset and turn the InputStream into a String.
	 */
	
	private static String getResponseBody(HttpResponse response) {
		String response_text = null;
		HttpEntity entity = null;
		try {
			entity = response.getEntity();
			response_text = _getResponseBody(entity);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			if (entity != null) {
				try {
					entity.consumeContent();
				} catch (IOException e1) {
				}
			}
		}
		return response_text;
	}

	
	private static String _getResponseBody(final HttpEntity entity) throws IOException, ParseException {
		if (entity == null) { throw new IllegalArgumentException("HTTP entity may not be null"); }
		InputStream instream = entity.getContent();
		if (instream == null) { return ""; }
		if (entity.getContentLength() > Integer.MAX_VALUE) { throw new IllegalArgumentException(
				"HTTP entity too large to be buffered in memory"); }
		String charset = EntityUtils.getContentCharSet(entity);
		if (charset == null) {
			charset = HTTP.DEFAULT_CONTENT_CHARSET;
		}
		Reader reader = new InputStreamReader(instream, charset);
		StringBuilder buffer = new StringBuilder();
		try {
			char[] tmp = new char[1024];
			int l;
			while ((l = reader.read(tmp)) != -1) {
				buffer.append(tmp, 0, l);
			}
		} finally {
			reader.close();
		}
		return buffer.toString();
	}

	
}
