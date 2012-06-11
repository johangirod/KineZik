package com.kinezik.drawing.test;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import com.kinezik.drawing.Drawing;
import com.kinezik.drawing.Line;
import com.kinezik.drawing.Point;

public class DrawingTest extends TestCase{

	@Test
	public void test() {
		Drawing D1 = new Drawing (1, 1) ;
		
		Line Line1= new Line (0);
		Line Line2= new Line (0);
		
		Point P1= new Point ((float)3.0 ,(float)0.0,(float)100.0,1);
		Point P2= new Point ((float)0.0 ,(float)0.0,(float)100.0,2);
		
		Point P3= new Point ((float)0.0 ,(float)2.0,(float)100.0,3);
		Point P4= new Point ((float)0.0 ,(float)4.0,(float)100.0,4);
		
		Line1.add(P1);
		Line1.add(P2);
		
		Line2.add(P3);
		Line2.add(P4);
		
		//double epsilon =10e-40;
		System.out.println(D1.getTotalLength());
		//assertEquals(5 ,D1.getTotalLength(), epsilon);
		
	}

}
