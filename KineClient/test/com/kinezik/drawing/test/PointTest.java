package com.kinezik.drawing.test;

import static org.junit.Assert.*;
import junit.framework.TestCase;
import com.kinezik.drawing.Point;

import org.junit.Test;



public class PointTest extends TestCase  {

	@Test
	public void testPoint() {
		Point MyPoint1 =new Point ((float)3.0 ,(float)2.0,(float)100.0,1);
		Point MyPoint2 =new Point ((float)1.0 ,(float)2.0,(float)100.0,3);
		double epsilon =10e-40;
		double speed = MyPoint1.speedTo(MyPoint2);
	    assertEquals (10*MyPoint1.distanceTo(MyPoint2)/(MyPoint1.time - MyPoint2.time), speed,epsilon) ;
	}

}
