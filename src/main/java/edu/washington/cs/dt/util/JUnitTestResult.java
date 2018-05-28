package edu.washington.cs.dt.util;

import edu.washington.cs.dt.RESULT;
import edu.washington.cs.dt.main.Main;
import junit.framework.AssertionFailedError;
import junit.framework.ComparisonFailure;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.runner.notification.Failure;

import java.util.regex.Pattern;

public class JUnitTestResult {
    private final String result;
    private final String stackTrace;
    private final String fullStackTrace;
    private final long interval;

    private final String testName;
    @Nullable
    private final JUnitTest test;

    /**
     * Private constructor, use the static methods to create instances (more descriptive names).
     */
    private JUnitTestResult(final String result,
                            final String stackTrace,
                            final String fullStackTrace,
                            final long interval,
                            final String testName,
                            final JUnitTest test) {
        this.result = result;
        this.stackTrace = stackTrace;
        this.fullStackTrace = fullStackTrace;
        this.interval = interval;
        this.testName = testName;
        this.test = test;
    }

    public static JUnitTestResult passing(final Long interval, final JUnitTest test) {
        return new JUnitTestResult(RESULT.PASS.name(),
                TestExecUtils.noStackTrace,
                TestExecUtils.noStackTrace,
                interval,
                test.name(),
                test);
    }

    public static JUnitTestResult missing(final String fullMethodName) {
        return new JUnitTestResult(RESULT.SKIPPED.name(),
                TestExecUtils.noStackTrace,
                TestExecUtils.noStackTrace,
                -1,
                fullMethodName,
                null);
    }

    public static JUnitTestResult missing(final JUnitTest test) {
        return new JUnitTestResult(RESULT.SKIPPED.name(),
                TestExecUtils.noStackTrace,
                TestExecUtils.noStackTrace,
                -1,
                test.name(),
                test);
    }

    public static JUnitTestResult ignored(String fullMethodName) {
        return new JUnitTestResult(RESULT.IGNORED.name(),
                TestExecUtils.noStackTrace,
                TestExecUtils.noStackTrace,
                -1,
                fullMethodName,
                null);
    }

    public static JUnitTestResult failOrError(final Failure failure,
                                              final long interval,
                                              final JUnitTest test) {
        final String result;
        final String stackTrace;
        final String fullStackTrace;

        //check whether it is a failure or an error
        final Throwable excep = failure.getException();
        if(isJUnitAssertionFailure(excep)) {
            result = RESULT.FAILURE.name();
        } else {
            result = RESULT.ERROR.name();
        }

        //get the stack trace
        stackTrace = flatStackTrace(excep);
        fullStackTrace = TestExecUtils.flatStrings(TestExecUtils.extractStackTraces(excep));

        return new JUnitTestResult(result, stackTrace, fullStackTrace, interval, test.name(), test);
    }

    private static boolean isJUnitAssertionFailure(Throwable exception) {
        return exception.getClass().equals(AssertionFailedError.class)
              || exception.getClass().equals(ComparisonFailure.class);
    }

    public static String flatStackTrace(Throwable exception) {
		String[] stacktraces = TestExecUtils.extractStackTraces(exception);
		Pattern p = Pattern.compile(Main.excludeRegex);
        return TestExecUtils.flatStrings(stacktraces, p, TestExecUtils.JUNIT_ASSERT);
	}

    public void output(StringBuilder sb) {
        sb.append(testName);
        sb.append(TestExecUtils.timeSep);
        sb.append(interval);
        sb.append(TestExecUtils.testResultSep);
        sb.append(result);
        sb.append(TestExecUtils.resultExcepSep);
        //			sb.append(stackTrace);
        sb.append(fullStackTrace);
        sb.append(Globals.lineSep);
    }

    public String getResult() {
        return result;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public JUnitTest getTest() {
        return test;
    }

    public static JUnitTestResult initFailure(final ExceptionInInitializerError e, final String fullMethodName) {
        final String stackTrace = flatStackTrace(e);
        final String fullStackTrace = TestExecUtils.flatStrings(TestExecUtils.extractStackTraces(e));

        return new JUnitTestResult(RESULT.FAILURE.name(), stackTrace, fullStackTrace, 0, fullMethodName, null);
    }
}
