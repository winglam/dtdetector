/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.tools;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.dt.OneTestExecResult;
import edu.washington.cs.dt.RESULT;
import edu.washington.cs.dt.TestExecResults;
import edu.washington.cs.dt.runners.AbstractTestRunner;
import edu.washington.cs.dt.runners.FixedOrderRunner;
import edu.washington.cs.dt.runners.RandomOrderRunner;
import edu.washington.cs.dt.util.Files;
import edu.washington.cs.dt.util.Globals;
import edu.washington.cs.dt.util.Utils;
import plume.Option;
import plume.Options;

public class RunTestInFixedOrder {
	
	@Option("See all options")
	public static boolean help = false;

	@Option("A file containing all tests to be execute")
	public static String testFile = null;
	
	@Option("The output file for test results")
	public static String outputFile = "./fixed_order_results.txt";
	
	@Option("Randomize the order")
	public static boolean random = false;
	
	@Option("Print detailed info")
	public static boolean verbose = false;
	
	public static void main(String[] args) {
		new RunTestInFixedOrder().nonStaticMain(args);
	}
	
	private void nonStaticMain(String[] args) {
		parse_and_valid_args(args);
		AbstractTestRunner runner = null;
		if(!random) {
		    runner = new FixedOrderRunner(testFile);
		} else {
			runner = new RandomOrderRunner(testFile);
		}
		
		TestExecResults results = runner.run();
		Utils.checkTrue(results.getExecutionRecords().size() == 1, "Just a single run");
		
		//get the results, and dump that out
		Map<String, OneTestExecResult> result = results.getExecutionRecords().get(0).singleRun;
		StringBuilder sb = new StringBuilder();
		StringBuilder failedTests = new StringBuilder();
		for(String test : result.keySet()) {
			sb.append(test);
			sb.append(" : ");
			sb.append(result.get(test).result);
			sb.append(Globals.lineSep);
			if(verbose) {
				if(!result.get(test).result.equals(RESULT.PASS)) {
					System.out.println(" test fail! " + test);
					System.out.println(result.get(test).getFullStackTrace());
					failedTests.append(test);
					failedTests.append(Globals.lineSep);
				}
			}
		}
		try {
			Files.writeToFile(sb.toString(), outputFile);
			if(verbose) {
				Files.writeToFile(failedTests.toString(), outputFile + "_only_failed.txt");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void parse_and_valid_args(String[] args) {
		Options options = new Options("RunTestInFixedOrder usage: ", RunTestInFixedOrder.class);
	    String[] file_args = options.parse_or_usage(args);
	    if(file_args.length != 0) {
	        Utils.flushToStd(file_args);
	        System.exit(1);
	    }
	    if(help) {
	    	Utils.flushToStd(options.usage());
	        System.exit(1);
	    }
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
