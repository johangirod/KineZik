package modele;



import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;


/**
 * Classe representant une chanson identifiée grâce à l'artiste, le nom et l'album, ainsi que son évaluation
 * @author johan
 *
 */
public class Song implements JSONString{

	protected String title;
	protected String artist;
	protected String album;
	private String genre;
	private Long id;

	private HashMap<Integer,Float> evaluations;

	/**Null values are allowed but discouraged since they will cause an error when calling toJSONString.
	 */
	public Song(String artist, String album, String title){
		this.album = album;
		this.artist = artist;
		this.title = title;
		this.evaluations = new HashMap<Integer,Float>();
	}
	
	/**This constructor will throw JSONException if a JSON key is missing, which happens if the original song object has
	 * null in one of its fields.
	 * 
	 * @param JSONString
	 * @throws JSONException
	 */
	public Song(String JSONString) throws JSONException {
		JSONObject value = new JSONObject(JSONString);
		this.title = value.getString("title");	
		this.artist = value.getString("artist");
		this.album =value.getString("album");
		this.genre = value.getString("genre");
		this.setId((long) value.optInt("id"));

		this.evaluations = new HashMap<Integer,Float>();
		if(value.getJSONObject("evaluators") != null){
			JSONObject valueMap= value.getJSONObject("evaluators");
			Iterator <String>  it =valueMap.keys();
			String next ;
			float val;
			while (it.hasNext()) {
				next = it.next();	
				val =(float)valueMap.getDouble(next);
				this.addEvaluations(Integer.parseInt(next), val);
			}
		}

	}

	public String getTitle(){
		return this.title;
	}

	public String getArtist(){
		return this.artist;
	}	

	public String getAlbum(){
		return this.album;
	}

	public void setGenre( String genre){
		this.genre=genre;
	}
	
	public String getGenre() {
		return genre ;
	}


	/**
	 * @param id : the song's id in the server database 
	 */
	public void setId(Long id){
		this.id = id;
	}

	public Long getId(){
		return this.id;
	}


	/**
	 * Ajoute l'évaluation identifiée par id de valeur evals pour cette chanson. 
	 * Précondition : evals est entre 0.0 et 1.0
	 * @param id  id de l'évaluateur (et donc de l'évaluation)
	 * @param evals  évaluation de l'évaluateur d'id id pour cette chanson (doit
	 * être compris entre 0 et 1)
	 * @throws IllegalArgumentException si evals n'est pas compris dans [0..1]
	 */
	public void addEvaluations(int id, float evals) {
		if ( evals < 0.0 || evals > 1.0 ) {
			throw new IllegalArgumentException("evals not in range [0..1]: " + evals);
		}
		this.evaluations.put(new Integer(id), new Float(evals));
	}
	
	public Float getEvaluation(Integer id){
		return this.evaluations.get(id);
	}

	/**
	 * Efface toutes les évaluations de cette chanson
	 * @return 
	 */
	public void clearEvaluations(){
		this.evaluations.clear();
	}


	public Iterator<Map.Entry<Integer, Float>> evaluationIterator(){
		return this.evaluations.entrySet().iterator();
	}

	
	

	@Override
	public String toJSONString() {
		String result = null;
		JSONObject json = new JSONObject ();

		try {
			json.put("title", this.title);
	
		json.put("artist", this.artist);
		json.put("album", this.album);
		json.put("id", this.id);
		json.put("genre", this.genre);

		JSONObject jsonEvaluators = new JSONObject();
		Iterator<Integer>  it = evaluations.keySet().iterator();

		int next ;
		while (it.hasNext()){
			next = it.next();
			jsonEvaluators.put(((Integer) next).toString(), evaluations.get(next));
		}

		json.put("evaluators", jsonEvaluators);

		result = json.toString();
		} catch (JSONException e) {
			return null;
		}
		return result;
	}

	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		Song song = (Song) obj;
		
		//Check infos match and avoid NUllPointerException
		boolean res = true;
		res &= ((this.title == null) && (song.title == null)) 
				|| ((this.title != null) && this.title.equals(song.title));
		res &= ((this.artist == null) && (song.artist == null)) 
				|| ((this.artist != null) && this.artist.equals(song.artist));
		res &= ((this.album == null) && (song.album == null)) 
				|| ((this.album != null) && this.album.equals(song.album));
		
		//This works because the equals method for Map is properly implemented
		res &= ((this.evaluations == null) && (song.evaluations == null)) 
				|| ((this.evaluations != null) && this.evaluations.equals(song.evaluations));
		
		return res;
	}
	
	/**
	 * Same as equals but only comparing Title, Album and Artist
	 */
	public boolean corresponds(Song song){
		boolean res = true;
		res &= ((this.title == null) && (song.title == null)) 
				|| ((this.title != null) && this.title.equals(song.title));
		res &= ((this.artist == null) && (song.artist == null)) 
				|| ((this.artist != null) && this.artist.equals(song.artist));
		res &= ((this.album == null) && (song.album == null)) 
				|| ((this.album != null) && this.album.equals(song.album));
		return res;
	}

}
