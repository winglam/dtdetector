/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.runners;

import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.dt.TestExecResults;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestRandomOrderRunner extends TestCase {
	
	public static Test suite() {
		return new TestSuite(TestRandomOrderRunner.class);
	}
	
	public void testRandomRunner() {
		List<String> tests = new LinkedList<String>();
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.test1");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.test2");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.test3");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.test4");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.test5");
		tests.add("edu.washington.cs.dt.samples.TestShareGlobals.test6");
		
		AbstractTestRunner runner = new RandomOrderRunner(tests);
		TestExecResults results = runner.run();
		
		System.out.println(results);
		
		assertEquals(1, results.getExecutionRecords().size());
	}

}
