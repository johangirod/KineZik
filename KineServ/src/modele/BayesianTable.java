package modele;


import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;


public class BayesianTable implements Serializable, JSONString {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6218749808953349606L;
	private int idEval;
	public float[][][] bayesMat;

	public BayesianTable(int Descriptor1Length, int Descriptor2Length,
			int Descriptor3Length, int idEval) {
		bayesMat = new float[Descriptor1Length][Descriptor2Length][Descriptor3Length];
		this.idEval = idEval;
	}
	
	public BayesianTable(String JSONString) throws JSONException{
		JSONObject json = new JSONObject(JSONString);
		idEval = json.getInt("id");
		JSONArray table0, table1, table2;
		table0 = json.getJSONArray("data");
		bayesMat = new float[table0.length()][][];
		for(int i = 0; i < table0.length(); i++){
			table1 = table0.getJSONArray(i);
			bayesMat[i] = new float[table1.length()][];
			for(int j = 0; j < table1.length(); j++){
				table2 = table1.getJSONArray(j);
				bayesMat[i][j] = new float[table2.length()];
				for(int k = 0; k < table2.length(); k++){
					bayesMat[i][j][k] = (float) table2.getDouble(k);
				}
			}
		}
	}

	public int getIdEval() {
		return idEval;
	}

	@Override
	public String toJSONString() {
		try{
			JSONObject json = new JSONObject();
			json.put("id", idEval);
			JSONArray table0, table1, table2;
			table0 = new JSONArray();
			for(int i = 0; i < bayesMat.length; i++){
				table1 = new JSONArray();
				for(int j = 0; j < bayesMat[i].length; j++){
					table2 = new JSONArray();
					for(int k = 0; k < bayesMat[i][j].length; k++){
						table2.put((double) bayesMat[i][j][k]); 
					}
					table1.put(table2);
				}
				table0.put(table1);
			}
			json.put("data", table0);
			return json.toString();
		} catch(JSONException e){
			return null;
		}
	
	}
	
	public float getEvalDessin(Float desc1, Float desc2, Float desc3){
		Integer index1 =  (int) (desc1*this.bayesMat.length);
		Integer index2 =  (int) (desc2*this.bayesMat[0].length);
		Integer index3 =  (int) (desc3*this.bayesMat[0][0].length);
		return this.bayesMat[index1][index2][index3];
		}
}
