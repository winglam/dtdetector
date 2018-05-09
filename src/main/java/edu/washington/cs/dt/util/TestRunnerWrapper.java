/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.textui.TestRunner;
import edu.washington.cs.dt.RESULT;
import edu.washington.cs.dt.main.ImpactMain;

/**
 * Beaware, also need to change TestRunnerWrapperFileInputs
 * */

public class TestRunnerWrapper {

    /*
     * args[0]: the result output file
     * args[1 : ]: the tests to be executed
     * */
    public static void main(String[] args) throws IOException {
        /*parse the argument*/
        String outputFile = args[0];
        String[] tests = new String[args.length - 1];
        for(int index = 1; index < args.length; index++) {
            tests[index - 1] = args[index];
        }
        /*create the StringBuilder to output results*/
        StringBuilder sb = new StringBuilder();

        try {
            final JUnitTestExecutor executor = JUnitTestExecutor.testOrder(Arrays.asList(tests));
            for (final JUnitTestResult result : executor.executeWithJUnit4Runner(false)) {
                result.output(sb);
            }

            //if not exist, create it
            File f = new File(outputFile);
            if(!f.exists()) {
                File dir = f.getParentFile();
                boolean created = true;
                if(dir != null && !dir.exists()) {
                    created = dir.mkdirs();
                }
                created = created & f.createNewFile();
                if(!created) {
                    throw new RuntimeException("Cannot create: " + outputFile);
                }
            }
            Files.writeToFile(sb.toString(), outputFile);
            Files.writeToFile("", TestExecUtils.lockFile + (args.length > 2 ? args[2] : ""));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // This is old code for running JUnit 3 tests.
        // However, it seems that JUnit 3 tests are actually compatible with JUnit 4, so there is no reason to have this.
        /*
        TestRunner aTestRunner = new TestRunner();

        long interval;
        for(String fullTestName : tests) {
        	System.out.println("Test being executed: " + fullTestName);
            String result = null;
            //String stackTrace = TestExecUtils.noStackTrace;
            String fullStackTrace = TestExecUtils.noStackTrace;
            try {
                String[] junitArgs = new String[]{"-m", fullTestName};
                // System.out.println(Utils.convertArrayToFlatString(junitArgs));
                long start = System.nanoTime();
                TestResult r = aTestRunner.start(junitArgs);
                interval = System.nanoTime() - start;
                if (r.wasSuccessful()) {
                    result = RESULT.PASS.name();
                } else {
                    if (r.errorCount() > 0) {
                        Utils.checkTrue(r.errorCount() == 1,
                                "Only execute 1 test: " + fullTestName + ", two errors: "
                                        + CodeUtils.flattenFailrues(r.errors()));
                        result = RESULT.ERROR.name();
                        TestFailure failure = r.errors().nextElement();
                        //stackTrace = TestExecUtils.flatStackTrace(failure, Main.excludeRegex);
                        fullStackTrace = TestExecUtils.flatStrings(TestExecUtils.extractStackTraces(failure.thrownException()));
                    }
                    if (r.failureCount() > 0) {
                        Utils.checkTrue(r.failureCount() == 1,
                                "Only execute 1 test: " + fullTestName + ", two failures: "
                                        + CodeUtils.flattenFailrues(r.failures()));
                        result = RESULT.FAILURE.name();
                        TestFailure failure = r.failures().nextElement();
                        //stackTrace = TestExecUtils.flatStackTrace(failure,
                        //		Main.excludeRegex);
                        fullStackTrace = TestExecUtils.flatStrings(TestExecUtils.extractStackTraces(failure.thrownException()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            sb.append(fullTestName);
            sb.append(TestExecUtils.timeSep);
            sb.append(interval);
            sb.append(TestExecUtils.testResultSep);
            sb.append(result);
            sb.append(TestExecUtils.resultExcepSep);
            //			sb.append(stackTrace);
            sb.append(fullStackTrace);
            sb.append(Globals.lineSep);
        }

        //if not exist, create it
        File f = new File(outputFile);
        if(!f.exists()) {
            File dir = f.getParentFile();
            boolean created = true;
            if(!dir.exists()) {
                created = dir.mkdirs();
            }
            created = created & f.createNewFile();
            if(!created) {
                throw new RuntimeException("Cannot create: " + outputFile);
            }
        }
        Files.writeToFile(sb.toString(), outputFile);
        Files.writeToFile("", TestExecUtils.lockFile);
        */
    }

}
