/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */ 
package edu.washington.cs.dt;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.dt.dd.DependentTestSetMinimizer;
import edu.washington.cs.dt.main.Main;
import edu.washington.cs.dt.runners.AbstractTestRunner;
import edu.washington.cs.dt.runners.CombinatorialRunner;
import edu.washington.cs.dt.runners.FixedOrderRunner;
import edu.washington.cs.dt.runners.IsolationRunner;
import edu.washington.cs.dt.runners.RandomOrderRunner;
import edu.washington.cs.dt.runners.ReversedOrderRunner;
import edu.washington.cs.dt.util.TestExecUtils;
import edu.washington.cs.dt.util.Utils;

/**
 * Need to configure paths
 * */
public class DependentTestIdentifier {
	
	public final List<String> tests;
	
	private String classPath = Main.classpath;
	private String tmpOutputFile = Main.tmpfile;
	
	private boolean minimize = false;
	private final boolean removeredundancy;
	
	public DependentTestIdentifier(List<String> tests) {
		this.tests = tests;
		removeredundancy = Main.removeredundancy;
	}
	
	public void setMinimize(boolean minimize) {
		this.minimize = minimize;
	}
	
	public void setClasspath(String cp) {
		this.classPath = cp;
	}
	
	public void setTmpOutputFile(String tmpFile) {
		this.tmpOutputFile = tmpFile;
	}
	
	public List<TestExecResultsDelta> findDependenceForIsolation() {
	    //AbstractTestRunner fixedOrderRunner = createFixedOrderRunner();
	    AbstractTestRunner isolationRunner = createIsolationRunner();
	    
	    //TestExecResults fixedOrderResults = fixedOrderRunner.run();
	    TestExecResults isolationResults = isolationRunner.run();
	    
	    //List<TestExecResult> foRecords = fixedOrderResults.getExecutionRecords();
	    //Utils.checkTrue(foRecords.size() == 1, "The fixed order runner only launches JVM once!");
	    
	    //see the difference, any behavior difference is treated as dependent tests
	    TestExecResult intendedResult = isolationResults.getExecutionRecords().get(0);
	    TestExecResultsDifferentior differ = new TestExecResultsDifferentior(intendedResult, isolationResults);
	    List<TestExecResultsDelta> deltas = differ.diffResults();
	    
	    if(!minimize) {
		//return deltas;
	    }
	    
	    List<TestExecResultsDelta> minimized = this.minimizeDependentTests(deltas);
	    
	    return minimized;
	}
	
	public List<TestExecResultsDelta> findDependenceForReverse() {
		AbstractTestRunner fixedOrderRunner = createFixedOrderRunner();
		AbstractTestRunner reverseOrderRunner = createReversedOrderRunner();
		
		TestExecResults fixedOrderResults = fixedOrderRunner.run();
		TestExecResults reverseOrderResults = reverseOrderRunner.run();
		
		List<TestExecResult> foRecords = fixedOrderResults.getExecutionRecords();
		Utils.checkTrue(foRecords.size() == 1, "The fixed order runner only launches JVM once!");
		List<TestExecResult> reRecords = reverseOrderResults.getExecutionRecords();
		Utils.checkTrue(reRecords.size() == 1, "The reversed order runner only launches JVM once!");
		
		TestExecResult intendedResult = fixedOrderResults.getExecutionRecords().get(0);
		TestExecResultsDifferentior differ = new TestExecResultsDifferentior(intendedResult, reverseOrderResults);
		List<TestExecResultsDelta> deltas = differ.diffResults();
		
		if(!this.minimize) {
			return deltas;
		}
		
		List<TestExecResultsDelta> minimized = this.minimizeDependentTests(deltas);
		
		return minimized;
	}
	
	public List<TestExecResultsDelta> findDependenceForRandomization() {
		AbstractTestRunner fixedOrderRunner = createFixedOrderRunner();
		AbstractTestRunner randomOrderRunner = createRandomOrderRunner();
		
		TestExecResults fixedOrderResults = fixedOrderRunner.run();
		TestExecResults randomOrderResults = randomOrderRunner.run();
		
		List<TestExecResult> foRecords = fixedOrderResults.getExecutionRecords();
		Utils.checkTrue(foRecords.size() == 1, "The fixed order runner only launches JVM once!");
		List<TestExecResult> randRecords = randomOrderResults.getExecutionRecords();
		Utils.checkTrue(randRecords.size() == 1, "The random order runner only launches JVM once!");
		
		TestExecResult intendedResult = fixedOrderResults.getExecutionRecords().get(0);
		TestExecResultsDifferentior differ = new TestExecResultsDifferentior(intendedResult, randomOrderResults);
		List<TestExecResultsDelta> deltas = differ.diffResults();
		
		if(!this.minimize) {
			return deltas;
		}
		
		List<TestExecResultsDelta> minimized = this.minimizeDependentTests(deltas);
		
		return minimized;
	}
	
	public List<TestExecResultsDelta> findDependenceForCombination(int k) {
		AbstractTestRunner fixedOrderRunner = createFixedOrderRunner();
		AbstractTestRunner combRunner = createCombinatorialRunner(k);
		
		TestExecResults fixedOrderResults = fixedOrderRunner.run();
		TestExecResults combResults = combRunner.run();
		
		List<TestExecResult> foRecords = fixedOrderResults.getExecutionRecords();
		Utils.checkTrue(foRecords.size() == 1, "The fixed order runner only launches JVM once!");
		
		//see the difference, any behavior difference is treated as dependent tests
		TestExecResult intendedResult = fixedOrderResults.getExecutionRecords().get(0);
		TestExecResultsDifferentior differ = new TestExecResultsDifferentior(intendedResult, combResults);
		List<TestExecResultsDelta> deltas = differ.diffResults();
		
		if(!this.minimize) {
			return deltas;
		}
		
		List<TestExecResultsDelta> minimized = this.minimizeDependentTests(deltas);
		
		return minimized;
	}
	
	protected FixedOrderRunner createFixedOrderRunner() {
		FixedOrderRunner fixedOrderRunner = new FixedOrderRunner(this.tests);
		fixedOrderRunner.setClassPath(this.classPath);
		fixedOrderRunner.setTmpOutputFile(this.tmpOutputFile);
		return fixedOrderRunner;
	}
	
	protected ReversedOrderRunner createReversedOrderRunner() {
		ReversedOrderRunner reversedOrderRunner = new ReversedOrderRunner(this.tests);
		reversedOrderRunner.setClassPath(this.classPath);
		reversedOrderRunner.setTmpOutputFile(this.tmpOutputFile);
		return reversedOrderRunner;
	}
	
	protected RandomOrderRunner createRandomOrderRunner() {
		RandomOrderRunner randomOrderRunner = new RandomOrderRunner(this.tests);
		randomOrderRunner.setClassPath(this.classPath);
		randomOrderRunner.setTmpOutputFile(this.tmpOutputFile);
		return randomOrderRunner;
	}
	
	protected IsolationRunner createIsolationRunner() {
		IsolationRunner isolationRunner = new IsolationRunner(this.tests);
		isolationRunner.setClassPath(this.classPath);
		isolationRunner.setTmpOutputFile(this.tmpOutputFile);
		return isolationRunner;
	}
	
	protected CombinatorialRunner createCombinatorialRunner(int k) {
		CombinatorialRunner combRunner = new CombinatorialRunner(this.tests, k);
		combRunner.setClassPath(this.classPath);
		combRunner.setTmpOutputFile(this.tmpOutputFile);
		return combRunner;
	}
	
	private List<TestExecResultsDelta> minimizeDependentTests(List<TestExecResultsDelta> deltas) {
		List<TestExecResultsDelta> minimized = new LinkedList<TestExecResultsDelta>();
		for(TestExecResultsDelta d : deltas) {
			String testName = d.testName;
			OneTestExecResult ir = d.intendedResult;
			List<String> intendedPreTests = d.intendedPreTests;
			OneTestExecResult dr = d.divergentResult;
			List<String> dependentTests = d.dependentTests;
			//minimize both intendedPreTests and dependentTests
			List<String> minimizedPreTests = new LinkedList<String>();
			List<String> minimizedDependentTests = new LinkedList<String>();
			//first try to remove all to see the result
			List<String> onlyOneTest = Collections.singletonList(testName);
			OneTestExecResult singleResult = TestExecUtils.executeTestsInFreshJVM(this.classPath, this.tmpOutputFile, onlyOneTest).get(testName);
			//if the outcome of execute a test is diffferent from executing a batch of tests, we need to do minimization
			if(!singleResult.equals(ir)) {
				DependentTestSetMinimizer minimizer = new DependentTestSetMinimizer(intendedPreTests, testName,
						ir, this.classPath, this.tmpOutputFile);
				minimizedPreTests = minimizer.minimize();
			}
			if(!singleResult.equals(dr)) {
				DependentTestSetMinimizer minimizer = new DependentTestSetMinimizer(dependentTests, testName,
						dr, this.classPath, this.tmpOutputFile);
				minimizedDependentTests = minimizer.minimize();
			}
			TestExecResultsDelta simplifiedDelta = new TestExecResultsDelta(testName, ir, minimizedPreTests,
					dr, minimizedDependentTests);
			minimized.add(simplifiedDelta);
		}
		
		if(this.removeredundancy) {
			Utils.stdln("Number of dependent tests before removing redundancy: " + minimized.size());
			minimized = TestExecResultsDelta.removeRedundancy(minimized);
			Utils.stdln("Number of dependent tests after removing redundancy: " + minimized.size());
		}
		
		return minimized;
	}
}
