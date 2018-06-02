/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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
        try {
            runTests(args);
            System.exit(0);
        } catch (EmptyTestListException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    public static int runTests(String[] args) throws IOException {
        if(args.length < 2) {
            System.err.println("The arg number must be at least 2: args[0] " +
                    "is the output file, args[1] is the file containing all unit tests."
                    + "args[2] is optional to check whether there are incompatible tests and"
                    + "skips them.");
            System.exit(2);
        }
        /*parse the argument*/
        String outputFile = args[0];
        List<String> content = Files.readWholeNoExp(args[1]);

        List<String> argsList = Arrays.asList(args);

        boolean skipIncompatibleTests = argsList.contains("-skipIncompatibleTests");
        boolean skipMissingTests = argsList.contains("-skipMissingTests");
        boolean runSeparately = argsList.contains("-separate");

        List<String> tests = new LinkedList<String>();
        for(String line : content) {
            if(!line.trim().equals("")) {
                tests.add(line.trim());
            }
        }

        int testsExecuted = 0;
        try {
            /*create the StringBuilder to output results*/
            StringBuilder sb = new StringBuilder();
            for (final JUnitTestResult result : JUnitTestExecutor.runOrder(tests, skipMissingTests, runSeparately)) {
                result.output(sb);
                testsExecuted++;
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
        return testsExecuted;
    }
}
