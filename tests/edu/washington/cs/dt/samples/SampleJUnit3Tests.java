package edu.washington.cs.dt.samples;

import junit.framework.TestCase;

public class SampleJUnit3Tests extends TestCase {

	public void testJUnit3_1() {}
	public void testJUnit3_2() {}
	public void testJUnit3_3() {}
	
	public void testJUnit3_fail() { fail(); }
	
	public void testJUnit3_exception() { throw new IllegalArgumentException(); }
	
}
