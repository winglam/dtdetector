/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.tools;

import edu.washington.cs.dt.TestExecResult;
import edu.washington.cs.dt.TestExecResults;
import edu.washington.cs.dt.runners.FixedOrderRunner;
import edu.washington.cs.dt.tools.RunTestInFixedOrder;
import edu.washington.cs.dt.util.Log;
import edu.washington.cs.dt.util.Utils;
import junit.framework.TestCase;

public class TestRunTestInFixedOrder extends TestCase {
	
	public void testExamples() {
		RunTestInFixedOrder.main(new String[]{"--testFile=./tests/edu/washington/cs/dt/main/sampleinput.txt"});
	}
	
	public void testCrystal_fixedorder() {
		String testFile = TestRandomizedDependentTestFinder.crystalFile_manual;
		String logFile = "./fixed_order_crystal_results.txt";
		runTestInFixedOrderAndSaveResults(testFile, logFile);
	}
	
	public void testSynoptic_fixedorder() {
		String testFile = TestRandomizedDependentTestFinder.synopticFile_manual;
		String logFile = "./fixed_order_synoptic_results.txt";
		runTestInFixedOrderAndSaveResults(testFile, logFile);
	}
	
	public void testJodatime_fixedorder() {
		String testFile = TestRandomizedDependentTestFinder.jodatimeFile_manual;
		String logFile = "./fixed_order_jodatime_results.txt";
		runTestInFixedOrderAndSaveResults(testFile, logFile);
	}
	
	public void testXMLSecurity_fixedorder() {
		String testFile = TestRandomizedDependentTestFinder.xmlSecurityFile_manual;
		String logFile = "./fixed_order_xmlsecurity_results.txt";
		runTestInFixedOrderAndSaveResults(testFile, logFile);
	}
	
	private void runTestInFixedOrderAndSaveResults(String testFile, String logFile) {
        Log.logConfig(logFile);
		
		FixedOrderRunner fixedRunner = new FixedOrderRunner(testFile);
		TestExecResults expected_results = fixedRunner.run();
		Utils.checkTrue(expected_results.getExecutionRecords().size() == 1,
				"The size is: " + expected_results.getExecutionRecords().size());
		TestExecResult rs = expected_results.getExecutionRecords().get(0);
		
		//write the results to the log file
		Log.logln("The default execution results: ");
		for(String test : rs.getAllTests()) {
			Log.logln("  Test: " + test + ": " + rs.getResult(test).result);
		}
		
		Log.log = null;
	}
	
}
