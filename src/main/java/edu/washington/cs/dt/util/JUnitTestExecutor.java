/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang, Reed Oei
 */
package edu.washington.cs.dt.util;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runners.model.InitializationError;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


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

    public static Set<JUnitTestResult> runOrder(final List<String> testList,
                                                final boolean skipMissing,
                                                final boolean runSeparately)
            throws ClassNotFoundException{
        final JUnitTestExecutor executor;
        if (skipMissing) {
            executor = JUnitTestExecutor.skipMissing(testList);
        } else {
            executor = JUnitTestExecutor.testOrder(testList);
        }

        if (runSeparately) {
            return executor.executeSeparately();
        } else {
            return executor.executeWithJUnit4Runner();
        }
    }

	private final List<JUnitTest> testOrder = new ArrayList<>();
    private final Map<String, JUnitTest> testMap = new HashMap<>();
	private final Set<Class<?>> allClasses = new HashSet<>();
	private final Set<JUnitTestResult> knownResults = new HashSet<>();

    public JUnitTestExecutor(final JUnitTest test) {
        this(Collections.singletonList(test));
    }

    public JUnitTestExecutor(final List<JUnitTest> tests) {
        this(tests, new HashSet<JUnitTestResult>());
    }

    public JUnitTestExecutor(final List<JUnitTest> tests, final Set<JUnitTestResult> knownResults) {
        for (final JUnitTest test : tests) {
            if (test.isClassCompatible()) {
                testOrder.add(test);
                allClasses.add(test.javaClass());
                testMap.put(test.name(), test);
            } else {
                System.out.println("  Detected incompatible test case with RunWith annotation.");
            }
        }

        this.knownResults.addAll(knownResults);
    }

    //package.class.method
    public static JUnitTestExecutor singleton(final String fullMethodName) throws ClassNotFoundException {
        return JUnitTestExecutor.testOrder(Collections.singletonList(fullMethodName));
    }

    public static JUnitTestExecutor skipMissing(final List<String> testOrder) {
        final List<JUnitTest> tests = new ArrayList<>();
        final Set<JUnitTestResult> knownResults = new HashSet<>();

        for (int i = 0; i < testOrder.size(); i++) {
            final String fullMethodName = testOrder.get(i);
            try {
                final JUnitTest test = new JUnitTest(fullMethodName, i);

                tests.add(test);
            } catch (ClassNotFoundException e) {
                knownResults.add(JUnitTestResult.missing(fullMethodName));
                System.out.println("  Skipped missing test : " + fullMethodName);
            } catch (ExceptionInInitializerError e) {
                knownResults.add(JUnitTestResult.initFailure(e, fullMethodName));
                System.out.println("Test failed in initialization: " + fullMethodName);
            }
        }

        return new JUnitTestExecutor(tests, knownResults);
    }

    public static JUnitTestExecutor testOrder(final List<String> testOrder) throws ClassNotFoundException {
        final List<JUnitTest> tests = new ArrayList<>();

        for (int i = 0; i < testOrder.size(); i++) {
            final String fullMethodName = testOrder.get(i);
            tests.add(new JUnitTest(fullMethodName, i));
        }

        return new JUnitTestExecutor(tests);
    }

    private boolean checkContains(final Map<String, Long> testRuntimes,
                                  final Map<String, JUnitTest> passingTests,
                                  final String fullTestName) {
        if (!testRuntimes.containsKey(fullTestName)) {
            System.out.println("[ERROR]: No running time measured for test name '" + fullTestName + "'");
            return false;
        }

        if (!passingTests.containsKey(fullTestName)) {
            System.out.println("[ERROR]: No JUnitTest found for test name '" + fullTestName + "'");
            return false;
        }

        return true;
    }

    private Set<JUnitTestResult> results(final Result re, final Map<String, Long> testRuntimes) {
        final Set<JUnitTestResult> results = new HashSet<>(knownResults);
        final Map<String, JUnitTest> passingTests = new HashMap<>();

        // We can only mark a test as passing if it actually ran.
        for (final String testName : testRuntimes.keySet()) {
            passingTests.put(testName, testMap.get(testName));
        }

        for (final Failure failure : re.getFailures()) {
            System.out.println(failure.getDescription());
            // If the description is a test (that is, a single test), then handle it normally.
            // Otherwise, the ENTIRE class failed during initialization or some such thing.
            if (failure.getDescription().isTest()) {
                final String fullTestName = TestExecUtils.fullName(failure.getDescription());

                if (!checkContains(testRuntimes, passingTests, fullTestName)) {
                    continue;
                }

                results.add(JUnitTestResult.failOrError(failure,
                        testRuntimes.get(fullTestName),
                        passingTests.get(fullTestName)));
                passingTests.remove(fullTestName);
            } else {
                // The ENTIRE class failed, so we need to mark every test from this class as failing.
                final String className = failure.getDescription().getClassName();

                // Make a set first so that we can modify the original hash map
                for (final JUnitTest test : testOrder) {
                    if (test.javaClass().getCanonicalName().equals(className)) {
                        results.add(JUnitTestResult.failOrError(failure, 0, test));

                        if (passingTests.containsKey(test.name())) {
                            passingTests.remove(test.name());
                        }
                    }
                }
            }
        }

        for (final String fullMethodName : passingTests.keySet()) {
            if (!checkContains(testRuntimes, passingTests, fullMethodName)) {
                continue;
            }

            results.add(JUnitTestResult.passing(testRuntimes.get(fullMethodName), passingTests.get(fullMethodName)));
        }

        return results;
    }

    private Set<JUnitTestResult> execute(final List<JUnitTest> tests) {
        // This will happen only if no tests are selected by the filter.
        // In this case, we will throw an exception with a name that makes sense.
        if (tests.size() == 0) {
            throw new EmptyTestListException(testOrder);
        }

        final PrintStream currOut = System.out;
        final PrintStream currErr = System.err;

//        System.setOut(EMPTY_STREAM);
//        System.setErr(EMPTY_STREAM);

        final JUnitCore core = new JUnitCore();

        final Map<String, Long> testRuntimes = new HashMap<>();
        core.addListener(new TestTimeListener(testRuntimes));
        final Result re;
        try {
            re = core.run(new JUnitTestRunner(tests));

//        System.setOut(currOut);
//        System.setErr(currErr);

            return results(re, testRuntimes);
        } catch (InitializationError initializationError) {
            initializationError.printStackTrace();
        }

        return new HashSet<>();
    }

    public Set<JUnitTestResult> executeSeparately() {
        final Set<JUnitTestResult> results = new HashSet<>();

        for (final JUnitTest test : testOrder) {
            results.addAll(execute(Collections.singletonList(test)));
        }

        return results;
    }

    public Set<JUnitTestResult> executeWithJUnit4Runner() {
        return execute(testOrder);
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
            System.out.println("Test being executed: " + TestExecUtils.fullName(description));
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
                System.out.println("Test finished but did not start: " + fullTestName);
            }
        }
    }
}
