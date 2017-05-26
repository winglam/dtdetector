/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.runners;

import java.util.List;
import java.util.Map;

import edu.washington.cs.dt.OneTestExecResult;
import edu.washington.cs.dt.TestExecResults;
import edu.washington.cs.dt.tools.RunTestInFixedOrder;
import edu.washington.cs.dt.util.Files;
import edu.washington.cs.dt.util.TestExecUtils;
import edu.washington.cs.dt.util.Utils;

public class RandomOrderRunner extends AbstractTestRunner {

	static int count = 0;
	
	private String inputTestFile;
	
	public RandomOrderRunner(String fileName) {
		super(fileName);
		this.inputTestFile = fileName;
	}
	
	public RandomOrderRunner(List<String> tests) {
		super(tests);
	}

	@Override
	public TestExecResults run() {
		List<String> randomizedList = Utils.randomList(super.junitTestList);
//		if(RunTestInFixedOrder.verbose) {
//			String testOrderFile = this.inputTestFile + "_order_" + (count++) + ".txt";
//			Files.writeToFileWithNoExp(randomizedList, testOrderFile);
//		}
		TestExecResults result = TestExecResults.createInstance();
        Map<String, OneTestExecResult> singleRun = TestExecUtils.executeTestsInFreshJVM(super.getClassPath(),
        		super.getTmpOutputFile(), randomizedList);
		result.addExecutionResults(singleRun);
		return result;
	}
}