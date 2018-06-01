/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.util;

import edu.washington.cs.dt.RESULT;
import edu.washington.cs.dt.samples.SampleJUnit3Tests;
import edu.washington.cs.dt.samples.junit4x.ExampleBeforeClassTests;
import edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestJUnitTestExecutor extends TestCase {

	public static Test suite() {
		return new TestSuite(TestJUnitTestExecutor.class);
	}

	private static JUnitTestResult singletonResult(final JUnitTestExecutor executor) {
	    return executor.executeWithJUnit4Runner().iterator().next();
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

	public void testRunMultipleJUnit4() throws Exception {
		final JUnitTestExecutor executor = JUnitTestExecutor.testOrder(
				Arrays.asList(
						"edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testZ",
						"edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testX",
						"edu.washington.cs.dt.samples.junit4x.ExampleBeforeClassTests.testXsHasOneItemAndAddOne",
						"edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.test1",
						"edu.washington.cs.dt.samples.junit4x.ExampleBeforeClassTests.testXsHasTwoItems",
						"edu.washington.cs.dt.samples.junit4x.ExampleBeforeClassTests.interactWithJunit4xTest",
						"edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testK"
				)
		);

		// We have to clear this before running tests in this class.
        // This is because each time we invoke JUnit, it calls the @BeforeClass in the test class,
        // causing some tests (e.g. ExampleJunit4xTest.test1) to fail.
		ExampleJunit4xTest.list.clear();

		final Map<String, String> expected = new HashMap<>();
		expected.put("edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.test1", RESULT.PASS.name());
		expected.put("edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testX", RESULT.PASS.name());
		expected.put("edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testZ", RESULT.ERROR.name());
		expected.put("edu.washington.cs.dt.samples.junit4x.ExampleBeforeClassTests.testXsHasOneItemAndAddOne", RESULT.PASS.name());
		expected.put("edu.washington.cs.dt.samples.junit4x.ExampleBeforeClassTests.testXsHasTwoItems", RESULT.PASS.name());
		expected.put("edu.washington.cs.dt.samples.junit4x.ExampleBeforeClassTests.interactWithJunit4xTest", RESULT.PASS.name());
		expected.put("edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testK", RESULT.PASS.name());

		checkExpected(expected, executor.executeWithJUnit4Runner());
	}

	public void testRunSeparateAndTogether() throws Exception {
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

		// Need to clear the state between runs.
		ExampleBeforeClassTests.xs.clear();

		checkExpected(expectedJUnitRunner, executor.executeWithJUnit4Runner());

		// Need to clear the state between runs.
		ExampleBeforeClassTests.xs.clear();

		checkExpected(expectedSeparate, executor.executeSeparately());
	}

	public void testUseCustomRunner() throws Exception {
		final List<String> testOrder =
				Collections.singletonList(
						"edu.washington.cs.dt.samples.junit4x.SampleTestRunnerTests.test1"
				);

		final JUnitTestExecutor executor = JUnitTestExecutor.testOrder(testOrder);
		final Map<String, String> expected = new HashMap<>();

		expected.put("edu.washington.cs.dt.samples.junit4x.SampleTestRunnerTests.test1", RESULT.PASS.name());

		checkExpected(expected, executor.executeWithJUnit4Runner());
	}

	private void checkExpected(final Map<String, String> expected, final Set<JUnitTestResult> results) {
		assertEquals(expected.size(), results.size());

		for (final JUnitTestResult result : results) {
			if (!expected.containsKey(result.getTest().name())) {
				fail("Ran unexpected test: " + result.getTest().name());
			}

			assertEquals(expected.get(result.getTest().name()), result.getResult());
		}
	}
}
