package com.kinezik.bayesian;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

import org.json.JSONException;

import android.content.Context;

import com.kinezik.R;
import com.kinezik.database.DatabaseOpenHelper;
import com.kinezik.shared.BayesianTable;
import com.kinezik.shared.JSONTranslator;
public class BayesianTranslator {

	private static final String JSONTable = "JSONTable";
	private static File JSON ;
	private DatabaseOpenHelper helper;
	
	/** lit le JSON dans un fichier et le traduit en hashmap 
	 * @throws IOException 
	 * @throws JSONException */
	public HashMap <Integer, BayesianTable>  getBayesianTable(Context context) throws IOException, JSONException {
		helper  = new DatabaseOpenHelper(context);
		
		HashMap<Integer,BayesianTable> tables = new HashMap<Integer,BayesianTable>();
		FileInputStream instream ;
		if (JSON.exists()) {
			instream = new FileInputStream(JSONTable);}
		else {
			JSON = new File(context.getFilesDir(),JSONTable );
			FileOutputStream out = new FileOutputStream(JSON);
			out.write(context.getResources().getString(R.string.Tables).getBytes());
			out.close();
			instream = new FileInputStream(JSONTable);
		}
		Reader reader = new InputStreamReader(instream);
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
		tables=JSONTranslator.JSONToTableMap(buffer.toString());
		return tables;
	}

	/**stocke les tables bayesiennes dans un fichier 
	 * @throws IOException */
	public static void storeBayesianTables (String tables, Context context) throws IOException {
		JSON = new File(context.getFilesDir(),JSONTable );
		FileOutputStream out ;
		out = new FileOutputStream(JSON); 
		out.write(tables.getBytes());
		out.close();
		
		
//		if (!JSON.exists()){ 
//			JSON = new File(context.getFilesDir(),JSONTable );
//		}

	}
}
