package edu.washington.cs.dt.util;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.builders.AnnotatedBuilder;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.statements.Fail;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.rules.MethodRule;
import org.junit.rules.RunRules;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
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

    private RunNotifier notifier = null;

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
        this.notifier = notifier;
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
        List<FrameworkMethod> befores = new ArrayList<>(test.testClass().getAnnotatedMethods(Before.class));

        for (final Method method : Utils.getAllMethods(test.javaClass())) {
            if (method.getName().toLowerCase().equals("setup") && method.getParameterTypes().length == 0) {
                method.setAccessible(true);

                final FrameworkMethod fMethod = new FrameworkMethod(method);

                if (!befores.contains(fMethod)) {
                    befores.add(fMethod);
                }
            }
        }

        return befores.isEmpty() ? statement : new RunBefores(statement, befores, target);
    }

    private Statement withAfters(JUnitTest test, Object target,
                                 Statement statement) {
        List<FrameworkMethod> afters = new ArrayList<>(test.testClass().getAnnotatedMethods(After.class));

        for (final Method method : Utils.getAllMethods(test.javaClass())) {
            if (method.getName().toLowerCase().equals("teardown") && method.getParameterTypes().length == 0) {
                method.setAccessible(true);

                final FrameworkMethod fMethod = new FrameworkMethod(method);

                if (!afters.contains(fMethod)) {
                    afters.add(fMethod);
                }
            }
        }

        return afters.isEmpty() ? statement : new RunAfters(statement, afters, target);
    }

    private Statement withRules(JUnitTest test, Object target, Statement statement) {
        List<TestRule> testRules = getTestRules(test, target);
        Statement result = statement;
        result = withMethodRules(test, testRules, target, result);
        result = withTestRules(test, testRules, result);

        return result;
    }

    private Statement withMethodRules(JUnitTest test, List<TestRule> testRules,
                                      Object target, Statement result) {
        for (org.junit.rules.MethodRule each : getMethodRules(test, target)) {
            if (!testRules.contains(each)) {
                result = each.apply(result, test.frameworkMethod(), target);
            }
        }
        return result;
    }

    private List<org.junit.rules.MethodRule> getMethodRules(final JUnitTest test, final Object target) {
        return rules(test, target);
    }

    /**
     * @param target the test case instance
     * @return a list of MethodRules that should be applied when executing this
     *         test
     */
    private List<MethodRule> rules(final JUnitTest test, final Object target) {
        final List<MethodRule> rules = test.testClass().getAnnotatedMethodValues(target, Rule.class, MethodRule.class);

        rules.addAll(test.testClass().getAnnotatedFieldValues(target, Rule.class, MethodRule.class));

        return rules;
    }

    private Statement withTestRules(JUnitTest test, List<TestRule> testRules,
                                    Statement statement) {
        return testRules.isEmpty() ? statement :
                new RunRules(statement, testRules, test.description());
    }

    private List<TestRule> getTestRules(JUnitTest test, Object target) {
        List<TestRule> result = test.testClass().getAnnotatedMethodValues(target, Rule.class, TestRule.class);

        result.addAll(test.testClass().getAnnotatedFieldValues(target, Rule.class, TestRule.class));

        return result;
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

        Statement statement = methodInvoker(method, testObj);
        statement = possiblyExpectingExceptions(method, testObj, statement);
        statement = withPotentialTimeout(method, testObj, statement);
        statement = withBefores(test, testObj, statement);
        statement = withAfters(test, testObj, statement);
        statement = withRules(test, testObj, statement);
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
            try {
                Statement statement = usingClassRunner(test);

                if (statement == null) {
                    try {
                        statement = methodBlock(test);

                        eachNotifier.fireTestStarted();

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
                } else {
                    // Here we assume that the statement returned will handle all beforeclass/afterclass and related methods.
                    // it show also take care of notification (via the notifier field in this class).
                    statement.evaluate();
                }
            }
            // We catch these exceptions because the use of Statement.evaluate forces it,
            // but these should be handled already (either inside the try or by the statement's execution).
            catch (AssumptionViolatedException e) {
                eachNotifier.addFailedAssumption(e);
            } catch (Throwable e) {
                eachNotifier.addFailure(e);
            }
        }
    }

    private Statement usingClassRunner(final JUnitTest test) {
        try {
            final Runner runner = new AnnotatedBuilder(null).runnerForClass(test.javaClass());

            if (runner != null) {
                return new Statement() {
                    @Override
                    public void evaluate() throws Throwable {
                        Filter.matchMethodDescription(test.description()).apply(runner);
                        runner.run(notifier);
                    }
                };
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
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
