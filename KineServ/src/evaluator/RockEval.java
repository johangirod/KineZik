package evaluator;

import java.io.File;

import modele.BayesianTable;
import entagged.audioformats.AudioFile;
import entagged.audioformats.AudioFileIO;
import entagged.audioformats.exceptions.CannotReadException;

public class RockEval implements Evaluator {

	private static final String name = "Indie Evaluator";
	private static int id;
	private static BayesianTable BT;

	public RockEval(){

	}

	@Override
	public float evaluate(File mp3File) {
		float value = 0;
		AudioFile mp3;
		try {
			mp3 = AudioFileIO.read(mp3File);
			System.out.println("GENRE : "+ mp3.getTag().getFirstGenre());
			if (mp3.getTag().getFirstGenre().contains("Rock") || 
					mp3.getTag().getFirstGenre().contains("rock") ) 
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
		RockEval.id = id;
		// CREATE THE BAYESIAN TABLE
		BayesianTable BT = new BayesianTable(5,5,5, id);
		for(int i = 0 ; i<5 ; i++){
			for (int j = 0; j<5; j++ ){
				for (int k = 0; k<5; k++){
					BT.bayesMat[i][j][k] = ((float) (i)*(j))/25;
				}
			}
		}
		RockEval.BT = BT;
	}

	@Override
	public BayesianTable getBayesTable() {
		return RockEval.BT;
	}

}
