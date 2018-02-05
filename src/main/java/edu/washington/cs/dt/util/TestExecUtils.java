/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.util;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestFailure;
import plume.Option;
import edu.washington.cs.dt.OneTestExecResult;
import edu.washington.cs.dt.RESULT;
import edu.washington.cs.dt.main.Main;


public class TestExecUtils {

    public static String testResultSep = "#";

    public static String resultExcepSep = "%#%#";

    public static String timeSep = "?";

    public static String noStackTrace = "NO_STACK_TRACE_FOR_A_PASSING_TEST";

    @Option("The temp file to store all tests to execute")
    public static String testsfile = "./tmptestfiles.txt";

    @Option("The min number of tests when using ./tmptestfiles")
    public static int threshhold = 120;
    public static String lockFile = "LOCK_FILE";
    public static String exitFileName = "EXIT_FILE";

    public static boolean fork_test_execution = true;

    public Map<String, OneTestExecResult> executeTestsInFreshJVM(String classPath, String outputFile, List<String> tests, String append) {
    	if (fork_test_execution) {
    		return executeTestsInFreshJVMForkTestExecution(classPath, outputFile, tests, append);
    	} else {
    		List<String> commandList = new LinkedList<String>();
    		Files.createIfNotExistNoExp(testsfile+append);
    		Files.writeToFileWithNoExp(tests, testsfile+append);
    		commandList.add(outputFile);
    		commandList.add(testsfile+append);
    		commandList.add("true");

    		String[] args = commandList.toArray(new String[0]);
    		int testsExecuted = 0;
    		Map<String, OneTestExecResult> testResults = null;
    		try {
    			testsExecuted = TestRunnerWrapperFileInputs.runTests(args);
        		testResults = parseTestResults(outputFile);

    			File exitFile = new File(exitFileName+append);
    			File file = new File(lockFile+append);
	    		file.delete();
	    		exitFile.delete();
    	        File tmpFile = new File(outputFile);
    			File tmpTestfile = new File(testsfile+append);
    			tmpFile.delete();
    			tmpTestfile.delete();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		Utils.checkTrue(testsExecuted == testResults.size(), "Test num not equal. Results is size " + testResults.size() + ". Tests is size " + testsExecuted + ".");
    		return testResults;
    	}
    }
    public Map<String, OneTestExecResult> executeTestsInFreshJVMForkTestExecution(String classPath, String outputFile, List<String> tests, String append) {

        List<String> commandList = new LinkedList<String>();
        commandList.add("java");
        commandList.add("-cp");
        commandList.add(classPath + Globals.pathSep + System.getProperties().getProperty("java.class.path", null));

//        if(tests.size() < threshhold) {
//            commandList.add("edu.washington.cs.dt.util.TestRunnerWrapper");
//            commandList.add(outputFile);
//            commandList.addAll(tests);
//        } else {
        Files.createIfNotExistNoExp(testsfile+append);
        Files.writeToFileWithNoExp(tests, testsfile+append);

        commandList.add("edu.washington.cs.dt.util.TestRunnerWrapperFileInputs");
        commandList.add(outputFile);
        commandList.add(testsfile+append);
        
        commandList.add(append);
//        }

        String[] args = commandList.toArray(new String[0]);

        File exitFile = new File(exitFileName+append);
		File file = new File(lockFile+append);
    	try{
    		file.delete();
    		exitFile.delete();
    	}catch(Exception e){
    		e.printStackTrace();
    	}

        Process proc = Command.execProc(args, System.out, "", false);

        while (!file.exists() && !exitFile.exists()) {
        	try {
        	    Thread.sleep(1000);
        	} catch(InterruptedException ex) {
        	    Thread.currentThread().interrupt();
        	}
        }

        proc.destroy();

        if (exitFile.exists()) {
        	try {
        		file.delete();
        		exitFile.delete();
        	} catch(Exception e){
        		e.printStackTrace();
        	}
        	System.err.println("Exit file detected.");
        	System.exit(0);
        } else {
        	try {
        		file.delete();
        		exitFile.delete();
        	} catch(Exception e){
        		e.printStackTrace();
        	}
        }

        Map<String, OneTestExecResult> testResults = parseTestResults(outputFile);

        Utils.checkTrue(tests.size() == testResults.size(), "Test num not equal. Results is size " + testResults.size() + ". Tests is size " + tests.size() + ".");

        return testResults;
    }
    
    
    /*
     * Executes a list of tests in order by launching a fresh JVM, and
     * returns the result of each test.
     * 
     * The test is in the form of packageName.className.methodName
     * */
    public static Map<String, OneTestExecResult> executeTestsInFreshJVM(String classPath, String outputFile, List<String> tests) {
    	if (fork_test_execution) {
    		return executeTestsInFreshJVMForkTestExecution(classPath, outputFile, tests);
    	} else {
    		List<String> commandList = new LinkedList<String>();
    		Files.createIfNotExistNoExp(testsfile);
    		Files.writeToFileWithNoExp(tests, testsfile);
    		commandList.add(outputFile);
    		commandList.add(testsfile);
    		commandList.add("true");

    		String[] args = commandList.toArray(new String[0]);
    		int testsExecuted = 0;
    		Map<String, OneTestExecResult> testResults = null;
    		try {
    			testsExecuted = TestRunnerWrapperFileInputs.runTests(args);
        		testResults = parseTestResults(outputFile);

    			File exitFile = new File(exitFileName);
    			File file = new File(lockFile);
	    		file.delete();
	    		exitFile.delete();
    	        File tmpFile = new File(outputFile);
    			File tmpTestfile = new File(testsfile);
    			tmpFile.delete();
    			tmpTestfile.delete();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		Utils.checkTrue(testsExecuted == testResults.size(), "Test num not equal. Results is size " + testResults.size() + ". Tests is size " + testsExecuted + ".");
    		return testResults;
    	}
    }

    public static Map<String, OneTestExecResult> executeTestsInFreshJVMForkTestExecution(String classPath, String outputFile, List<String> tests) {

        List<String> commandList = new LinkedList<String>();
        commandList.add("java");
        commandList.add("-cp");
        commandList.add(classPath + Globals.pathSep + System.getProperties().getProperty("java.class.path", null));

//        if(tests.size() < threshhold) {
//            commandList.add("edu.washington.cs.dt.util.TestRunnerWrapper");
//            commandList.add(outputFile);
//            commandList.addAll(tests);
//        } else {
        Files.createIfNotExistNoExp(testsfile);
        Files.writeToFileWithNoExp(tests, testsfile);

        commandList.add("edu.washington.cs.dt.util.TestRunnerWrapperFileInputs");
        commandList.add(outputFile);
        commandList.add(testsfile);
//        }

        String[] args = commandList.toArray(new String[0]);

        File exitFile = new File(exitFileName);
		File file = new File(lockFile);
    	try{
    		file.delete();
    		exitFile.delete();
    	}catch(Exception e){
    		e.printStackTrace();
    	}

        Process proc = Command.execProc(args, System.out, "", false);

        while (!file.exists() && !exitFile.exists()) {
        	try {
        	    Thread.sleep(1000);
        	} catch(InterruptedException ex) {
        	    Thread.currentThread().interrupt();
        	}
        }

        proc.destroy();

        if (exitFile.exists()) {
        	try {
        		file.delete();
        		exitFile.delete();
        	} catch(Exception e){
        		e.printStackTrace();
        	}
        	System.err.println("Exit file detected.");
        	System.exit(0);
        } else {
        	try {
        		file.delete();
        		exitFile.delete();
        	} catch(Exception e){
        		e.printStackTrace();
        	}
        }

        Map<String, OneTestExecResult> testResults = parseTestResults(outputFile);

        Utils.checkTrue(tests.size() == testResults.size(), "Test num not equal. Results is size " + testResults.size() + ". Tests is size " + tests.size() + ".");

        return testResults;
    }

    static Map<String, OneTestExecResult> parseTestResults(String outputFile) {
        Map<String, OneTestExecResult> ret = new LinkedHashMap<String, OneTestExecResult>();

        List<String> lines = Files.readWholeNoExp(outputFile);

        for(String line : lines) {
            int resultSepIndex = line.indexOf(TestExecUtils.testResultSep);
            int excepSepIndex = line.indexOf(TestExecUtils.resultExcepSep);
            int timeSepIndex = line.indexOf(timeSep);
            Utils.checkTrue(resultSepIndex != -1, "resultSepIndex != -1");
            Utils.checkTrue(excepSepIndex != -1, "excepSepIndex != -1");
            Utils.checkTrue(timeSepIndex != -1, "timeSepIndex != -1");

            String testCase = line.substring(0, timeSepIndex);
            String time = line.substring(timeSepIndex + timeSep.length(), resultSepIndex);
            String result = line.substring(resultSepIndex + TestExecUtils.testResultSep.length(), excepSepIndex);
            String fullStacktrace = line.substring(excepSepIndex + TestExecUtils.resultExcepSep.length(), line.length());
            if(result.equals(RESULT.PASS.name())) {
                OneTestExecResult r = new OneTestExecResult(RESULT.PASS, fullStacktrace, Long.parseLong(time));
                ret.put(testCase, r);
            } else if (result.equals(RESULT.FAILURE.name())) {
                OneTestExecResult r = new OneTestExecResult(RESULT.FAILURE, fullStacktrace, Long.parseLong(time));
                ret.put(testCase, r);
            } else if (result.equals(RESULT.ERROR.name())) {
                OneTestExecResult r = new OneTestExecResult(RESULT.ERROR, fullStacktrace, Long.parseLong(time));
                ret.put(testCase, r);
            } else {
                throw new RuntimeException("Unknown result: " + result);
            }
        }

        return ret;
    }

    public static final String JUNIT_ASSERT = "junit.framework.Assert";

    public static String flatStackTrace(TestFailure failure, String excludeRegex) {
        Pattern p = Pattern.compile(excludeRegex);
        Throwable t = failure.thrownException();
        String[] stackTraces = extractStackTraces(t);
        String flatString = flatStrings(stackTraces, p, JUNIT_ASSERT);
        return flatString;
    }

    public static String stackTraceSep = " - ";

    public static String flatStrings(String[] strs, Pattern excludeRegex, String exceptedPrefix) {
        StringBuilder sb = new StringBuilder();
        for(String str : strs) {
            if(shouldExclude(str, excludeRegex, exceptedPrefix)) {
                continue;
            }
            if(str.startsWith("edu.washington.cs.dt")) {
                continue;
            }
            sb.append(str);
            //			sb.append(" - ");
            sb.append(stackTraceSep);
        }
        return sb.toString();
    }

    public static String flatStrings(String[] strs) {
        StringBuilder sb = new StringBuilder();
        for(String str : strs) {
            sb.append(str);
            sb.append(stackTraceSep);
        }
        return sb.toString();
    }

    public static String flatFilteredStackTraces(String fullStackTrace) {
        String[] elements = fullStackTrace.split(stackTraceSep);
        Pattern p = Pattern.compile(Main.excludeRegex);
        String flatString = TestExecUtils.flatStrings(elements, p, TestExecUtils.JUNIT_ASSERT);
        return flatString;
    }

    public static String[] extractStackTraces(Throwable t) {
        String[] traces = new String[t.getStackTrace().length];
        for(int i = 0; i < t.getStackTrace().length; i++) {
            traces[i] = t.getStackTrace()[i].toString().trim();
        }
        return traces;
    }

    public static boolean shouldExclude(String target, Pattern p, String exceptedPrefix) {
        Matcher m = p.matcher(target);
        if( m.find() && !(target.startsWith(exceptedPrefix))) {
            return true;
        }
        return false;
    }
}
