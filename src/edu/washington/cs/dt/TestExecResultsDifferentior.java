/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.dt.main.Main;
import edu.washington.cs.dt.util.Utils;

public class TestExecResultsDifferentior {
	
	public final TestExecResult intendedResults; //the fixed-order execution
	public final TestExecResults comparingResults; //the results for comparing
	
	private final boolean removeredundancy;
	
	public TestExecResultsDifferentior(TestExecResult intendedResults,
			TestExecResults comparingResults) {
		this.intendedResults = intendedResults;
		this.comparingResults = comparingResults;
		this.removeredundancy = Main.removeredundancy;
	}
	
	public List<TestExecResultsDelta> diffResults() {
		List<String> intendedExecutedTests = this.intendedResults.getAllTests();
		
		List<TestExecResultsDelta> rets = new LinkedList<TestExecResultsDelta>();
		for(TestExecResult cr : comparingResults.getExecutionRecords()) {
			Map<String, OneTestExecResult> singleRun = cr.singleRun;
			List<String> executedTests = new LinkedList<String>();
			//add the key set in order
			executedTests.addAll(singleRun.keySet());
			for(int i = 0; i < executedTests.size(); i++) {
				String t = executedTests.get(i);
				OneTestExecResult r = singleRun.get(t);
				//lookup intended results
				OneTestExecResult intend = this.intendedResults.getResult(t);
				Utils.checkNull(intend, "The test: " + t + " does not exist in intended behaviors.");
				
				if(!r.equals(intend)) {
					List<String> deps = executedTests.subList(0, i);
					List<String> preExecutedTests = intendedExecutedTests.subList(0, intendedExecutedTests.indexOf(t));
					TestExecResultsDelta delta = new TestExecResultsDelta(t, intend, preExecutedTests, r, deps);
					rets.add(delta);
				}
			}
		}
		
		if(this.removeredundancy) {
			Utils.stdln("Number of dependent tests before removing redundancy: " + rets.size());
			rets = TestExecResultsDelta.removeRedundancy(rets);
			Utils.stdln("Number of dependent tests after removing redundancy: " + rets.size());
		}
		
		return rets;
	}
	
}

