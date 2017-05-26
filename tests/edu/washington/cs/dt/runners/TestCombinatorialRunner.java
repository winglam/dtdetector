/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.runners;

import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.dt.TestExecResults;
import edu.washington.cs.dt.main.Main;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestCombinatorialRunner extends TestCase {
	
	public static Test suite() {
		return new TestSuite(TestCombinatorialRunner.class);
	}
	
	public void test1() {
		List<String> tests = new LinkedList<String>();
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
		
		Main.showProgress = true;
		
		AbstractTestRunner runner = new CombinatorialRunner(tests, 2);
		TestExecResults results = runner.run();
		
		System.out.println(results);
		
		assertEquals(182, results.getExecutionRecords().size());
	}
	
	public void testDifferentExceptions() {
		List<String> tests = new LinkedList<String>();
		tests.add("edu.washington.cs.dt.samples.TestDifferentException.testSetA");
		tests.add("edu.washington.cs.dt.samples.TestDifferentException.testSetB");
		tests.add("edu.washington.cs.dt.samples.TestDifferentException.testUseAB");
		
		AbstractTestRunner runner = new CombinatorialRunner(tests, 3);
		TestExecResults results = runner.run();
		
		System.out.println(results);
		assertEquals(6, results.getExecutionRecords().size());
	}
	
}
