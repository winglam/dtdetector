/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.samples;

import junit.framework.TestCase;

public class TestDifferentException extends TestCase {
	
	public static Object a = null;
	
	public static Object b = null;
	
	public void testSetA() {
		a = new Object();
	}
	
	public void testSetB() {
		b = new Object();
	}
	
	public void testUseAB() {
		a.hashCode();
		b.hashCode();
		if(a != null && b != null) {
			throw new RuntimeException();
		}
	}
}
