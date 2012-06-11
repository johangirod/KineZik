package com.kinezik.database;

import com.kinezik.R;
import com.kinezik.shared.StringTransfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

	private static final String DATABASE_TABLE_NAME = "kinezik";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_TABLE_CREATE_SONGS = "CREATE TABLE Songs ( " +
															"_id INTEGER PRIMARY KEY, " +
															"Artist TEXT NOT NULL," +
															"Album TEXT NOT NULL," +
															"Title TEXT NOT NULL," +
															"Uri TEXT," +
															"UNIQUE (Artist, Album, Title));";
	
	
	private static final String DATABASE_TABLE_CREATE_EVALUATIONS = "CREATE TABLE Evaluations ( " +
																	"idSong INTEGER," +
																	"idEval INTEGER," +
																	"Evaluation REAL," +
																	"FOREIGN KEY(idSong) REFERENCES Songs(_id)" +
																	");";
	
	private static final String DATABASE_TABLE_CREATE_BAYESIANS = "CREATE TABLE BayesianTables (" +
																 "id INTEGER PRIMARY KEY," +
																 "JsonContent TEXT);"; 
	
	
	private Context context;
	
	public DatabaseOpenHelper(Context context){
		super(context, DATABASE_TABLE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("DEBUG", "Creating database");
		db.execSQL(DATABASE_TABLE_CREATE_SONGS);
		db.execSQL(DATABASE_TABLE_CREATE_EVALUATIONS);
		db.execSQL(DATABASE_TABLE_CREATE_BAYESIANS);
		ContentValues val = new ContentValues();

		Log.d("TRACE", "JSON String at the init : " +context.getResources().getString(R.string.Tables));
		val.put("JsonContent", (context.getResources().getString(R.string.Tables)));
		db.insert("BayesianTables", null, val);
	}

	@Override
	public void onUpgrade(SQLiteDatabase bd, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
