package com.kinezik.shared;

import java.util.ArrayList;

import junit.framework.Assert;

import modele.JSONTranslator;
import modele.Song;

import org.json.JSONException;
import org.junit.Test;


/**These tests assume the Song class has been tested and approved.
 * 
 * @author victormours
 *
 */
public class JSONTranslatorTest {

	@Test
	public void testJSONToSongList() {
		try {
			JSONTranslator.JSONToSongList(null);
			Assert.fail("A null pointer exception should be raised for a null parameter");
		} catch (JSONException e) {
			Assert.fail("Wrong exception catched");
		} catch (NullPointerException e){
			//OK
		}
		
		ArrayList<Song> list = null;
		try {
			list = JSONTranslator.JSONToSongList("[]");
		} catch (JSONException e) {
			Assert.fail();
			e.printStackTrace();
		}
		Assert.assertNotNull(list);
		Assert.assertEquals(0, list.size());
		
		Song song = new Song("Artist", "Album", "Title");
		list = null;
		try {
			list = JSONTranslator.JSONToSongList("["+ song.toJSONString() +"]");
		} catch (JSONException e) {
			Assert.fail();
			e.printStackTrace();
		}
		Assert.assertNotNull(list);
		Assert.assertEquals(1, list.size());
		Assert.assertNotNull(list.get(0));
		Assert.assertTrue(song.equals(list.get(0)));
		
		song.addEvaluations( 0,(float) 0.0);
		list = null;
		try {
			list = JSONTranslator.JSONToSongList("["+ song.toJSONString() +"]");
		} catch (JSONException e) {
			Assert.fail();
			e.printStackTrace();
		}
		Assert.assertNotNull(list);
		Assert.assertEquals(1, list.size());
		Assert.assertTrue(song.equals(list.get(0)));
		
		list = null;
		for(int i = 1; i < 100; i++){
			song.addEvaluations(i, 1/i);
		}
		try {
			list = JSONTranslator.JSONToSongList("["+ song.toJSONString() +"]");
		} catch (JSONException e) {
			Assert.fail();
			e.printStackTrace();
		}
		Assert.assertNotNull(list);
		Assert.assertEquals(1, list.size());
		Assert.assertTrue(song.equals(list.get(0)));
	}
	
	@Test public void songHashmapTest(){
		String json = "[]";
		String res = null;
		try{
			res = JSONTranslator.SongListToJSON(JSONTranslator.JSONToSongList(json));
		} catch (JSONException e) {
			Assert.fail();
			e.printStackTrace();
		}
		Assert.assertEquals(json, res);
	}

}
