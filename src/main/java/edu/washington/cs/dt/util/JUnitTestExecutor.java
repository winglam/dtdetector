/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang, Reed Oei
 */
package edu.washington.cs.dt.util;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import junit.framework.AssertionFailedError;
import junit.framework.ComparisonFailure;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.Failure;

import edu.washington.cs.dt.RESULT;
import edu.washington.cs.dt.main.Main;
import org.junit.runner.notification.RunListener;


/**
 * This class is in too low level. I made it package-visible
 * on purpose.
 * */
class JUnitTestExecutor {
    private static final PrintStream EMPTY_STREAM = new PrintStream(new OutputStream() {
        public void write(int b) {
            //DO NOTHING
        }
    });

	private final List<JUnitTest> testOrder = new ArrayList<>();
    private final Map<String, JUnitTest> testMap = new HashMap<>();
	private final Set<Class<?>> allClasses = new HashSet<>();
	private final Set<JUnitTestResult> missingTests = new HashSet<>();

    public JUnitTestExecutor(final JUnitTest test) {
        this(Collections.singletonList(test));
    }

    public JUnitTestExecutor(final List<JUnitTest> tests) {
        this(tests, new HashSet<JUnitTestResult>());
    }

    public JUnitTestExecutor(final List<JUnitTest> tests, final Set<JUnitTestResult> missingTests) {
        for (final JUnitTest test : tests) {
            if (test.isClassCompatible()) {
                testOrder.add(test);
                allClasses.add(test.testClass());
                testMap.put(test.name(), test);
            } else {
                System.out.println("  Detected incompatible test case with RunWith annotation.");
            }
        }

        this.missingTests.addAll(missingTests);
    }

    //package.class.method
    public static JUnitTestExecutor singleton(final String fullMethodName) throws ClassNotFoundException {
        return new JUnitTestExecutor(new JUnitTest(fullMethodName));
    }

    public static JUnitTestExecutor singleton(String className, String junitMethod) {
        return new JUnitTestExecutor(new JUnitTest(className, junitMethod));
    }

    public static JUnitTestExecutor singleton(Class<?> junitTest, String junitMethod) {
        return new JUnitTestExecutor(new JUnitTest(junitTest, junitMethod));
    }

    public static JUnitTestExecutor skipMissing(final List<String> testOrder) {
        final List<JUnitTest> tests = new ArrayList<>();
        final Set<JUnitTestResult> missingTests = new HashSet<>();

        for (int i = 0; i < testOrder.size(); i++) {
            final String fullMethodName = testOrder.get(i);
            try {
                final JUnitTest test = new JUnitTest(fullMethodName, i);

                tests.add(test);
            } catch (ClassNotFoundException e) {
                missingTests.add(JUnitTestResult.missing(fullMethodName));
                System.out.println("  Skipped missing test : " + fullMethodName);
            }
        }

        return new JUnitTestExecutor(tests, missingTests);
    }

    public static JUnitTestExecutor testOrder(final List<String> testOrder) throws ClassNotFoundException {
        final List<JUnitTest> tests = new ArrayList<>();

        for (int i = 0; i < testOrder.size(); i++) {
            final String fullMethodName = testOrder.get(i);
            tests.add(new JUnitTest(fullMethodName, i));
        }

        return new JUnitTestExecutor(tests);
    }

    public Set<JUnitTestResult> executeWithJUnit4Runner(final boolean skipIncompatible) {
        JUnitCore core = new JUnitCore();

        final Request r = Request.classes(allClasses.toArray(new Class<?>[0]))
                .filterWith(new TestOrderFilter())
                .sortWith(new TestOrderComparator());

        PrintStream currOut = System.out;
		PrintStream currErr = System.err;

		System.setOut(EMPTY_STREAM);
		System.setErr(EMPTY_STREAM);

		final Map<String, Long> testRuntimes = new HashMap<>();
		core.addListener(new TestTimeListener(testRuntimes));

		System.setOut(currOut);
		System.setErr(currErr);

		final Set<JUnitTestResult> results = new HashSet<>(missingTests);
		final Map<String, JUnitTest> passingTests = new HashMap<>(testMap);

        final Result re = core.run(r);
		for (final Failure failure : re.getFailures()) {
		    final String fullTestName = TestExecUtils.fullName(failure.getDescription());

		    results.add(JUnitTestResult.failOrError(failure,
                    testRuntimes.get(fullTestName),
                    passingTests.get(fullTestName)));
            passingTests.remove(fullTestName);
        }

        for (final String fullMethodName : passingTests.keySet()) {
            results.add(JUnitTestResult.passing(testRuntimes.get(fullMethodName), passingTests.get(fullMethodName)));
        }

        return results;
	}

    private static class TestTimeListener extends RunListener {
        private final Map<String, Long> times;
        private final Map<String, Long> testRuntimes;

        public TestTimeListener(Map<String, Long> testRuntimes) {
            this.testRuntimes = testRuntimes;
            times = new HashMap<>();
        }

        @Override
        public void testStarted(Description description) throws Exception {
            times.put(TestExecUtils.fullName(description), System.nanoTime());
        }

        @Override
        public void testFinished(Description description) throws Exception {
            final String fullTestName = TestExecUtils.fullName(description);

            if (times.containsKey(fullTestName)) {
                final long startTime = times.get(fullTestName);
                testRuntimes.put(fullTestName, System.nanoTime() - startTime);
                times.remove(fullTestName);
            } else {
                System.out.println("Test finished but did not start: " + description.getDisplayName());
            }
        }
    }

    private class TestOrderFilter extends Filter {
        @Override
        public boolean shouldRun(final Description description) {
            for (final JUnitTest test : testOrder) {
                if (Filter.matchMethodDescription(test.description()).shouldRun(description)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public String describe() {
            // Return the order we are running in.
            final List<String> testNames = new ArrayList<>();
            for (final JUnitTest test : testOrder) {
                testNames.add(test.name());
            }

            return testNames.toString();
        }
    }

    private class TestOrderComparator implements Comparator<Description> {
        @Override
        public int compare(Description a, Description b) {
            // Safe to not check for null on map access because we filtered first.
            return Integer.compare(
                    testMap.get(TestExecUtils.fullName(a)).index(),
                    testMap.get(TestExecUtils.fullName(b)).index());
        }
    }
}
