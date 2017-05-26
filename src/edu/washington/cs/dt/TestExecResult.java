/**
 * Copyright 2012 University of Washington. All Rights Reserved.
 * @author Sai Zhang
 */
package edu.washington.cs.dt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * TODO should add an exception stack trace here
 * to see the exactly error has been thrown
 */
public class TestExecResult {
    public final Map<String, OneTestExecResult> singleRun;

    protected List<String> allTests = new LinkedList<String>();

    /* The classified result after running all tests */
    protected List<String> passingTestsInOrder = new LinkedList<String>();
    protected List<String> failingTestsInOrder = new LinkedList<String>();
    protected List<String> errorTestsInOrder = new LinkedList<String>();
    protected Map<String, RESULT> nameToResultsMap = new HashMap<String, RESULT>();

    public TestExecResult(Map<String, OneTestExecResult> singleRun) {
        this.singleRun = singleRun;
        // classify each test
        for (String test : this.singleRun.keySet()) {
            allTests.add(test);
            // check the results
            OneTestExecResult r = this.singleRun.get(test);
            if (r.result.equals(RESULT.PASS)) {
                this.passingTestsInOrder.add(test);
                nameToResultsMap.put(test, RESULT.PASS);
            } else if (r.result.equals(RESULT.FAILURE)) {
                this.failingTestsInOrder.add(test);
                nameToResultsMap.put(test, RESULT.FAILURE);
            } else if (r.result.equals(RESULT.ERROR)) {
                this.errorTestsInOrder.add(test);
                nameToResultsMap.put(test, RESULT.ERROR);
            } else {
                throw new RuntimeException("Unknown results: " + r);
            }
        }
    }

    public List<String> getAllTests() {
        return this.allTests;
    }

    public OneTestExecResult getResult(String test) {
        return this.singleRun.get(test);
    }

    public boolean isTestPassed(String test) {
        return this.passingTestsInOrder.contains(test);
    }

    public boolean isTestFailed(String test) {
        return this.failingTestsInOrder.contains(test);
    }

    public boolean isTestError(String test) {
        return this.errorTestsInOrder.contains(test);
    }

    public Map<String, RESULT> getNameToResultsMap() {
        return nameToResultsMap;
    }

    public List<String> getValues() {
        List<String> retValues = new ArrayList<>();
        for (OneTestExecResult result : singleRun.values()) {
            retValues.add(result.toString());
        }
        return retValues;
    }

    @Override
    public String toString() {
        return this.singleRun.toString();
    }
}