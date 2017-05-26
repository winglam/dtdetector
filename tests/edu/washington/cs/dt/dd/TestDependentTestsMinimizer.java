/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.dd;

import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.dt.OneTestExecResult;
import edu.washington.cs.dt.RESULT;
import edu.washington.cs.dt.main.Main;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestDependentTestsMinimizer extends TestCase {

	public static Test suite() {
		return new TestSuite(TestDependentTestsMinimizer.class);
	}
	
	@Override
	public void tearDown() {
		Main.comparestacktrace = true;
	}
	
	public void test1() {
		List<String> tests = new LinkedList<String>();
		Main.comparestacktrace = false;
		
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.test1");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.test2");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.testDummy");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.test4");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.testDummy1");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.testDummy2");
		
		
		String dependentTest = "edu.washington.cs.dt.samples.TestShareGlobals.test5";
		OneTestExecResult intendedResult = new OneTestExecResult(RESULT.FAILURE);
		String classPath = "";
		String tmpOutputFile = "./tests/edu/washington/cs/dt/dd/tmpFile1.txt";
		
		DependentTestSetMinimizer minimizer = new DependentTestSetMinimizer(tests, dependentTest,
				intendedResult, classPath, tmpOutputFile);
		List<String> minTests = minimizer.minimize();
		System.out.println(minTests);
		assertEquals(3, minTests.size());
		assertEquals(minTests.get(0), "edu.washington.cs.dt.samples.TestShareGlobals.test1");
		assertEquals(minTests.get(1), "edu.washington.cs.dt.samples.TestShareGlobals.test2");
		assertEquals(minTests.get(2), "edu.washington.cs.dt.samples.TestShareGlobals.test4");
	}
	
	public void test2() {
		Main.comparestacktrace = false;
		
		List<String> tests = new LinkedList<String>();
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.testStr1");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.testStr2");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.testStr3");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.testStr4");
		
		
		String dependentTest = "edu.washington.cs.dt.samples.TestShareGlobals.testStr5";
		OneTestExecResult intendedResult = new OneTestExecResult(RESULT.PASS);
		String classPath = "";
		String tmpOutputFile = "./tests/edu/washington/cs/dt/dd/tmpFile2.txt";
		
		DependentTestSetMinimizer minimizer = new DependentTestSetMinimizer(tests, dependentTest,
				intendedResult, classPath, tmpOutputFile);
		List<String> minTests = minimizer.minimize();
		System.out.println(minTests);
		assertEquals(1, minTests.size());
		assertEquals(minTests.get(0), "edu.washington.cs.dt.samples.TestShareGlobals.testStr1");
	} 
	
}
