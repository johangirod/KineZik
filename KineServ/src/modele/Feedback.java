package modele;

import java.util.HashSet;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

public class Feedback implements JSONString {
	
	public static final int THUMB_DOWN = 0;
	public static final int THUMB_UP = 1;
	
	private long idSong;
	private HashSet<Integer> evaluator = new HashSet<Integer>();
	
	private float desc1, desc2, desc3;
	private long time;
	
	private int action;
	
	public Feedback(){};
	
	public Feedback(String JSONFeedback) throws JSONException{
		JSONObject value = new JSONObject(JSONFeedback);
		this.setIdSong( value.optLong("idSong"));
		this.setTime(value.optLong("time"));
		this.setAction(value.optInt("action"));
	    this.setDesc1((float)value.optDouble("desc1"));
	    this.setDesc2((float)value.optDouble("desc2"));
	    this.setDesc3((float)value.optDouble("desc3"));
	    this.evaluator= new HashSet<Integer>();
	    if(value.getJSONObject("evaluator") != null){
	    	JSONObject valueSet= value.getJSONObject("evaluator");
	    	Iterator <String>  it =valueSet.keys();
			String next ;
			int val;
			while (it.hasNext()) {
				next = it.next();
				val =valueSet.getInt(next);
				evaluator.add(val);
	    }
		
	}
	}
	/**
	 * Add an evaluator used for the song
	 * @param evaluator
	 */
	public void addEvaluator(int evaluator){
		this.evaluator.add(evaluator);
	}
	
	/**
	 * @return an iterator on the Evaluator
	 */
	public Iterator<Integer> iteratorEval(){
		return this.evaluator.iterator();
	}
	
	
	/**
	 * @return the idSong
	 */
	public long getIdSong() {
		return idSong;
	}
	/**
	 * @param idSong the idSong to set
	 */
	public void setIdSong(long idSong) {
		this.idSong = idSong;
	}
	/**
	 * @return the desc1
	 */
	public float getDesc1() {
		return desc1;
	}
	/**
	 * @param desc1 the desc1 to set
	 */
	public void setDesc1(float desc1) {
		this.desc1 = desc1;
	}
	/**
	 * @return the desc2
	 */
	public float getDesc2() {
		return desc2;
	}
	/**
	 * @param desc2 the desc2 to set
	 */
	public void setDesc2(float desc2) {
		this.desc2 = desc2;
	}
	/**
	 * @return the desc3
	 */
	public float getDesc3() {
		return desc3;
	}
	/**
	 * @param desc3 the desc3 to set
	 */
	public void setDesc3(float desc3) {
		this.desc3 = desc3;
	}
	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public String toJSONString() {
		
		JSONObject feedback = new JSONObject();
		String result = null ;
		int i=1;
		try {
			feedback.put("desc1", this.desc1);
			feedback.put("desc2", this.desc2);
			feedback.put("desc3", this.desc3);
			feedback.put("idSong", this.idSong);
			feedback.put("time", this.time);
			feedback.put("action", this.action);
			
			JSONObject jsonEvaluators = new JSONObject();
			Iterator<Integer> it = evaluator.iterator();
			while (it.hasNext()) {
				jsonEvaluators.put("Evaluator"+String.valueOf(i),it.next());
				i=i+1;
			}
			feedback.put("evaluator", jsonEvaluators);
			result= feedback.toString();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @return the action
	 */
	public int getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(int action) {
		this.action = action;
	}
	
	
	
	
}
