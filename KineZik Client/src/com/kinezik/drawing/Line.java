package com.kinezik.drawing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents lines drawn by the user on the tactile screen. Lines made of a single point are called dots.
 * @author victormours
 *
 */
public class Line {
	
	private final float peakThreshold;
	private List<Point> points;
	
	
	public Line(float peakThreshold) {
		this.peakThreshold = peakThreshold;
		points = new ArrayList<Point>();
	}
	
	public Iterator<Point> iterator(){
		return points.iterator();
	}
	
	public void add(Point p){
		points.add(p);
	}
	
	public int getSize(){
		return points.size();
	}
	
	/**Renvoie la longueur, en pixels, du trait*/
	public float getLength(){
		Iterator<Point> ite = points.iterator();
		float res = 0;
		Point a, b;
		if(ite.hasNext()){
			a = ite.next();
			while(ite.hasNext()){
				b = ite.next();
				res+= a.distanceTo(b);
				a = b;
			}
		}
		return res;
	}
	/**Renvoie la distance maximale entre deux points successifs de la ligne*/
	public float getMaxDistance(){
		Iterator<Point> ite = points.iterator();
		float max = 0;
		Point a, b;
		if(ite.hasNext()){
			a = ite.next();
			while(ite.hasNext()){
				b = ite.next();
				if ( max < a.distanceTo(b)){
					max = a.distanceTo(b);
				}
				a = b;
			}
		}
		return max;
	}

	public float getAvgDistance(){
		if (points.size() > 0){
			return this.getLength()/points.size();
		} else {
			return 0;
		}
	}
	
	
	public Line derive(){
		if(points.size() < 2){
			return this;
		} else {
			Line derived = new Line(0);
			Iterator<Point> ite = points.iterator();
			Point a, b;
			if(ite.hasNext()){
				a = ite.next();
				while(ite.hasNext()){
					b = ite.next();
					long delta = b.time - a.time;
					derived.add(new Point( (b.x - a.x)/delta, (b.y - a.y)/delta, 0, (a.time+b.time)/2));
					a = b;
				}
			}
			return derived;
		}
	}
	
	public float getXmin() {
		Iterator<Point> ite = points.iterator();
		float Xmin=0;
		if (ite.hasNext()) { 
			 Xmin=ite.next().x; }
		while (ite.hasNext()){
			Xmin = Math.min(Xmin, ite.next().x);
		}
		return Xmin;
	}
	
	
	public float getXmax() {
		Iterator<Point> ite = points.iterator();
		float Xmax=0;
		if (ite.hasNext()) {
		Xmax=ite.next().x;}
		while (ite.hasNext()){
			Xmax = Math.max(Xmax, ite.next().x);
		}
		return Xmax;
	}
	
	public float getYmin() {
		Iterator<Point> ite = points.iterator();
		float Ymin=0;
		if (ite.hasNext()) {
		  Ymin=ite.next().y;}
		while (ite.hasNext()){
			Ymin = Math.min(Ymin, ite.next().y);
		}
		return Ymin;
	}
	
	public float getYmax() {
		Iterator<Point> ite = points.iterator();
		float Ymax=0;
		if (ite.hasNext()) {
		  Ymax=ite.next().y;}
		while (ite.hasNext()){
			Ymax = Math.max(Ymax, ite.next().y);
		}
		return Ymax;
	}

	

	public int getNbPeaks() {
		Iterator<Point> ite = points.iterator();
		float dx=0, dy=0, ddx=0, ddy=0 ; 
		double num, denum;
		float Rc = 0;
		Point nextPoint1, nextPoint2 = null, nextPoint3=null;
		int count = 0;
		boolean inPeak = false;
		if (ite.hasNext()) {
			nextPoint2=ite.next();}
		if(ite.hasNext()) {
		    nextPoint3=ite.next();}
		
		while (ite.hasNext()){
			nextPoint1 = nextPoint2;
			nextPoint2=nextPoint3;
			nextPoint3=ite.next();
			
			dx= nextPoint1.getDerive(nextPoint2).x;
			dy= nextPoint1.getDerive(nextPoint2).y;
			
			ddx= nextPoint1.getDerive(nextPoint2).getDerive(nextPoint2.getDerive(nextPoint3)).x;
			ddy= nextPoint1.getDerive(nextPoint2).getDerive(nextPoint2.getDerive(nextPoint3)).y;
		
			num=Math.pow( (Math.pow(dx, 2) + Math.pow(dy,2)) , 1.5) ;
			denum= (dx*ddy - dy*ddx);
			Rc= (float) Math.abs(num/denum);
			
			if(Rc < peakThreshold){
				count++;
				inPeak = true;
			}
			if(inPeak && Rc > peakThreshold*1.2){
				inPeak = false;
			}
		}
		
		return count;
		
	}
}
