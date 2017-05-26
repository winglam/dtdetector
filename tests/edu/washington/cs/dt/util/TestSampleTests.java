/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.util;

import java.io.IOException;

import edu.washington.cs.dt.samples.TestShareGlobals;

import junit.framework.TestCase;

public class TestSampleTests extends TestCase {
	
	static final String className = "edu.washington.cs.dt.samples.TestShareGlobals";
	
	public void setUp() {
		TestShareGlobals.reset();
	}
	
	public void testAllPass() throws IOException {
		String[] args = new String[]{
				"./tests/edu/washington/cs/dt/samples/allpass.txt",
				className + ".test1",
				className + ".test2",
				className + ".test3",
				className + ".test4",
				className + ".test5"
		};
		TestRunnerWrapper.main(args);
	}

	public void testFail() throws IOException {
		String[] args = new String[]{
				"./tests/edu/washington/cs/dt/samples/havefails.txt",
				className + ".test1",
				className + ".test2",
				className + ".test4",
				className + ".test5"
		};
		TestRunnerWrapper.main(args);
	}
	
	public void testException() throws IOException {
		String[] args = new String[]{
				"./tests/edu/washington/cs/dt/samples/haveerrors.txt",
				className + ".test1",
				className + ".test2",
				className + ".test4",
				className + ".test3"
		};
		TestRunnerWrapper.main(args);
	}
}
