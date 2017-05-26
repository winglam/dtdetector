/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt;

import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.dt.util.Globals;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestDependentTestDetector extends TestCase {
	
	List<String> tests = new LinkedList<String>();
	
	public static Test suite() {
		return new TestSuite(TestDependentTestDetector.class);
	}
	
	public void setUp() {
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.test1");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.test2");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.test3");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.test4");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.test5");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.test6");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.testDummy");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.testDummy1");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.testDummy2");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.testStr1");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.testStr2");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.testStr3");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.testStr4");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.testStr5");
	}

	public void testIsolation() {
		DependentTestIdentifier identifier = new DependentTestIdentifier(tests);
		identifier.setMinimize(true);
		List<TestExecResultsDelta> l = identifier.findDependenceForIsolation();
		System.out.println(l);
		assertEquals(4, l.size());
		assertEquals(l.get(0).testName, "edu.washington.cs.dt.samples.TestShareGlobals.testStr2");
		assertEquals(l.get(1).testName, "edu.washington.cs.dt.samples.TestShareGlobals.testStr3");
		assertEquals(l.get(2).testName, "edu.washington.cs.dt.samples.TestShareGlobals.testStr4");
		assertEquals(l.get(3).testName, "edu.washington.cs.dt.samples.TestShareGlobals.testStr5");
	}
	
	public void testReverse() {
		DependentTestIdentifier identifier = new DependentTestIdentifier(tests);
		identifier.setMinimize(true);
		List<TestExecResultsDelta> l = identifier.findDependenceForReverse();
		System.out.println(l);
		assertEquals(4, l.size());
	}
	
	public void testRandom() {
		DependentTestIdentifier identifier = new DependentTestIdentifier(tests);
		identifier.setMinimize(true);
		List<TestExecResultsDelta> l = identifier.findDependenceForRandomization();
		System.out.println(l);
	}
	
	public void testCombine() {
		tests.clear();
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.test1");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.test2");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.test3");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.test4");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.test5");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.test6");
		
		DependentTestIdentifier identifier = new DependentTestIdentifier(tests);
		identifier.setMinimize(true);
		List<TestExecResultsDelta> l = identifier.findDependenceForCombination(4);
		for(TestExecResultsDelta d : l) {
		    System.out.println(d);
		    System.out.println(Globals.lineSep);
		}
		assertEquals(8, l.size());
	}
}
