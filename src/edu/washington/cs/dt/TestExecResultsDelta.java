/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.washington.cs.dt.main.Main;
import edu.washington.cs.dt.util.Files;
import edu.washington.cs.dt.util.Globals;

public class TestExecResultsDelta {
	public final String testName;
	public final OneTestExecResult intendedResult;
	public final List<String> intendedPreTests;
	public final OneTestExecResult divergentResult;
	public final List<String> dependentTests; /*the tests executed before testName */
	
	private final boolean printstacktrace;
	private final boolean printexecseq;
	
	public TestExecResultsDelta(String testName, OneTestExecResult intendedResult,
			List<String> intendedPreTests, OneTestExecResult divergentResult, List<String> dependentTests) {
		this.testName = testName;
		this.intendedResult = intendedResult;
		this.intendedPreTests = new LinkedList<String>();
		this.intendedPreTests.addAll(intendedPreTests);
		this.divergentResult = divergentResult;
		this.dependentTests = new LinkedList<String>();
		this.dependentTests.addAll(dependentTests);
		this.printstacktrace = Main.printstacktrace;
		this.printexecseq = Main.printexecseq;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Test: " + testName);
		sb.append(Globals.lineSep);
		sb.append("Intended behavior: " + intendedResult.result);
		sb.append(Globals.lineSep);
		if(printstacktrace) {
		    sb.append("Stack trace: " + intendedResult.getFullStackTrace());
		    sb.append(Globals.lineSep);
		}
		if(printexecseq) {
		    sb.append("when executed after: " + intendedPreTests.toString());
		    sb.append(Globals.lineSep);
		}
		sb.append("The revealed different behavior: " + divergentResult.result);
		sb.append(Globals.lineSep);
		if(printstacktrace) {
		    sb.append("Stack trace: " + divergentResult.getFullStackTrace());
		    sb.append(Globals.lineSep);
		}
		if(printexecseq) {
		    sb.append("when executed after: " + dependentTests.toString());
		    sb.append(Globals.lineSep);
		}
		return sb.toString();
	}
	
	public static void writeToFile(Collection<TestExecResultsDelta> deltas, String fileName) {
		StringBuilder sb = new StringBuilder();
		for(TestExecResultsDelta delta : deltas) {
			sb.append(delta.toString());
			sb.append(Globals.lineSep);
		}
		try {
			Files.writeToFile(sb.toString(), fileName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static List<TestExecResultsDelta> removeRedundancy(Collection<TestExecResultsDelta> deltas) {
		List<TestExecResultsDelta> noRedundnacy = new LinkedList<TestExecResultsDelta>();
		
		Set<String> uniqueSig = new HashSet<String>();
		for(TestExecResultsDelta delta : deltas) {
			String s = delta.toString();
			if(uniqueSig.contains(s)) {
				continue;
			}
			noRedundnacy.add(delta);
			uniqueSig.add(s);
		}
		
		return noRedundnacy;
	}
}