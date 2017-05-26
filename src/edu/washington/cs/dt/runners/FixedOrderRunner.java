/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.runners;

import java.util.List;
import java.util.Map;

import edu.washington.cs.dt.OneTestExecResult;
import edu.washington.cs.dt.TestExecResults;
import edu.washington.cs.dt.main.Main;
import edu.washington.cs.dt.util.TestExecUtils;

public class FixedOrderRunner extends AbstractTestRunner {

    public FixedOrderRunner(List<String> tests) {
        super(tests);
    }

    public FixedOrderRunner(String fileName) {
        super(fileName);
    }

    @Override
    public TestExecResults run() {
        TestExecResults result = TestExecResults.createInstance();
        Map<String, OneTestExecResult> singleRun = TestExecUtils.executeTestsInFreshJVM(super.getClassPath(),
                super.getTmpOutputFile(), super.junitTestList);
        result.addExecutionResults(singleRun);
        //check do we need to dump the fixed ordered results to an intermediate file
        if(Main.fixedOrderReport != null) {
            super.saveResultsToFile(singleRun, Main.fixedOrderReport);
        }
        return result;
    }
}
