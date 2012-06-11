package evaluator;

import java.util.HashSet;
import java.util.Iterator;

public class EvaluatorSet {

	private Integer nbEvaluator = 0;
	private static HashSet<Evaluator> evaluators = new HashSet<Evaluator>();
	
	public Iterator<Evaluator>  getIteEval(){
		return EvaluatorSet.evaluators.iterator();
	}
	
	public void addEvaluator(Evaluator eval){
		nbEvaluator ++;
		eval.setId(nbEvaluator);
		evaluators.add(eval);
	}
	
	public void evaluateSong(ServerSong song){
		Iterator<Evaluator> iteEval = EvaluatorSet.evaluators.iterator();
		while(iteEval.hasNext()){
			Evaluator evaluator = iteEval.next();
			song.addEvaluations(evaluator.getId(), evaluator.evaluate(song.getMp3File()));
		}
	}
}
