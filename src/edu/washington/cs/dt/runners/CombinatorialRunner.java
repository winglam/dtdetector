/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.runners;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.dt.OneTestExecResult;
import edu.washington.cs.dt.TestExecResults;
import edu.washington.cs.dt.main.Main;
import edu.washington.cs.dt.util.PermutationGenerator;
import edu.washington.cs.dt.util.TestExecUtils;

public class CombinatorialRunner extends AbstractTestRunner {
	
	private final int k;

	public CombinatorialRunner(String fileName, int k) {
		super(fileName);
		this.k = k;
		this.checkK(k);
	}
	
	public CombinatorialRunner(List<String> tests, int k) {
		super(tests);
		this.k = k;
		this.checkK(k);
	}

	@Override
	public TestExecResults run() {
		TestExecResults result = TestExecResults.createInstance();
		//first compute all k-combinations, then observe its outcome
		//finally record all failed tests (with its prefixed tests)
		int total = super.junitTestList.size();
		PermutationGenerator generator = new PermutationGenerator(total, this.k);
		
		int count = 0;
		int totalnum = generator.getPermutationNum();
		while(generator.hasNext()) {
			long starttime = System.currentTimeMillis();
			
			/*get a list of test to run*/
			int[] testIndices = generator.getNext();
			List<String> tests = new LinkedList<String>();
			for(int index : testIndices) {
				tests.add(super.junitTestList.get(index));
			}
			//run the test
			//need to abstract this part into a utiltiy class
			Map<String, OneTestExecResult> singleRun = TestExecUtils.executeTestsInFreshJVM(super.getClassPath(),
					super.getTmpOutputFile(), tests);
			result.addExecutionResults(singleRun);
			
			if(Main.showProgress) {
				long endtime = System.currentTimeMillis();
				long elpased = endtime - starttime;
				float seconds = (totalnum - count)*elpased/(1000);
				System.out.println("Run the: " + count + " / " + totalnum + " test. Still need: " + seconds + " seconds to finish");
			}
			count++;
		}
		return result;
	}

	private void checkK(int k) {
		if(k <= 1) {
			throw new RuntimeException("The value k to set must > 1. \n" +
					"Use IsolationRunner to run tests in isolation for the case k = 1.");
		}
	}
}
