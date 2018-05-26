/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.util;

import edu.washington.cs.dt.RESULT;
import edu.washington.cs.dt.samples.SampleJUnit3Tests;
import edu.washington.cs.dt.samples.junit4x.ExampleBeforeClassTests;
import edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestJUnitTestExecutor extends TestCase {

	public static Test suite() {
		return new TestSuite(TestJUnitTestExecutor.class);
	}

	private static JUnitTestResult singletonResult(final JUnitTestExecutor executor) {
	    return executor.executeWithJUnit4Runner(false).iterator().next();
    }

	public void testPassJUnit3() throws Exception {
		JUnitTestExecutor executor = JUnitTestExecutor.singleton(SampleJUnit3Tests.class.getCanonicalName() + ".testJUnit3_1");
		JUnitTestResult result = singletonResult(executor);
		assertEquals(RESULT.PASS.name(), result.getResult());
		assertEquals(TestExecUtils.noStackTrace, result.getStackTrace());
	}

	public void testPassJUnit4() throws Exception {
		JUnitTestExecutor executor = JUnitTestExecutor.singleton(ExampleJunit4xTest.class.getCanonicalName() + ".testX");
		JUnitTestResult result = singletonResult(executor);
		assertEquals(RESULT.PASS.name(), result.getResult());
		assertEquals(TestExecUtils.noStackTrace, result.getStackTrace());
	}

	public void testJUnit3WithException() throws Exception {
		JUnitTestExecutor executor = JUnitTestExecutor.singleton(SampleJUnit3Tests.class.getCanonicalName() + ".testJUnit3_exception");
        JUnitTestResult result = singletonResult(executor);
		assertEquals(RESULT.ERROR.name(), result.getResult());
		assertFalse(TestExecUtils.noStackTrace.equals(result.getStackTrace()));
	}

	public void testFail1JUnit4() throws Exception {
		JUnitTestExecutor executor = JUnitTestExecutor.singleton(ExampleJunit4xTest.class.getCanonicalName() + ".testE");
		JUnitTestResult result = singletonResult(executor);
		assertEquals(RESULT.FAILURE.name(), result.getResult());
		assertFalse(TestExecUtils.noStackTrace.equals(result.getStackTrace()));
	}

	public void testJUnit3WithFailure() throws Exception {
		JUnitTestExecutor executor = JUnitTestExecutor.singleton(SampleJUnit3Tests.class.getCanonicalName() + ".testJUnit3_fail");
        JUnitTestResult result = singletonResult(executor);
		assertEquals(RESULT.FAILURE.name(), result.getResult());
		assertFalse(TestExecUtils.noStackTrace.equals(result.getStackTrace()));
	}

	public void testFail2JUnit4() throws Exception {
		JUnitTestExecutor executor = JUnitTestExecutor.singleton(ExampleJunit4xTest.class.getCanonicalName() + ".testF");
		JUnitTestResult result = singletonResult(executor);
		assertEquals(RESULT.FAILURE.name(), result.getResult());
		assertFalse(TestExecUtils.noStackTrace.equals(result.getStackTrace()));
	}

	public void testErrorJUnit4() throws Exception {
		JUnitTestExecutor executor = JUnitTestExecutor.singleton(ExampleJunit4xTest.class.getCanonicalName() + ".testZ");
        JUnitTestResult result = singletonResult(executor);
		assertEquals(RESULT.ERROR.name(), result.getResult());
		assertFalse(TestExecUtils.noStackTrace.equals(result.getStackTrace()));
	}

	public void testRunMultipleJUnit4() throws ClassNotFoundException {
		final JUnitTestExecutor executor = JUnitTestExecutor.testOrder(
				Arrays.asList(
						"edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testZ",
						"edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testX",
						"edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.test1"
				)
		);

		// We have to clear this before running tests in this class.
        // This is because each time we invoke JUnit, it calls the @BeforeClass in the test class,
        // causing some tests (e.g. ExampleJunit4xTest.test1) to fail.
		ExampleJunit4xTest.list.clear();

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

	public void testRunSeparateAndTogether() throws ClassNotFoundException {
		final List<String> testOrder =
            Arrays.asList(
                    "edu.washington.cs.dt.samples.junit4x.ExampleBeforeClassTests.testXsHasOneItemAndAddOne",
                    "edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testX",
                    "edu.washington.cs.dt.samples.junit4x.ExampleBeforeClassTests.testXsHasTwoItems"
            );

		final JUnitTestExecutor executor = JUnitTestExecutor.testOrder(testOrder);
		final Map<String, String> expectedJUnitRunner = new HashMap<>();

		expectedJUnitRunner.put("edu.washington.cs.dt.samples.junit4x.ExampleBeforeClassTests.testXsHasOneItemAndAddOne", RESULT.PASS.name());
		expectedJUnitRunner.put("edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testX", RESULT.PASS.name());
		expectedJUnitRunner.put("edu.washington.cs.dt.samples.junit4x.ExampleBeforeClassTests.testXsHasTwoItems", RESULT.PASS.name());

		final Map<String, String> expectedSeparate = new HashMap<>();

		expectedSeparate.put("edu.washington.cs.dt.samples.junit4x.ExampleBeforeClassTests.testXsHasOneItemAndAddOne", RESULT.PASS.name());
		expectedSeparate.put("edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testX", RESULT.PASS.name());
		expectedSeparate.put("edu.washington.cs.dt.samples.junit4x.ExampleBeforeClassTests.testXsHasTwoItems", RESULT.ERROR.name());

		checkExpected(expectedJUnitRunner, executor.executeWithJUnit4Runner(false));

		// Need to clear the state between runs.
		ExampleBeforeClassTests.xs.clear();

		checkExpected(expectedSeparate, executor.executeSeparately(false));
	}

	private void checkExpected(final Map<String, String> expected, final Set<JUnitTestResult> results) {
		Assert.assertEquals(expected.size(), results.size());

		for (final JUnitTestResult result : results) {
			Assert.assertEquals(expected.get(result.getTest().name()), result.getResult());
		}
	}
}
