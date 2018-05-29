package edu.washington.cs.dt.util;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.statements.Fail;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JUnitTestRunner extends BlockJUnit4ClassRunner {
    private final Set<String> ranBeforeClassSet = new HashSet<>();
    private final List<JUnitTest> tests = new ArrayList<>();

    public JUnitTestRunner(final List<JUnitTest> tests) throws InitializationError {
        // Necessary so we can use all of the JUnit runner code written for BlockJUnit4ClassRunner.
        super(DummyClass.class);
        this.tests.addAll(tests);
    }

    @Override
    protected List<FrameworkMethod> getChildren() {
        final List<FrameworkMethod> children = new ArrayList<>();

        for (final JUnitTest test : tests) {
            children.add(test.frameworkMethod());
        }

        return children;
    }

    @Override
    public void run(RunNotifier notifier) {
        EachTestNotifier testNotifier = new EachTestNotifier(notifier, this.getDescription());

        try {
            for (final JUnitTest test : tests) {
                System.out.println("Test being executed: " + TestExecUtils.fullName(test.description()));
                runChild(test, notifier);
            }
        } catch (AssumptionViolatedException e) {
            testNotifier.fireTestIgnored();
        } catch (StoppedByUserException e) {
            throw e;
        } catch (Throwable e) {
            testNotifier.addFailure(e);
        }
    }

    private boolean ranBeforeClass(final FrameworkMethod method) {
        return ranBeforeClassSet.contains(method.getMethod().getDeclaringClass().getCanonicalName());
    }

    private boolean isLastMethod(final FrameworkMethod method) {
        final String fullTestName = JUnitTest.methodName(method);

        boolean found = false;
        for (final JUnitTest test : tests) {
            if (test.name().equals(fullTestName)) {
                found = true;
            } else if (test.javaClass().equals(method.getMethod().getDeclaringClass()) && found) {
                // If we've found the method passed in, and we find another method in the same class
                // we aren't done with the class yet.
                return false;
            }
        }

        return true;
    }

    private Statement beforeClasses(final JUnitTest test) {
        final List<FrameworkMethod> befores = test.testClass().getAnnotatedMethods(BeforeClass.class);
        return new RunBefores(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                // Intentionally empty.
            }
        }, befores, null);
    }

    private Statement afterClasses(final JUnitTest test) {
        final List<FrameworkMethod> afters = test.testClass().getAnnotatedMethods(AfterClass.class);
        return new RunAfters(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                // Intentionally empty.
            }
        }, afters, null);
    }

    private Statement withBefores(JUnitTest test, Object target,
                                  Statement statement) {
        List<FrameworkMethod> befores = test.testClass().getAnnotatedMethods(Before.class);

        for (final Method method : Utils.getAllMethods(test.javaClass())) {
            if (method.getName().toLowerCase().equals("setup")) {
                method.setAccessible(true);
                befores.add(new FrameworkMethod(method));
            }
        }

        return befores.isEmpty() ? statement : new RunBefores(statement, befores, target);
    }

    private Statement withAfters(JUnitTest test, Object target,
                                 Statement statement) {
        List<FrameworkMethod> afters = test.testClass().getAnnotatedMethods(After.class);

        for (final Method method : Utils.getAllMethods(test.javaClass())) {
            if (method.getName().toLowerCase().equals("teardown")) {
                method.setAccessible(true);
                afters.add(new FrameworkMethod(method));
            }
        }

        return afters.isEmpty() ? statement : new RunAfters(statement, afters, target);
    }

    private Statement methodBlock(final JUnitTest test) {
        Object testObj;
        try {
            testObj = (new ReflectiveCallable() {
                protected Object runReflectiveCall() throws Throwable {
                    // for JUnit 3.8.2
                    try {
                        return test.javaClass().getConstructor(String.class).newInstance(test.getTestName());
                    } catch (NoSuchMethodException e) {
                        return test.testClass().getOnlyConstructor().newInstance();
                    }
                }
            }).run();
        } catch (Throwable e) {
            return new Fail(e);
        }

        final FrameworkMethod method = test.frameworkMethod();

        Statement statement = this.methodInvoker(method, testObj);
        statement = this.possiblyExpectingExceptions(method, testObj, statement);
        statement = this.withPotentialTimeout(method, testObj, statement);
        statement = this.withBefores(test, testObj, statement);
        statement = this.withAfters(test, testObj, statement);
        return statement;
    }

    private void runChild(JUnitTest test, RunNotifier notifier) {
        final FrameworkMethod method = test.frameworkMethod();

        if (method == null) {
            System.out.println("Test method not found: " + test.name());
            throw new TestNotFoundException(test);
        }

        final EachTestNotifier eachNotifier = new EachTestNotifier(notifier, test.description());

        if (method.getAnnotation(Ignore.class) != null) {
            eachNotifier.fireTestIgnored();
        } else {
            eachNotifier.fireTestStarted();

            try {
                Statement statement = methodBlock(test);

                if (!ranBeforeClass(method)) {
                    ranBeforeClassSet.add(method.getMethod().getDeclaringClass().getCanonicalName());
                    // Run this way so it doesn't show up in the stack trace for the test and possibly cause the tools
                    // to incorrectly label it as dependent
                    beforeClasses(test).evaluate();
                }

                statement.evaluate();

                if (isLastMethod(method)) {
                    // Run this way so it doesn't show up in the stack trace for the test and possibly cause the tools
                    // to incorrectly label it as dependent
                    afterClasses(test).evaluate();
                }
            } catch (AssumptionViolatedException e) {
                eachNotifier.addFailedAssumption(e);
            } catch (Throwable e) {
                eachNotifier.addFailure(e);
            } finally {
                eachNotifier.fireTestFinished();
            }
        }
    }

    @Override
    protected Description describeChild(FrameworkMethod method) {
        if (method == null) {
            return Description.EMPTY;
        }
        return Description.createTestDescription(method.getMethod().getDeclaringClass(), method.getName());
    }
}
