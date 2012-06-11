/**
 * 
 */
package com.kinezik.bayesian;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * @author root
 *
 */
public class SongOpenHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "KineZikData";
	private static final int DATABASE_VERSION = 1;
	
	
    private static final String EVALUATION_TABLE_NAME = "evaluation";
    private static final String EVALUATION_TABLE_CREATE =
            "CREATE TABLE " + EVALUATION_TABLE_NAME + " (" +
            "idSong INTEGER," +
            "idEval INTEGER," +
            "evaluation FLOAT);";

	
	public SongOpenHelper(Context context) {
		super(context,  DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		  db.execSQL(EVALUATION_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
