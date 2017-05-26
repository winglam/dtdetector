/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.runners;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.dt.OneTestExecResult;
import edu.washington.cs.dt.RESULT;
import edu.washington.cs.dt.TestExecResults;
import edu.washington.cs.dt.main.Main;
import edu.washington.cs.dt.util.Files;
import edu.washington.cs.dt.util.Globals;
import edu.washington.cs.dt.util.TestExecUtils;
import edu.washington.cs.dt.util.Utils;

public abstract class AbstractTestRunner {

	/* The input tests */
	protected final List<String> junitTestList;
	
	/*keep the classpath needs to run the tests, tmp output file to keep the
	 * intermediate results*/
	protected String classPath = null;
	protected String tmpOutputFile = null;
	
	/* Note that we use List here, since order matters*/
	public AbstractTestRunner(List<String> tests) {
		this.junitTestList = new LinkedList<String>();
		this.junitTestList.addAll(tests);
		this.classPath = System.getProperties().getProperty("java.class.path", null);
		tmpOutputFile = Main.tmpfile;
	}
	
	public AbstractTestRunner(String fileName) {
		this(Files.readWholeNoExp(fileName));
	}
	
	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}
	
	public String getClassPath() {
		return this.classPath;
	}
	
	public void setTmpOutputFile(String fileName) {
		this.tmpOutputFile = fileName;
	}
	
	public String getTmpOutputFile() {
		return this.tmpOutputFile;
	}
	
	public boolean isTestInList(String test) {
		return this.junitTestList.contains(test);
	}
	
	public abstract TestExecResults run();
	
	protected void saveResultsToFile(Collection<Map<String, OneTestExecResult>> results, String fileName) {
		StringBuilder sb = new StringBuilder();
		int numOfPass = 0;
		int numOfError = 0;
		int numOfFail = 0;
		for(Map<String, OneTestExecResult> result : results) {
			for(String test : result.keySet()) {
				RESULT r = result.get(test).result;
				sb.append(test);
				sb.append(" : ");
				sb.append(r);
				String fullStackTrace = result.get(test).getFullStackTrace();
				if(fullStackTrace != null && !fullStackTrace.trim().equals(TestExecUtils.noStackTrace)) {
					sb.append(Globals.lineSep);
					String[] sts = fullStackTrace.split(TestExecUtils.stackTraceSep);
					for(String st : sts) {
						sb.append("    ");
						sb.append(st);
						sb.append(Globals.lineSep);
					}
				}
//				String filteredStack = result.get(test).getFilteredStackTrace();
//				if(filteredStack != null && !filteredStack.trim().equals(TestExecUtils.noStackTrace)) {
//					sb.append(Globals.lineSep);
//					String[] sts = filteredStack.split(TestExecUtils.stackTraceSep);
//					for(String st : sts) {
//						sb.append("    ");
//						sb.append(st);
//						sb.append(Globals.lineSep);
//					}
//				}
				sb.append(Globals.lineSep);
				if(r == RESULT.PASS) {
					numOfPass++;
				} else if (r == RESULT.ERROR) {
					numOfError++;
				} else if (r == RESULT.FAILURE) {
					numOfFail ++;
				} else {
					throw new Error("Unknown: " + r);
				}
			}
//			Utils.checkTrue(numOfPass + numOfError + numOfFail == result.size(),
//					"Wrong number.");
		}
		sb.append("-----------------");
		sb.append(Globals.lineSep);
		sb.append("Passing tests: " + numOfPass);
		sb.append(Globals.lineSep);
		sb.append("Failing tests: " + numOfFail);
		sb.append(Globals.lineSep);
		sb.append("Error tests: " + numOfError);
		sb.append(Globals.lineSep);
		try {
			Files.writeToFile(sb.toString(), fileName);
		} catch (IOException e) {
			throw new Error(e);
		}
	}
	
	protected void saveResultsToFile(Map<String, OneTestExecResult> result, String fileName) {
		this.saveResultsToFile(Collections.singletonList(result), fileName);
	}
}
