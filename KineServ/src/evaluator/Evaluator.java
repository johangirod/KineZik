package evaluator;

import modele.BayesianTable;

public interface Evaluator {
	/**
	 * Return the evaluation of the song for this evaluator
	 * @param mp3File
	 * @return the evaluation
	 */
	float evaluate(java.io.File mp3File);
	
	/**
	 * 
	 * @return
	 */
	int getId();
	
	/**
	 * 
	 * @return
	 */
	String getName();
	
	/**
	 * 
	 */
	void setId(int id);
	
	/**
	 * 
	 * @return
	 */
	BayesianTable getBayesTable();
	
}
