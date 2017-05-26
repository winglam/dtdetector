/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.dd;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.dt.OneTestExecResult;
import edu.washington.cs.dt.main.Main;
import edu.washington.cs.dt.util.Log;
import edu.washington.cs.dt.util.TestExecUtils;
import edu.washington.cs.dt.util.Utils;

public class DependentTestSetMinimizer extends AbstractMinimizer<String>{

	public final String classPath;
	public final String tmpOutputFile;
	
	public final String dependentTest;
	public final OneTestExecResult intendedResult;
	
	private Map<List<String>, OneTestExecResult> cachedResults = new HashMap<List<String>, OneTestExecResult>();
	private boolean enablecache = Main.enablecache;

	/**
	 * Running tests + dependentTest will make the dependentTest reveal a different
	 * result than intendedResult.
	 * 
	 * This class finds a minimal subset of tests, called subtests, that running
	 * subtess + dependentTest reveal the same behavior for dependentTest as
	 * running tests + dependentTest
	 * */
	public DependentTestSetMinimizer(List<String> tests,
			String dependentTest, OneTestExecResult intendedResult,
			String classPath, String tmpOutputFile) {
		super(tests);
		this.classPath = classPath;
		this.tmpOutputFile = tmpOutputFile;
		this.dependentTest = dependentTest;
		this.intendedResult = intendedResult;
	}

	@Override
	protected boolean is_still_fail(List<String> tests) {
		List<String> exec_tests = new LinkedList<String>();
		exec_tests.addAll(tests);
		exec_tests.add(dependentTest);
		
		//first check the cache if enabled
		OneTestExecResult r = null;
		if(this.enablecache && this.cachedResults.containsKey(exec_tests)) {
			r = cachedResults.get(exec_tests);
			Log.logln("* Get from cache for r: " + exec_tests.size());
		} else {
			//execute tests in an isolated JVM
			Map<String, OneTestExecResult> results = TestExecUtils.executeTestsInFreshJVM(this.classPath, this.tmpOutputFile, exec_tests);
			//check the result
			r = results.get(this.dependentTest);
			//put into cache
			if(this.enablecache) {
				Utils.checkNull(r, "can not be null");
				Utils.checkTrue(!this.cachedResults.containsKey(exec_tests), "");
			    this.cachedResults.put(exec_tests, r);
			}
		}
//		System.out.println(r);
//		System.out.println("intended: " + intendedResult);
		
		Utils.checkNull(r, "r should not be null.");
		Log.logln("* Result: " + r.result + ", intended: " + intendedResult + " Executed tests:  \n"  + exec_tests.size()//);
				+ " : "+ exec_tests);
		if(r.equals(intendedResult)) {
			return true;  //the same as the intended result (i.e., executed in a fixed order)
		} else {
			return false;   //still have different behaviors
		}
	}

}
