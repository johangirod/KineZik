package com.example.helloandroid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Debug;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HelloAndroid extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        postData(this.getCurrentFocus());
        
    }

    
    


    
    public void postData(View view) {
        // Create a new HttpClient and Post Header 

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://192.168.0.10:8080/KineServ/GetSession");

            // Add your data
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("UUID", "123456789012345678901234567890ab"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
        } catch (ClientProtocolException e) {
        	AlertDialog alertDialog;
        	alertDialog = new AlertDialog.Builder(this).create();
        	alertDialog.setTitle("Erreur CE exception");
        	alertDialog.setMessage(e.getMessage());
        	alertDialog.show();
        	
            // TODO Auto-generated catch block
        } catch (IOException e) {
        	AlertDialog alertDialog;
        	alertDialog = new AlertDialog.Builder(this).create();
        	alertDialog.setTitle("Erreur exception");
        	alertDialog.setMessage(e.getMessage());
        	alertDialog.show();
        } 


    } 
    
}