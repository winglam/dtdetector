package edu.washington.cs.dt.util;

public class TestNotFoundException extends RuntimeException {
    public TestNotFoundException(final JUnitTest test) {
        super("Test method not found: " + test.name());
    }
}
