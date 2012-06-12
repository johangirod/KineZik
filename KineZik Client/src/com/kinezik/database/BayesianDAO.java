package com.kinezik.database;

import java.util.HashMap;

import org.json.JSONException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kinezik.shared.BayesianTable;
import com.kinezik.shared.JSONTranslator;

public class BayesianDAO {

	private DatabaseOpenHelper helper;

	public BayesianDAO(Context context){
		helper  = new DatabaseOpenHelper(context);
	}

	public HashMap <Integer, BayesianTable>  getBayesianTable() throws JSONException {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT * FROM BayesianTables;", null);

		//The database contains a single entry representing a hashmap of bayesian tables as a json string
		if(cur != null && cur.moveToFirst()){
			db.close();
			Log.d("TRACE", "JSON String : " + cur.getString(1));
			return JSONTranslator.JSONToTableMap(cur.getString(1));
		}
		db.close();
		return null;
	}

	public void storeBayesianTables (String tables) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("BayesianTables", null, null);

		Cursor cur = db.rawQuery("SELECT * FROM BayesianTables;", null);
		if(cur != null && cur.moveToFirst()){
			db.close();
			Log.d("DEBUG", "IL Y A QQCH DANS BAYES TABLE APRES SUPPRESSION TOTALE !"  + cur.getString(1));
			cur.close();
		} else {

			Log.d("DEBUG", "C'est bon il y a rien");

			ContentValues val = new ContentValues();
			val.put("JsonContent", tables);
			db.insert("BayesianTables", null, val);
			cur = db.rawQuery("SELECT * FROM BayesianTables;", null);
			if(cur != null && cur.moveToFirst()){
				db.close();
				Log.d("DEBUG", "OKBAYES"  + cur.getString(1));

			} else {

				Log.d("DEBUG", "PASOKBAYES"  );
			}
			db.close();
		}
	}
}
