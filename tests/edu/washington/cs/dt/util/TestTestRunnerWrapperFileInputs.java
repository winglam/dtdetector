/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.util;

import java.io.IOException;

import edu.washington.cs.dt.main.Main;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestTestRunnerWrapperFileInputs extends TestCase {
	
	public static Test suite() {
		return new TestSuite(TestTestRunnerWrapperFileInputs.class);
	}
	
	public void testExeAll() throws IOException {
		Main.junit4 = true;
		String[] args = new String[]{
				"./tmp.txt",
				"./tests/edu/washington/cs/dt/samples/junit4x/junit4tests.txt"
				
		};
		TestRunnerWrapperFileInputs.main(args);
	}
	
	public void tearDown() {
		Main.junit4 = false;
	}

}
