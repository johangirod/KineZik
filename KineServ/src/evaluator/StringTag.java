package evaluator;

import entagged.audioformats.AudioFile;

public class StringTag{

	public static String getStringGenres(AudioFile mp3){
		String res = mp3.getTag().getFirstGenre();
		res.replace("(", "");
		res.replace(")", "");
		if (res.length() != 0 &&  res.matches("[0-9]*")){
			Integer genreInt = Integer.decode(res);
			if (genreInt < entagged.audioformats.Tag.DEFAULT_GENRES.length) {	
				res = entagged.audioformats.Tag.DEFAULT_GENRES[genreInt];
			}
		}
		return res;
	}

}
