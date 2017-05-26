/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.samples;

import junit.framework.TestCase;

public class TestShareGlobals extends TestCase {
	
	static int x1 = 1;
	static int x2 = 1;
	static int x3 = 1;
	static int x4 = 1;
	static int x5 = 1;
	
	public static void reset() {
		x1 = 1;
		x2 = 1;
		x3 = 1;
		x4 = 1;
		x5 = 1;
	}
	
	public void test1() {
		x1 --;
		x2 --;
	}
	
	public void test2() {
		x3 --;
	}
	
	public void test3() {
		x5 --;
		if(x4 == 0) {
			throw new RuntimeException();
		}
	}
	
	public void test4() {
		x4 --;
		x5 --;
	}
	
	public void test5() {
		assertTrue(x1 !=0 || x2 != 0 || x3 != 0 || x4 !=0 || x5 !=0);
	}
	
	public void test6() {
		x4--;
	}
	
	public void testDummy() {}
	
	public void testDummy1() {}
	
	public void testDummy2() {}
	
	/**Another set of tests, which have non-inference with the above */
	static String a = null;
	public void testStr1() {
		a = "a";
	}
	public void testStr2() {
		a.intern();
	}
	public void testStr3() {
		a.intern();
	}
	public void testStr4() {
		a.intern();
	}
	public void testStr5() {
		a.intern();
	}
}