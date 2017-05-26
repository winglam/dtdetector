/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.util;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestRandomization extends TestCase {
	
	public static Test suite() {
		return new TestSuite(TestRandomization.class);
	}

	public void testSimpleRandomization() {
		List<String> tests = new LinkedList<String>();
		tests.add("test1");
		tests.add("test2");
		tests.add("test3");
		tests.add("test4");
		tests.add("test5");
		//randomize
		tests = Utils.randomList(tests);
		System.out.println(tests);
	}
	
}