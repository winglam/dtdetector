/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.util;

import edu.washington.cs.dt.RESULT;
import edu.washington.cs.dt.samples.SampleJUnit3Tests;
import edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class TestJUnitTestExecutor extends TestCase {

	public static Test suite() {
		return new TestSuite(TestJUnitTestExecutor.class);
	}

	private static JUnitTestResult singletonResult(final JUnitTestExecutor executor) {
	    return executor.executeWithJUnit4Runner(false).iterator().next();
    }

	public void testPassJUnit3() {
		JUnitTestExecutor executor = JUnitTestExecutor.singleton(SampleJUnit3Tests.class, "testJUnit3_1");
		JUnitTestResult result = singletonResult(executor);
		assertEquals(RESULT.PASS.name(), result.getResult());
		assertEquals(TestExecUtils.noStackTrace, result.getStackTrace());

		try {
			executor = JUnitTestExecutor.singleton("edu.washington.cs.dt.samples.SampleJUnit3Tests.testJUnit3_1");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        result = singletonResult(executor);
		assertEquals(RESULT.PASS.name(), result.getResult());
		assertTrue(TestExecUtils.noStackTrace.equals(result.getStackTrace()));

		executor = JUnitTestExecutor.singleton("edu.washington.cs.dt.samples.SampleJUnit3Tests", "testJUnit3_1");
		result = singletonResult(executor);
		assertEquals(RESULT.PASS.name(), result.getResult());
		assertTrue(TestExecUtils.noStackTrace.equals(result.getStackTrace()));
	}

	public void testPassJUnit4() {
		JUnitTestExecutor executor = JUnitTestExecutor.singleton(ExampleJunit4xTest.class, "testX");
		JUnitTestResult result = singletonResult(executor);
		assertEquals(RESULT.PASS.name(), result.getResult());
		assertEquals(TestExecUtils.noStackTrace, result.getStackTrace());

		try {
			executor = JUnitTestExecutor.singleton("edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testX");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        result = singletonResult(executor);
		assertEquals(RESULT.PASS.name(), result.getResult());
		assertTrue(TestExecUtils.noStackTrace.equals(result.getStackTrace()));

		executor = JUnitTestExecutor.singleton("edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest", "testX");
        result = singletonResult(executor);
		assertEquals(RESULT.PASS.name(), result.getResult());
		assertTrue(TestExecUtils.noStackTrace.equals(result.getStackTrace()));
	}

	public void testJUnit3WithException() {
		JUnitTestExecutor executor = JUnitTestExecutor.singleton(SampleJUnit3Tests.class, "testJUnit3_exception");
        JUnitTestResult result = singletonResult(executor);
		assertEquals(RESULT.ERROR.name(), result.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(result.getStackTrace()));

		try {
			executor = JUnitTestExecutor.singleton("edu.washington.cs.dt.samples.SampleJUnit3Tests.testJUnit3_exception");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = singletonResult(executor);
		assertEquals(RESULT.ERROR.name(), result.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(result.getStackTrace()));

		executor = JUnitTestExecutor.singleton("edu.washington.cs.dt.samples.SampleJUnit3Tests", "testJUnit3_exception");
		result = singletonResult(executor);
		assertEquals(RESULT.ERROR.name(), result.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(result.getStackTrace()));
	}

        /*
	public void testFail1JUnit4() {
		JUnitTestExecutor executor = new JUnitTestExecutor(ExampleJunit4xTest.class, "testE");
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.FAILURE.name(), result.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(result.getStackTrace()));

		try {
			executor = new JUnitTestExecutor("edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testE");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.FAILURE.name(), result.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(result.getStackTrace()));

		executor = new JUnitTestExecutor("edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest", "testE");
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.FAILURE.name(), result.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(result.getStackTrace()));
	}
        */

	public void testJUnit3WithFailure() {
		JUnitTestExecutor executor = JUnitTestExecutor.singleton(SampleJUnit3Tests.class, "testJUnit3_fail");
        JUnitTestResult result = singletonResult(executor);
		assertEquals(RESULT.FAILURE.name(), result.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(result.getStackTrace()));

		try {
			executor = JUnitTestExecutor.singleton("edu.washington.cs.dt.samples.SampleJUnit3Tests.testJUnit3_fail");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = singletonResult(executor);
		assertEquals(RESULT.FAILURE.name(), result.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(result.getStackTrace()));

		executor = JUnitTestExecutor.singleton("edu.washington.cs.dt.samples.SampleJUnit3Tests", "testJUnit3_fail");
		result = singletonResult(executor);
		assertEquals(RESULT.FAILURE.name(), result.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(result.getStackTrace()));
	}

        /*
	public void testFail2JUnit4() {
		JUnitTestExecutor executor = new JUnitTestExecutor(ExampleJunit4xTest.class, "testF");
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.FAILURE.name(), result.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(result.getStackTrace()));

		try {
			executor = new JUnitTestExecutor("edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testF");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.FAILURE.name(), result.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(result.getStackTrace()));

		executor = new JUnitTestExecutor("edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest", "testF");
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.FAILURE.name(), result.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(result.getStackTrace()));
	}
        */

	public void testErrorJUnit4() {
		JUnitTestExecutor executor = JUnitTestExecutor.singleton(ExampleJunit4xTest.class, "testZ");
        JUnitTestResult result = singletonResult(executor);
		assertEquals(RESULT.ERROR.name(), result.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(result.getStackTrace()));

		try {
			executor = JUnitTestExecutor.singleton("edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testZ");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = singletonResult(executor);
		assertEquals(RESULT.ERROR.name(), result.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(result.getStackTrace()));

		executor = JUnitTestExecutor.singleton("edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest", "testZ");
		result = singletonResult(executor);
		assertEquals(RESULT.ERROR.name(), result.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(result.getStackTrace()));
	}

	public void testRunMultipleJUnit4() throws ClassNotFoundException {
		final JUnitTestExecutor executor = JUnitTestExecutor.testOrder(
				Arrays.asList(
						"edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testZ",
						"edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testX",
						"edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.test1"
				)
		);

		final Set<JUnitTestResult> results = executor.executeWithJUnit4Runner(false);

		assertEquals(3, results.size());

		for (final JUnitTestResult result : results) {
		    if (result.getTest().name().equals("edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.test1")) {
		        assertEquals(RESULT.PASS.name(), result.getResult());
            } else if (result.getTest().name().equals("edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testX")) {
                assertEquals(RESULT.PASS.name(), result.getResult());
            } else if (result.getTest().name().equals("edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testZ")) {
                assertEquals(RESULT.ERROR.name(), result.getResult());
            } else {
		        fail("Unexpected test run: " + result.getTest().name());
            }
        }
	}
}
