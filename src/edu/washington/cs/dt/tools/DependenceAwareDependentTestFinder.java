package edu.washington.cs.dt.tools;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.washington.cs.dt.OneTestExecResult;
import edu.washington.cs.dt.TestExecResult;
import edu.washington.cs.dt.TestExecResults;
import edu.washington.cs.dt.runners.FixedOrderRunner;
import edu.washington.cs.dt.util.Files;
import edu.washington.cs.dt.util.Log;
import edu.washington.cs.dt.util.Utils;

//it tooks a set of files and run tests in each file separately
public class DependenceAwareDependentTestFinder {

    public final int k ;
	
	private final List<String> defaultTestList;
	
	private final Set<List<String>> candidateTestClusters;
	
	public static boolean verbose = true;
	
	public static boolean only_compare_outcome = true;
	
	public DependenceAwareDependentTestFinder(String defaultOrderFileName, String folderName, int k) {
		Utils.checkNull(defaultOrderFileName, "Should not be null");
		Utils.checkNull(folderName, "Should not be null");
		Utils.checkTrue(k >= 1, "Invalid k value: " + k);
		this.k = k;
		//read all tests
		this.defaultTestList = Files.readWholeNoExp(defaultOrderFileName);
		//get all cluster tests
		this.candidateTestClusters = TestReader.readTestsFromAllFiles(folderName, k);
		int count = 0;
		for(List<String> testList : candidateTestClusters) {
			count +=  testList.size();
		}
		//check the total num
//		Utils.checkTrue(count == this.defaultTestList.size(), "Diff size: " + count + ", v.s. " + this.defaultTestList.size());
	}
	
	public Set<String> findDependentTests() {
        long startTime = System.currentTimeMillis();
		
		//use linked hash set to keep the original order
		Set<String> depTests = new LinkedHashSet<String>();
		FixedOrderRunner fixedRunner = new FixedOrderRunner(this.defaultTestList);
		TestExecResults expected_results = fixedRunner.run();
		Utils.checkTrue(expected_results.getExecutionRecords().size() == 1,
				"The size is: " + expected_results.getExecutionRecords().size());
		TestExecResult rs = expected_results.getExecutionRecords().get(0);
		if(verbose) {
			Log.logln("The default execution results: ");
			for(String test : rs.getAllTests()) {
				Log.logln("  Test: " + test + ": " + rs.getResult(test).result);
			}
		}
		
		//run each k-paired tests
		int count = 0;
		for(List<String> currTests : this.candidateTestClusters) {
			if(verbose) {
				Log.logln(count + ", running tests: " + currTests);
			}
			
			FixedOrderRunner runner = new FixedOrderRunner(currTests);
			TestExecResults exec_results = runner.run();
			//find new dependent tests
			Set<String> diffTests = this.identifyTestsWithDifferentResults(expected_results, exec_results);
			depTests.addAll(diffTests);
			
			if(verbose && count % 100 == 0) {
				Log.logln("Time cost: " + (System.currentTimeMillis() - startTime)/1000 + " second");
				Log.logln("Number of dependent tests: " + depTests.size());
			}
			
			count++;
		}
		
		Log.logln("End, total time cost: " + (System.currentTimeMillis() - startTime)/1000 + " seconds");
		Log.logln("Number of dependent tests: " + depTests.size());
		for(String dt : depTests) {
			Log.logln("    " + dt);
		}
		
		return depTests;
	}
	
	private Set<String> identifyTestsWithDifferentResults(TestExecResults expected_results,
			TestExecResults exec_results) {
		Set<String> diffTests = new LinkedHashSet<String>();
		//check the number of JVM launches
		Utils.checkTrue(expected_results.getExecutionRecords().size() == 1, "Number of JVM launches: "
				+ expected_results.getExecutionRecords().size());
		Utils.checkTrue(exec_results.getExecutionRecords().size() == 1, "Number of JVM launches: "
				+ exec_results.getExecutionRecords().size());
		
		//get the test results
		TestExecResult default_results = expected_results.getExecutionRecords().get(0);
		TestExecResult actual_results = exec_results.getExecutionRecords().get(0);
		
//		System.out.println(default_results.getAllTests().size());
//		System.out.println(default_results.getAllTests());
		
		//check the number of the tests
		Utils.checkTrue(default_results.getAllTests().size() == this.defaultTestList.size(),
				"Size not equal: " + default_results.getAllTests().size());
		Utils.checkTrue(actual_results.getAllTests().size() == this.k,
				"Size not equal: " + actual_results.getAllTests().size() + ", this k: " + this.k);
		
		//then compare the test execution outcome
		for(String actualTest : actual_results.getAllTests()) {
//			System.out.println("actual test: " + actualTest);
			
			OneTestExecResult defaultTestResult = default_results.getResult(actualTest);
			OneTestExecResult actualTestResult = actual_results.getResult(actualTest);
			
			//the test is not executed in the default order, skip it
			if(defaultTestResult == null) {
				continue;
			}
			
			//compare the results
			if(!sameResults(defaultTestResult, actualTestResult)) {
				diffTests.add(actualTest);
				if(verbose) {
					Log.logln("  Diff Test: " + actualTest);
					Log.logln("    default result: " + defaultTestResult.result);
					Log.logln("    result in this run: " + actualTestResult.result);
				}
			}
		}
		
		return diffTests;
	}
	
	private boolean sameResults(OneTestExecResult r1, OneTestExecResult r2) {
//		System.out.println(r1);
//		System.out.println(r2);
		if(only_compare_outcome) {
			return r1.result.equals(r2.result);
		} else {
			return r1.equals(r2);
		}
	}
}
