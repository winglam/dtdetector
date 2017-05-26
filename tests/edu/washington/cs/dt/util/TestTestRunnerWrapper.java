/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.util;

import java.io.IOException;

import edu.washington.cs.dt.main.Main;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestTestRunnerWrapper extends TestCase {
	
	public static Test suite() {
		return new TestSuite(TestTestRunnerWrapper.class);
	}
	
	public void testExe1() throws IOException {
		Main.junit4 = true;
		String[] args = new String[]{
				"./tmp.txt",
				"edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testX"
		};
		TestRunnerWrapper.main(args);
	}
	
	public void testExe2() throws IOException {
		Main.junit4 = true;
		String[] args = new String[]{
				"./tmp.txt",
				"edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testZ"
		};
		TestRunnerWrapper.main(args);
	}
	
	public void testExe3() throws IOException {
		Main.junit4 = true;
		String[] args = new String[]{
				"./tmp.txt",
				"edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testE"
		};
		TestRunnerWrapper.main(args);
	}
	
	public void testExeAll() throws IOException {
		Main.junit4 = true;
		String[] args = new String[]{
				"./tmp.txt",
				"edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testX",
				"edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testY",
				"edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testE"
		};
		TestRunnerWrapper.main(args);
	}
	
	public void tearDown() {
		Main.junit4 = false;
	}
}
