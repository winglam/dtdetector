/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.dt.OneTestExecResult;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestSampleTestsInFreshJVM extends TestCase {
	
	static final String className = "edu.washington.cs.dt.samples.TestShareGlobals";
	
	public static Test suite() {
		return new TestSuite(TestSampleTestsInFreshJVM.class);
	}
	
	public void testAllPass() {
		
		String classPath = "";
		
		String outputFile = "./tests/edu/washington/cs/dt/samples/allpass_freshjvm.txt";
		
		List<String> tests = new LinkedList<String>();
		tests.add(className + ".test1");
		tests.add(className + ".test2");
		tests.add(className + ".test3");
		tests.add(className + ".test4");
		tests.add(className + ".test5");
		
		Map<String, OneTestExecResult> result = TestExecUtils.executeTestsInFreshJVM(classPath, outputFile, tests);
		assertEquals(result.size(), 5);
	}

	public void testFail() {
        String classPath = "";
		
		String outputFile = "./tests/edu/washington/cs/dt/samples/havefail_freshjvm.txt";
		
		List<String> tests = new LinkedList<String>();
		tests.add(className + ".test1");
		tests.add(className + ".test2");
		tests.add(className + ".test4");
		tests.add(className + ".test5");
		
		Map<String, OneTestExecResult> result = TestExecUtils.executeTestsInFreshJVM(classPath, outputFile, tests);
		assertEquals(result.size(), 4);
	}
	
	public void testException() {
        String classPath = "";
		
		String outputFile = "./tests/edu/washington/cs/dt/samples/haveerror_freshjvm.txt";
		
		List<String> tests = new LinkedList<String>();
		tests.add(className + ".test1");
		tests.add(className + ".test2");
		tests.add(className + ".test4");
		tests.add(className + ".test3");
		
		Map<String, OneTestExecResult> result = TestExecUtils.executeTestsInFreshJVM(classPath, outputFile, tests);
		assertEquals(result.size(), 4);
	}
	
}
