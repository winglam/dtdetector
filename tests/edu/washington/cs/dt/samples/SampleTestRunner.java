package edu.washington.cs.dt.samples;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.JUnit4;
import org.junit.runners.model.InitializationError;

public class SampleTestRunner extends Runner {
    private final Class<?> clz;

    // Note. This is intentionally set false. If this test runner is not used, then the tests in
    // SampleTestRunnerTests will fail (BY DESIGN).
    public static boolean thisMustBeTrueForTests = false;

    public SampleTestRunner(final Class<?> clz) {
        this.clz = clz;
    }

    private Runner getRunner() {
        try {
            return new JUnit4(clz);
        } catch (InitializationError initializationError) {
            initializationError.printStackTrace();
            return null;
        }
    }

    @Override
    public Description getDescription() {
        return getRunner().getDescription();
    }

    @Override
    public void run(RunNotifier notifier) {
        thisMustBeTrueForTests = true;
        getRunner().run(notifier);
        thisMustBeTrueForTests = false;
    }
}
