package edu.washington.cs.dt.util;

import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.RunWith;

public class JUnitTest {
    private final String fullMethodName;
    private final Class<?> clzName;
    private final int i;
    private final String junitMethod;

    public JUnitTest(final String fullMethodName) throws ClassNotFoundException {
        this(fullMethodName, 0);
    }

    public JUnitTest(final String fullMethodName, int i) throws ClassNotFoundException {
        this.fullMethodName = fullMethodName;

        final String className = fullMethodName.substring(0, this.fullMethodName.lastIndexOf("."));
        this.clzName = Class.forName(className);

        this.junitMethod = fullMethodName.substring(this.fullMethodName.lastIndexOf(".") + 1);

        this.i = i;
    }

    public JUnitTest(String className, String junitMethod) {
        try {
            this.clzName = Class.forName(className);
            this.fullMethodName = this.clzName.getName() + "." + junitMethod;
            this.junitMethod = junitMethod;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        i = 0;
    }

    public boolean isClassCompatible() {
        RunWith annotations = clzName.getAnnotation(RunWith.class);
        return annotations == null || !annotations.value().getName().equals("org.junit.runners.Parameterized");
    }

    public JUnitTest(Class<?> clzName, String junitMethod) {
        this.clzName = clzName;
        this.fullMethodName = this.clzName.getName() + "." + junitMethod;
        this.junitMethod = junitMethod;
        i = 0;
    }

    public Description description() {
        return Description.createTestDescription(clzName, junitMethod);
    }

    public String name() {
        return fullMethodName;
    }

    public Class<?> testClass() {
        return clzName;
    }

    public int index() {
        return i;
    }

    public String methodName() {
        return junitMethod;
    }

    public Request request() {
        return Request.method(clzName, junitMethod);
    }
}
