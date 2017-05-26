/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.util;

import edu.washington.cs.dt.RESULT;
import edu.washington.cs.dt.samples.SampleJUnit3Tests;
import edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestJUnitTestExecutor extends TestCase {
	
	public static Test suite() {
		return new TestSuite(TestJUnitTestExecutor.class);
	}
	
	public void testPassJUnit3() {
		JUnitTestExecutor executor = new JUnitTestExecutor(SampleJUnit3Tests.class, "testJUnit3_1");
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.PASS.name(), executor.getResult());
		assertEquals(TestExecUtils.noStackTrace, executor.getStackTrace());
		
		try {
			executor = new JUnitTestExecutor("edu.washington.cs.dt.samples.SampleJUnit3Tests.testJUnit3_1");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.PASS.name(), executor.getResult());
		assertTrue(TestExecUtils.noStackTrace.equals(executor.getStackTrace()));
		
		executor = new JUnitTestExecutor("edu.washington.cs.dt.samples.SampleJUnit3Tests", "testJUnit3_1");
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.PASS.name(), executor.getResult());
		assertTrue(TestExecUtils.noStackTrace.equals(executor.getStackTrace()));
	}
	
	public void testPassJUnit4() {
		JUnitTestExecutor executor = new JUnitTestExecutor(ExampleJunit4xTest.class, "testX");
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.PASS.name(), executor.getResult());
		assertEquals(TestExecUtils.noStackTrace, executor.getStackTrace());
		
		try {
			executor = new JUnitTestExecutor("edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testX");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.PASS.name(), executor.getResult());
		assertTrue(TestExecUtils.noStackTrace.equals(executor.getStackTrace()));
		
		executor = new JUnitTestExecutor("edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest", "testX");
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.PASS.name(), executor.getResult());
		assertTrue(TestExecUtils.noStackTrace.equals(executor.getStackTrace()));
	}
	
	public void testJUnit3WithException() {
		JUnitTestExecutor executor = new JUnitTestExecutor(SampleJUnit3Tests.class, "testJUnit3_exception");
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.ERROR.name(), executor.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(executor.getStackTrace()));
		
		try {
			executor = new JUnitTestExecutor("edu.washington.cs.dt.samples.SampleJUnit3Tests.testJUnit3_exception");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.ERROR.name(), executor.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(executor.getStackTrace()));
		
		executor = new JUnitTestExecutor("edu.washington.cs.dt.samples.SampleJUnit3Tests", "testJUnit3_exception");
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.ERROR.name(), executor.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(executor.getStackTrace()));
	}
	
	public void testFail1JUnit4() {
		JUnitTestExecutor executor = new JUnitTestExecutor(ExampleJunit4xTest.class, "testE");
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.FAILURE.name(), executor.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(executor.getStackTrace()));
		
		try {
			executor = new JUnitTestExecutor("edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testE");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.FAILURE.name(), executor.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(executor.getStackTrace()));
		
		executor = new JUnitTestExecutor("edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest", "testE");
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.FAILURE.name(), executor.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(executor.getStackTrace()));
	}
	
	public void testJUnit3WithFailure() {
		JUnitTestExecutor executor = new JUnitTestExecutor(SampleJUnit3Tests.class, "testJUnit3_fail");
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.FAILURE.name(), executor.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(executor.getStackTrace()));
		
		try {
			executor = new JUnitTestExecutor("edu.washington.cs.dt.samples.SampleJUnit3Tests.testJUnit3_fail");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.FAILURE.name(), executor.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(executor.getStackTrace()));
		
		executor = new JUnitTestExecutor("edu.washington.cs.dt.samples.SampleJUnit3Tests", "testJUnit3_fail");
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.FAILURE.name(), executor.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(executor.getStackTrace()));
	}
	
	public void testFail2JUnit4() {
		JUnitTestExecutor executor = new JUnitTestExecutor(ExampleJunit4xTest.class, "testF");
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.FAILURE.name(), executor.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(executor.getStackTrace()));
		
		try {
			executor = new JUnitTestExecutor("edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testF");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.FAILURE.name(), executor.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(executor.getStackTrace()));
		
		executor = new JUnitTestExecutor("edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest", "testF");
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.FAILURE.name(), executor.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(executor.getStackTrace()));
	}
	
	public void testErrorJUnit4() {
		JUnitTestExecutor executor = new JUnitTestExecutor(ExampleJunit4xTest.class, "testZ");
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.ERROR.name(), executor.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(executor.getStackTrace()));
		
		try {
			executor = new JUnitTestExecutor("edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest.testZ");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.ERROR.name(), executor.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(executor.getStackTrace()));
		
		executor = new JUnitTestExecutor("edu.washington.cs.dt.samples.junit4x.ExampleJunit4xTest", "testZ");
		executor.executeWithJUnit4Runner();
		assertEquals(RESULT.ERROR.name(), executor.getResult());
		assertTrue(!TestExecUtils.noStackTrace.equals(executor.getStackTrace()));
	}

}
