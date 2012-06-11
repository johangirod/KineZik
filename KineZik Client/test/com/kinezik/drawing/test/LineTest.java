package com.kinezik.drawing.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.junit.Test;

import com.kinezik.drawing.Line;
import com.kinezik.drawing.Point;

public class LineTest extends TestCase  {

	@Test
	public void testLine() {
		Line MyLine = new Line(0);
		Line Circle = new Line(0);
		Point p1 =new Point ((float)1.0 ,(float)2.0,(float)100.0,1);
		Point p2 =new Point ((float)4.0 ,(float)2.0,(float)100.0,2);
		Point p3 =new Point ((float)10.0 ,(float)2.0,(float)100.0,3);
		MyLine.add(p1); 
		MyLine.add(p2);
		MyLine.add(p3);
		double epsilon =10e-40;
		//assertEquals(9, MyLine.getLength(), epsilon);
		//assertEquals(6, MyLine.getMaxDistance(), epsilon);
	//	assertEquals(3,MyLine.getAvgDistance());
		
		double teta = Math.PI/180 ; 
		Point  P; 
		float R =1;
		float x , y ;
		for (int i=0 ;i<360; i++ ) {
			x = (float) (R*Math.cos(i*teta)) ;
		    y=(float)(R*Math.sin(i*teta));
			P = new Point(x,y,0,5000*i+2326);
	        Circle.add(P);		
		}	

		
   //System.out.println(Circle.getRc());
  // System.out.println(MyLine.getRc());
   

}
}