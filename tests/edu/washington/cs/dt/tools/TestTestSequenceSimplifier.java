/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.tools;

import java.io.IOException;

import junit.framework.TestCase;

public class TestTestSequenceSimplifier extends TestCase {
	
	public void testNoNeedToSimplify() throws IOException {
		TestSequenceSimplifier.main(new String[]{"--testFile=./tests/edu/washington/cs/dt/tools/testlist1.txt",
				"--outputFile=./no_need_simplify.txt"});
	}
	
	public void testNeedToSimplify() throws IOException {
		TestSequenceSimplifier.main(new String[]{"--testFile=./tests/edu/washington/cs/dt/tools/testlist2.txt",
				"--outputFile=./need_simplify.txt"});
	}

}