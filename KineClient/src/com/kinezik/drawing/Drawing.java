package com.kinezik.drawing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Drawing {
	
	private static final float REFERENCE_SPEED = (float) 0.55;
	private static final float PEAK_THRESHOLD = (float) 0.35;
	private static final float REFERENCE_SURFACE = (float) 0.5;
	
	List<Line> dessin;
	Line traitCourant;
	Paint peinture;
	int canvasSurface;
	
	public Drawing(int canvasHeight, int canvasWidth){
		dessin = new ArrayList<Line>();
		canvasSurface = canvasHeight*canvasWidth;
		nouveauTrait();
		peinture = new Paint();
        peinture.setColor(Color.BLACK);
        peinture.setAntiAlias(true);
	}
	
	public void nouveauTrait(){
		traitCourant = new Line(PEAK_THRESHOLD);
        dessin.add(traitCourant);
	}
	
	public void add(float x, float y, float pressure, long time){
		traitCourant.add(new Point(x, y, pressure, time));
	}
	
	public void draw(Canvas canvas){
		Iterator<Line> iterTrait = dessin.iterator();
    	while(iterTrait.hasNext()){
    		Iterator<Point> iterPoint = iterTrait.next().iterator();
    		if(iterPoint.hasNext()){
    			Point pointCour = iterPoint.next();
    			canvas.drawCircle(pointCour.x, pointCour.y, 1, peinture);
    			while(iterPoint.hasNext()){
    				Point pointPrec = pointCour;
    				pointCour = iterPoint.next();
    				canvas.drawLine(pointPrec.x, pointPrec.y, pointCour.x, pointCour.y, peinture);
    			}
    		}
    	}
	}

	
	 //////////////////////
	// Analysis methods //
   //////////////////////
	private float getBarraquandFormula(float nPlus, float nMoins){
		return  (float) ((1+nPlus)/(2+nPlus+nMoins));
	}
	

	public float getTotalLength(){
		Iterator<Line> iter = dessin.iterator();
		float res = 0;
		while(iter.hasNext()){
			res+=iter.next().getLength();
		}
		return res;
	}
	/** Retourne un indicateur sur le rayon de courbure */
	public float getDesc1(){
		return getBarraquandFormula(2*getNbPeak(), 0);
	}
	
	private float getNbPeak() {
		Iterator<Line> iter = dessin.iterator();
		float res = 0;
		while(iter.hasNext()){
			res+=iter.next().getNbPeaks();
		}
		return res;
	}
		
	

	/**Retourne un indicateur sur la vitesse moyenne */
	public float getDesc2(){
		float n=getAvgSpeed()- REFERENCE_SPEED;
		float nPlus= 7*Math.max(n,0);
		float nMoins= 7*Math.abs(Math.min(n,0));
		return getBarraquandFormula(nPlus, nMoins);
	}
	/**Retourne un indicateur sur l'occupation spatiale du dessin sur l'écran tactile */
	public float getDesc3(){
		Iterator<Line> iterTrait = dessin.iterator();
		float Xmin = 0, Ymin = 0, Xmax = 0, Ymax = 0;
		if (iterTrait.hasNext()) {
			Line current = iterTrait.next();
			Xmin = current.getXmin();
			Ymin = current.getYmin();
			Xmax = current.getXmax();
			Ymax = current.getYmax();
		}
		while(iterTrait.hasNext()){
			Line nextTrait = iterTrait.next();
			Xmin = Math.min(Xmin, nextTrait.getXmin());
			Ymin = Math.min(Ymin, nextTrait.getYmin());
			Xmax = Math.max(Xmax, nextTrait.getXmax());
			Ymax = Math.max(Ymax, nextTrait.getYmax());
		}

		float drawingSurface = (Xmax-Xmin)*(Ymax-Ymin)/canvasSurface;
		float nPlus = 7*Math.max(drawingSurface - REFERENCE_SURFACE, 0);
		float nMoins = 7*Math.abs(Math.min(drawingSurface - REFERENCE_SURFACE, 0));
	
		return getBarraquandFormula(nPlus, nMoins);
	}
	
	public float getAvgLineLength(){
		Iterator<Line> iter = dessin.iterator();
		float res = 0;
		float n = dessin.size();
		while(iter.hasNext()){
			res+=iter.next().getLength();
		}
		if (n != 0){
			return res/n;	
		} else {
			return 0;
		}
	}
	
	/**takes dots into accounts*/
	public int getLineCount(){
		return dessin.size();
	}
	
	public float getMaxSpeed(){
		Iterator<Line> iter = dessin.iterator();
		float max = 0;
		Line speedLine;
		while(iter.hasNext()){
			speedLine = iter.next().derive();
			if (speedLine.getMaxDistance() > max){
				max = speedLine.getMaxDistance();
			}
			
		}
		return max;
	}
	
	public float getAvgSpeed(){
		Iterator<Line> iter = dessin.iterator();
		float res = 0;
		float n = 0;
		Line speedLine;
		while(iter.hasNext()){
			speedLine = iter.next().derive();
			if (speedLine.getLength() > 0){
				res+=speedLine.getAvgDistance();
				n++;
			}
		}
		if (n != 0){
			return res/n;	
		} else {
			return 0;
		}
	}
	
	public float getMaxAcceleration(){
		Iterator<Line> iter = dessin.iterator();
		float max = 0;
		Line accLine;
		while(iter.hasNext()){
			accLine = iter.next().derive().derive();
			if (accLine.getMaxDistance() > max){
				max = accLine.getMaxDistance();
			}
			
		}
		return max;
	}
	
	public float getAvgAcceleration(){
		Iterator<Line> iter = dessin.iterator();
		float res = 0;
		float n = 0;
		Line accLine;
		while(iter.hasNext()){
			accLine = iter.next().derive().derive();
			if (accLine.getLength() > 0){
				res+=accLine.getAvgDistance();
				n++;
			}
		}
		if (n != 0){
			return res/n;	
		} else {
			return 0;
		}
	}
	
	/**returns the number of times an acceleration of at least 75% of the max acceleration was witnessed.*/
	public float getNbPeakAccelerations(){
		float max = this.getMaxAcceleration();
		float n = 0;
		Iterator<Line> iter = dessin.iterator();
		Line accLine;
		while(iter.hasNext()){
			accLine = iter.next().derive().derive();
			if (accLine.getMaxDistance() > (2*max)/4){
				n++;
			}
		}
		return n;
	}


	public void setCanvasSize(int canvasHeight, int canvasWidth){
		canvasSurface = canvasHeight*canvasWidth;
	}

}
