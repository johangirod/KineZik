package com.kinezik.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FeedbackDAO {
	private DatabaseOpenHelper helper;
	
	public FeedbackDAO(Context context){
		helper  = new DatabaseOpenHelper(context);
	}

	/**
	 * @return all the feedbacks in DataBase as JSON strings
	 */
	public ArrayList<String> getFeedback(){
		ArrayList<String> feedbacks = new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT * FROM Feedbacks;", null);
		if(cur != null && cur.moveToFirst()){
			do{
				feedbacks.add(cur.getString(0));
			} while(cur.moveToNext());
		}
		db.rawQuery("DELETE FROM Feedbacks;", null);
		db.close();
		return feedbacks;
	}
	
	/**
	 * @param feedback to save in data base
	 */
	public void storeFeedback(String feedback){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues val = new ContentValues();
		val.put("Feedback", feedback);
		db.insert("Feedbacks", null, val);
		db.close();
	}
	
}
