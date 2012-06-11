package com.kinezik.bayesian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import android.util.Log;

import com.kinezik.music.LocalSong;
import com.kinezik.shared.BayesianTable;

public class DrawEvaluator {
	HashMap<Integer, Float> EvalDrawing;

	// Constructeur
	public DrawEvaluator(float desc1, float desc2, float desc3,
			HashMap<Integer, BayesianTable> hashBT) {
		if (desc1 < 0 || desc1 > 1 || desc2 < 0 || desc2 > 1 || desc3 < 0
				|| desc3 > 1) {
			throw new IllegalArgumentException("parameter must be in [0..1]");
		}
		assert(!hashBT.isEmpty());
		EvalDrawing = new HashMap<Integer, Float>();
		// Calcul des évaluateurs du dessin
		Iterator<Integer> iteEval = hashBT.keySet().iterator();
		while (iteEval.hasNext()) {
			int idEval = iteEval.next();
			EvalDrawing.put(idEval,
					hashBT.get(idEval).getEvalDessin(desc1, desc2, desc3));
		}
	}

	
	public TreeSet<LocalSong> getBestSong(ArrayList<LocalSong> listSong) {
		Log.d("TRACE", "sizeof listSong " + listSong.size());
		TreeSet<LocalSong> fitSongs = new TreeSet<LocalSong>();
		Iterator<LocalSong> iteSong = listSong.iterator();
		LocalSong curSong;
		while (iteSong.hasNext()) {
			curSong = iteSong.next();
			Iterator<Integer> iteEv = EvalDrawing.keySet().iterator();
			float evalSong = 0;
			while (iteEv.hasNext()) {
				int idEv = iteEv.next();
				// Evaluation du dessin
				Float evDraw = EvalDrawing.get(idEv);
				if (evDraw == null) {
					evDraw = (float) 0.5;
				}
				// Evaluation de la chanson
				Float evSong = curSong.getEvaluation(idEv);
				if (evSong == null) {
					evSong = (float) 0.5;
				}
				evalSong = evalSong + evSong * evDraw;
			}
			Log.d("TRACE", "DrawEvaluator : l'evaluation de la chanson " + curSong.toJSONString() + 
					" vaut " + evalSong);
			curSong.setFit(evalSong);
			fitSongs.add(curSong);
		}
		Log.d("TRACE", "fitSong.size " + fitSongs.size());
		return fitSongs;
	}
}
