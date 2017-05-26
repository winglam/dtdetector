/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.main;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.washington.cs.dt.DependentTestIdentifier;
import edu.washington.cs.dt.TestExecResultsDelta;
import edu.washington.cs.dt.util.Files;
import edu.washington.cs.dt.util.Globals;
import edu.washington.cs.dt.util.TestExecUtils;
import edu.washington.cs.dt.util.Utils;
import plume.Option;
import plume.Options;
import plume.Unpublicized;

/*
 * The entry point
 * */
public class Main {
	
	/**
	 * Basic workflow:
	 * input: a file containing all junit tests
	 *        specify classpath
	 *        specify tmpOutputFile
	 * output: a report output file (default by print)
	 * */
   
	public static String VERSION = "Dependent Test Detector, Version 0.2, May 18, 2012";
	
	@Option("Show all options")
	public static boolean help = false;
	
	@Option("Execute JUnit4 tests - an experimental option")
	public static boolean junit4 = false;
	
	@Option("Print out all progress messages")
	public static boolean verbose = true;
	
	@Option("The classpath for executing the tests")
	public static String classpath = "";
	
	@Option("A file containing all tests to be executeds")
	public static String tests = null;
	
	@Option("A file containing all intermediate temp results")
	public static String tmpfile = "." + File.separator + "tmpfile.txt";
	
	@Option("A file containing the final report")
	public static String report = "." + File.separator + "report.txt";
	
	@Option("A file containing the execution results of fixed order execution")
	public static String fixedOrderReport =  null;
	
	@Option("A file containing the execution results of isolation execution")
	public static String isolationReport =  null;
	
	@Option("Print stack trace for inspection")
	public static boolean printstacktrace = false;
	
	@Option("Print the test sequence executed before a test")
	public static boolean printexecseq = true;
	
	@Option("Compare stack trace")
	public static boolean comparestacktrace = true;
	
	@Option("Use delta debugging to minize tests")
	public static boolean minimize = false;
	
	@Option("Enable cache to improve performance in minimzing tests")
	public static boolean enablecache = true;
	
	@Option("Remove redundant reports after minimizing dependent tests")
	public static boolean removeredundancy = true;
	
	@Option("Execute tests in isolation to check the results")
	public static boolean isolate = false;
	
	@Option("Execute tests in a reverse order to check the results")
	public static boolean reverse = false;
	
	@Option("Execute tests in all possible combinations")
	public static boolean combination = false;
	
	@Option("Execute tests in a randomized order")
	public static boolean randomize = false;
	
	@Option("How many rounds to randomize the test list")
	public static int round = 1;
	
	@Option("The length of each combination")
	public static int k = -1;
	
	@Option("The excluded stack trace regular expression")
	public static String excludeRegex = "^junit.";
	
	@Option("Show progress in terms of percentage. It will make the console messy.")
	public static boolean showProgress = false;
	
	@Option("Run the tool in multiple processes in a single machine")
	public static int processnum = 1;
	
	@Unpublicized
	@Option("Remove the temp file")
	public static boolean removetempfile = true;
	
	/* see lanuchDetector for details about how to initialize the detector*/
	public static void main(String[] args) {
		new Main().nonStaticMain(args);
	}
	
	/**
	 * methods for processing the input arguments, and launching the tool
	 * */
	private void nonStaticMain(String[] args) {
		this.parse_and_validate_args(args);
		lanchDetector();
		//clean up
		if(removetempfile) {
			File f = new File(tmpfile);
			if(f.exists()) {
			    Files.deleteFile(tmpfile);
			}
		}
	}
	
	private void parse_and_validate_args(String[] args) {
		Options options = new Options("Dependent Unit Tests Detector usage: ", Main.class, TestExecUtils.class);
	    String[] file_args = options.parse_or_usage(args);
	    if(file_args.length != 0) {
	        Utils.flushToStd(file_args);
	        System.exit(1);
	    }
	    if(help) {
	    	Utils.flushToStd(new String[]{VERSION});
	    	Utils.flushToStd(options.usage());
	        System.exit(1);
	    }
	    List<String> errorMsg = new LinkedList<String>();
	    if(tests == null) {
	    	errorMsg.add("You must specify either a file containing all tests via -tests");
	    }
	    if(processnum < 1) {
	    	errorMsg.add("You must specifiy a positive process num via --processnum");
	    }
	    int chosenOptions = 0;
	    if(isolate) { chosenOptions++; }
	    if(reverse) { chosenOptions++; }
	    if(randomize) {chosenOptions++;}
	    if(combination) {
	    	if(k < 1) {
	    		errorMsg.add("The option k must be specified via --k, when --combination is specified");
	    	}
	    	chosenOptions++;
	    }
	    if(chosenOptions == 0) {
	    	errorMsg.add("You must chose one option for detecting dependent tests: --isolate, --reverse, or --combination.");
	    }
	    if(chosenOptions > 1) {
	    	errorMsg.add("You can only chose one option for detecting dependent tests: --isolate, --reverse, or --combination.");
	    }
	    if(round < 1) {
	    	errorMsg.add("You should at least specifiy 1 round via --round");
	    }
	    if(!errorMsg.isEmpty()) {
	    	Utils.flushToStd(errorMsg.toArray(new String[0]));
	    	Utils.flushToStd(options.usage());
	        System.exit(1);
	    }
	    //set the verbose
	    Utils.VERBOSE = verbose;
	}
	
	private void lanchDetector() {
		List<String> allTests = Files.readWholeNoExp(tests);
		DependentTestIdentifier detector = new DependentTestIdentifier(allTests);
		detector.setClasspath(classpath);
		detector.setTmpOutputFile(tmpfile);
		detector.setMinimize(minimize);
		//keep all test execution differences
		List<TestExecResultsDelta> diffs = new LinkedList<TestExecResultsDelta>();
		if(isolate) {
		    diffs = detector.findDependenceForIsolation();
		} else if (reverse) {
			diffs = detector.findDependenceForReverse();
		} else if (combination) {
			diffs = detector.findDependenceForCombination(k);
		} else if (randomize) {
			if(round == 1) {
			    diffs = detector.findDependenceForRandomization();
			} else {
				Set<String> dependentTests = new LinkedHashSet<String>();
				//randomize for a few times
				for(int i = 0; i < round; i++) {
					diffs = detector.findDependenceForRandomization();
					TestExecResultsDelta.writeToFile(diffs,
							new File(report).getParentFile().getAbsolutePath() + Globals.fileSep + "randomize_" + i+ ".txt");
					for(TestExecResultsDelta diff : diffs) {
						dependentTests.add(diff.testName);
					}
				}
				Files.writeToFileWithNoExp(dependentTests,
						new File(report).getParentFile().getAbsolutePath() + Globals.fileSep + "all_tests.txt");
				return;
			}
		} else {
			throw new RuntimeException("Bugs in argument processing.");
		}
		//write the result to file
		TestExecResultsDelta.writeToFile(diffs, report);
	}
}