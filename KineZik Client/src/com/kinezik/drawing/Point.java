package com.kinezik.drawing;

public class Point {
	public float x;
	public float y;
	public float pressure;
	public long time;
	
	public Point(float x, float y, float pressure, long time){
		this.x = x;
		this.y = y;
		this.pressure = pressure;
		this.time = time;
	}
	
	public float distanceTo(Point a){
		return (float) Math.sqrt(Math.pow(this.x-a.x, 2) + Math.pow(this.y- a.y, 2));
	}
	
	/**returns zero if both points have the same time*/
	public double speedTo(Point p){
		if(this.time != p.time){
			return (long)(10*this.distanceTo(p))/(this.time - p.time);
		} else {
			return 0;
		}
	}
		
	public Point getDerive (Point p) {
		long delta = this.time - p.time;
		return (new Point( (this.x - p.x)/delta, (this.y - p.y)/delta, 0, this.time));
		}
	}

