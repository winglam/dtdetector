/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestPermutationGenerator extends TestCase {
	
	public static Test suite() {
		return new TestSuite(TestPermutationGenerator.class);
	}

	public void testPermutation0() {
		PermutationGenerator g = new PermutationGenerator(3, 2);
		assertEquals(6, g.getPermutationNum());
		
		while(g.hasNext()) {
			int[] next = g.getNext();
			System.out.println(Utils.convertArrayToFlatString(next));
		}
	}
	
}