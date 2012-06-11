package com.kinezik.shared.test;

import java.util.ArrayList;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.Test;

import com.kinezik.shared.JSONTranslator;
import com.kinezik.shared.music.Song;

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
		try {
			list = JSONTranslator.JSONToSongList("["+ song.toJSONString() +"]");
		} catch (JSONException e) {
			Assert.fail();
			e.printStackTrace();
		}
		Assert.assertNotNull(list);
		Assert.assertEquals(1, list.size());
		Assert.assertEquals(song, list.get(0));
		
		song.addEvaluations( 0,(float) 0.0);
		try {
			JSONTranslator.JSONToSongList("["+ song.toJSONString() +"]");
		} catch (JSONException e) {
			Assert.fail();
			e.printStackTrace();
		}
		
		for(int i = 1; i < 100; i++){
			song.addEvaluations(i, 1/i);
		}
		try {
			JSONTranslator.JSONToSongList("["+ song.toJSONString() +"]");
		} catch (JSONException e) {
			Assert.fail();
			e.printStackTrace();
		}
	}

}
