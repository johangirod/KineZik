package evaluator;

import java.io.File;

import modele.BayesianTable;
import entagged.audioformats.AudioFile;
import entagged.audioformats.AudioFileIO;
import entagged.audioformats.exceptions.CannotReadException;


public class DurationEval implements Evaluator {

	private static final String name = "Duration Evaluator";
	private static int id;
	private static BayesianTable BT;


	public float evaluate(File mp3File) {
		float value = 0;
		try {
			AudioFile mp3 = AudioFileIO.read(mp3File);
			value = (mp3.getPreciseLength() - 1*60)/(6*60);
			if (value>1){ value = 1;}
			if (value<0){ value = 0;}
		} catch (CannotReadException e) {
			value = (float) 0.5;
		}
		return value;
	}
	
	public int getId() {
		return id;
	}

	
	public String getName() {
		return name;
	}

	
	public void setId(int id) {
		DurationEval.id = id;
		// CREATE THE BAYESIAN TABLE
		BayesianTable BT = new BayesianTable(5,5,5, id);
		for(int i = 0 ; i<5 ; i++){
			for (int j = 0; j<5; j++ ){
				for (int k = 0; k<5; k++){
					BT.bayesMat[i][j][k] = ((float) (5-k))/5;
				}
			}
		}
		DurationEval.BT = BT;
	}

	
	public BayesianTable getBayesTable() {
		return DurationEval.BT;
	}


}
