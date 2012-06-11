package modele;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JSONTranslator {

	/**The json string is expected to be an array of Song objects. The array may be empty but it should not be null.
	 * 
	 * @param json
	 * @return
	 */
	public static ArrayList<Song> JSONToSongList(String json) throws JSONException{
		JSONArray array = new JSONArray(json);
		ArrayList<Song> res = new ArrayList<Song>();
		for(int i = 0; i < array.length(); i++){
			res.add(new Song(array.getJSONObject(i).toString()));
		}
		return res;
	}
	
	public static String SongListToJSON(ArrayList<Song> list){
		JSONArray res = new JSONArray();
		Iterator<Song> iter = list.iterator();
		while(iter.hasNext()){
			res.put(iter.next().toJSONString());
		}
		return  res.toString();
	}
	
	/**The json is expected to be a sequence of key-value pairs
	 * 
	 */
	public static HashMap<Integer,BayesianTable> JSONToTableMap(String json) throws JSONException{
		HashMap<Integer,BayesianTable> tables = new HashMap<Integer,BayesianTable>();
		JSONObject list = new JSONObject(json);
		Iterator<?> iter = list.keys();
		while(iter.hasNext()){
			String key =  (String) iter.next();
			tables.put(Integer.parseInt(key), new BayesianTable(list.getJSONObject(key).toString()));
		}
		return tables;
	}
	
	public static String getJSONFromTables(HashMap<Integer,BayesianTable> tables) throws JSONException{
		JSONObject list = new JSONObject();
		Iterator<Map.Entry<Integer, BayesianTable>> iter = tables.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry<Integer, BayesianTable> current = iter.next();
			list.put(JSONObject.numberToString(current.getKey()), current.getValue().toJSONString());
		}
		return list.toString();
	}
}
