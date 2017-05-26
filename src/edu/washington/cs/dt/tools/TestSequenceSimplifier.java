/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.tools;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.dt.OneTestExecResult;
import edu.washington.cs.dt.dd.DependentTestSetMinimizer;
import edu.washington.cs.dt.main.Main;
import edu.washington.cs.dt.util.Files;
import edu.washington.cs.dt.util.Globals;
import edu.washington.cs.dt.util.Log;
import edu.washington.cs.dt.util.TestExecUtils;
import edu.washington.cs.dt.util.Utils;
import plume.Option;
import plume.Options;

public class TestSequenceSimplifier {
	
	@Option("Show all options")
	public static boolean help = false;
	
	@Option("A test file consisting of all tests to be simplify.\n"
			+ "Note:  this class simplifies the test sequence before the *last* test, \n"
			+ "       and make the last test result the same as executed in the original order.")
	public static String testFile = null;
	
	@Option("A file containing all simplified test list")
	public static String outputFile = "./simplified_testlist.txt";
	
	@Option("Log file for recording delta debugging steps")
	public static String logfile = null;
	
	@Option("Use cache or not")
	public static boolean enablecache = false;
	
	@Option("Compare stack trace or not")
	public static boolean comparestacktrace = false;
	
	public static void main(String[] args) throws IOException {
		new TestSequenceSimplifier().nonStaticMain(args);
	}
	
	private void nonStaticMain(String[] args) throws IOException {
		parse_and_valid_args(args);
		//simplify the sequence
		simplify_sequence();
	}
	
	private void simplify_sequence() throws IOException {
		List<String> allTests = Files.readWhole(testFile);
		Utils.checkTrue(allTests.size() > 1, "The number of tests must > 1, but now it is: " + allTests.size());
		String dependentTest = allTests.get(allTests.size() - 1);
		List<String> preTests = allTests.subList(0, allTests.size() - 1);
		
		List<String> outputTestList = new LinkedList<String>();
		
		Map<String, OneTestExecResult> fixedExecResults = TestExecUtils.executeTestsInFreshJVM(Main.classpath, Main.tmpfile, allTests);
		OneTestExecResult fixedOrderResult = fixedExecResults.get(dependentTest);
		Utils.checkNull(fixedOrderResult, "");
		
		Map<String, OneTestExecResult> isolationResults = TestExecUtils.executeTestsInFreshJVM(Main.classpath, Main.tmpfile,
				Collections.singletonList(dependentTest));
		OneTestExecResult isolateResult = isolationResults.get(dependentTest);
		Utils.checkNull(isolateResult, "");
		
		//need to simplify, otherwise, not
		if(!fixedOrderResult.equals(isolateResult)) {
		    DependentTestSetMinimizer minimizer = new DependentTestSetMinimizer(preTests, dependentTest, fixedOrderResult,
				Main.classpath, Main.tmpfile);
		    List<String> minimizedTestList = minimizer.minimize();
		    outputTestList.addAll(minimizedTestList);
		}
		
		//append dependent test at the end
		outputTestList.add(dependentTest);
		
		StringBuilder sb = new StringBuilder();
		for(String minDep : outputTestList) {
			sb.append(minDep);
			sb.append(Globals.lineSep);
		}
		
		Files.writeToFile(sb.toString(), outputFile);
	}
	
	private void parse_and_valid_args(String[] args) {
		Options options = new Options("TestSequenceSimplifier usage: ", TestSequenceSimplifier.class);
	    String[] file_args = options.parse_or_usage(args);
	    if(file_args.length != 0) {
	        Utils.flushToStd(file_args);
	        System.exit(1);
	    }
	    if(help) {
	    	Utils.flushToStd(options.usage());
	        System.exit(1);
	    }
	    if(logfile != null) {
	    	Log.logConfig(logfile);
	    }
	    Main.enablecache = enablecache;
	    Main.comparestacktrace = comparestacktrace;
	    List<String> errorMsg = new LinkedList<String>();
	    if(testFile == null) {
	    	errorMsg.add("You must specify either a file containing all tests via --testFile");
	    }
	    if(!errorMsg.isEmpty()) {
	    	Utils.flushToStd(errorMsg.toArray(new String[0]));
	    	Utils.flushToStd(options.usage());
	        System.exit(1);
	    }
	}
}