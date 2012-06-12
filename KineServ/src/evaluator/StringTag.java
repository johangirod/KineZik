package evaluator;

import java.util.Iterator;

import entagged.audioformats.AudioFile;

public class StringTag{

	public static String getStringGenres(AudioFile mp3){
		String res = "";
		Iterator<?> ite = mp3.getTag().getGenre().iterator();
		while (ite.hasNext()){
			res.concat((String) ite.next().toString());
		}
		return res;
	}
	
}
