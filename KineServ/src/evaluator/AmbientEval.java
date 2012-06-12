package evaluator;

import java.io.File;

import modele.BayesianTable;
import entagged.audioformats.AudioFile;
import entagged.audioformats.AudioFileIO;
import entagged.audioformats.exceptions.CannotReadException;

public class AmbientEval implements Evaluator {

	private static final String name = "Ambient Evaluator";
	private static int id;
	private static BayesianTable BT;


	@Override
	public float evaluate(File mp3File) {
		float value = 0;
		AudioFile mp3;
		try {
			mp3 = AudioFileIO.read(mp3File);
			String res = StringTag.getStringGenres(mp3);
			System.out.println("GENRE : "+ res);
			if (res.contains("Ambient") || 
					res.contains("Ambient") ) 
			{
				value = 1;
			}
		} catch (CannotReadException e) {
			// TODO Auto-generated catch block
			value = (float) 0.5;
		}
		return value;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setId(int id) {
		AmbientEval.id = id;
		// CREATE THE BAYESIAN TABLE
		BayesianTable BT = new BayesianTable(5,5,5, id);
		for(int i = 0 ; i<5 ; i++){
			for (int j = 0; j<5; j++ ){
				for (int k = 0; k<5; k++){
					BT.bayesMat[i][j][k] = ((float) 0.5);
				}
			}
		}
		AmbientEval.BT = BT;
	}

	@Override
	public BayesianTable getBayesTable() {
		return AmbientEval.BT;
	}

}
