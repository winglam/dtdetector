/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.util;

import java.io.IOException;
import java.util.regex.Pattern;

import edu.washington.cs.dt.main.Main;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestStacktrace extends TestCase {
	static final String className = "edu.washington.cs.dt.samples.TestShareGlobals";
	
	public static Test suite() {
		return new TestSuite(TestStacktrace.class);
	}

	public void testExcludingJUnitStackTrace() throws IOException {
		String[] args = new String[]{
				"./tests/edu/washington/cs/dt/samples/havefails.txt",
				className + ".test1",
				className + ".test2",
				className + ".test4",
				className + ".test5"
		};
		TestRunnerWrapper.main(args);
	}
	
	public void testAssertionWrong() {
		String regex = Main.excludeRegex;
		
		String str1 = "junit.framework.TestRunner";
		
		Pattern p = Pattern.compile(regex);
	    assertTrue(TestExecUtils.shouldExclude(str1, p, TestExecUtils.JUNIT_ASSERT));
	    
	    String str2 = "junit.framework.Assert";;
	    assertTrue(!TestExecUtils.shouldExclude(str2, p, TestExecUtils.JUNIT_ASSERT));
	    
	    String str3 = "junit.framework.TestSuite.runTest(TestSuite.java:208)";
	    assertTrue(TestExecUtils.shouldExclude(str3, p, TestExecUtils.JUNIT_ASSERT));
	}
	
	public void testComparingJFreeChartStackTraces() {
		String regex = Main.excludeRegex;
		Pattern p = Pattern.compile(regex);
		String[] fixedOrders = new String[] {
				"junit.framework.AssertionFailedError: expected:<1.2322332E12> but was:<1.232233199999E12>",
				"junit.framework.Assert.fail(Assert.java:47)",
				"junit.framework.Assert.failNotEquals(Assert.java:282)",
				"junit.framework.Assert.assertEquals(Assert.java:101)",
				"junit.framework.Assert.assertEquals(Assert.java:108)",
				"org.jfree.chart.axis.junit.PeriodAxisTests.test2490803(PeriodAxisTests.java:326)",
				"sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)",
				"sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)",
				"sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)",
				"java.lang.reflect.Method.invoke(Method.java:601)",
				"junit.framework.TestCase.runTest(TestCase.java:154)",
				"junit.framework.TestCase.runBare(TestCase.java:127)",
				"junit.framework.TestResult$1.protect(TestResult.java:106)",
				"junit.framework.TestResult.runProtected(TestResult.java:124)",
				"junit.framework.TestResult.run(TestResult.java:109)",
				"junit.framework.TestCase.run(TestCase.java:118)",
				"junit.framework.TestSuite.runTest(TestSuite.java:208)",
				"junit.framework.TestSuite.run(TestSuite.java:203)",
				"junit.framework.TestSuite.runTest(TestSuite.java:208)",
				"junit.framework.TestSuite.run(TestSuite.java:203)",
				"org.eclipse.jdt.internal.junit.runner.junit3.JUnit3TestReference.run(JUnit3TestReference.java:130)",
				"org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)",
				"org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:460)",
				"org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:673)",
				"org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:386)",
				"org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:196)"

		};
		String[] isolations = new String[] {
				"junit.framework.AssertionFailedError: expected:<1.2322332E12> but was:<1.232233199999E12>",
				"junit.framework.Assert.fail(Assert.java:47)",
				"junit.framework.Assert.failNotEquals(Assert.java:282)",
				"junit.framework.Assert.assertEquals(Assert.java:101)",
				"junit.framework.Assert.assertEquals(Assert.java:108)",
				"org.jfree.chart.axis.junit.PeriodAxisTests.test2490803(PeriodAxisTests.java:326)",
				"sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)",
				"sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)",
				"sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)",
				"java.lang.reflect.Method.invoke(Method.java:601)",
				"junit.framework.TestCase.runTest(TestCase.java:154)",
				"junit.framework.TestCase.runBare(TestCase.java:127)",
				"junit.framework.TestResult$1.protect(TestResult.java:106)",
				"junit.framework.TestResult.runProtected(TestResult.java:124)",
				"junit.framework.TestResult.run(TestResult.java:109)",
				"junit.framework.TestCase.run(TestCase.java:118)",
				"org.eclipse.jdt.internal.junit.runner.junit3.JUnit3TestReference.run(JUnit3TestReference.java:130)",
				"org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)",
				"org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:460)",
				"org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:673)",
				"org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:386)",
				"org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:196)"

		};
		String flattenFixed
		    = TestExecUtils.flatStrings(fixedOrders, p, TestExecUtils.JUNIT_ASSERT);
		String flattenIsolation
		    = TestExecUtils.flatStrings(isolations, p, TestExecUtils.JUNIT_ASSERT);
		assertTrue(flattenFixed.equals(flattenIsolation));
	}
}
