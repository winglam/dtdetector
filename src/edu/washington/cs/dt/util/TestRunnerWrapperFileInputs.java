/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.util;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Beaware, also need to change TestRunnerWrapper
 * */

public class TestRunnerWrapperFileInputs {
    /*
     * args[0]: the result output file
     * args[1]: a file containing all tests
     * */
    public static void main(String[] args) throws IOException {
        if(args.length != 2) {
            System.err.println("The arg number must be 2: args[0] " +
                    "is the output file, args[1] is the file containing all unit tests.");
            System.exit(2);
        }
        /*parse the argument*/
        String outputFile = args[0];
        List<String> content = Files.readWholeNoExp(args[1]);
        List<String> tests = new LinkedList<String>();
        for(String line : content) {
            if(!line.trim().equals("")) {
                tests.add(line.trim());
            }
        }

        /*create the StringBuilder to output results*/
        StringBuilder sb = new StringBuilder();
        for(String fullTestName : tests) {
        	System.out.println("Test being executed: " + fullTestName);

            /*check the results*/
            String result = null;
            //			String stackTrace = TestExecUtils.noStackTrace;
            String fullStackTrace = TestExecUtils.noStackTrace;

            JUnitTestExecutor executor = null;
        	try {
                executor = new JUnitTestExecutor(fullTestName);
        	} catch (ClassNotFoundException e) {
        		Files.writeToFile("", TestExecUtils.exitFileName);
        		e.printStackTrace();
        		System.exit(0);
        	}
            long start = System.nanoTime();
            executor.executeWithJUnit4Runner();
            long interval = System.nanoTime() - start;
            result = executor.getResult();
            //				stackTrace = executor.getStackTrace();
            fullStackTrace = executor.getFullStackTrace();

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
            if(dir != null && !dir.exists()) {
                created = dir.mkdirs();
            }
            created = created & f.createNewFile();
            if(!created) {
                throw new RuntimeException("Cannot create: " + outputFile);
            }
        }
        Files.writeToFile(sb.toString(), outputFile);
        Files.writeToFile("", TestExecUtils.lockFile);
    }
}
