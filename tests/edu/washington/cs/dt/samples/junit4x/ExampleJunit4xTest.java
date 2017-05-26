/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.samples.junit4x;

import java.util.List;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import edu.washington.cs.dt.main.Main;
import edu.washington.cs.dt.util.TestExecUtils;

public class ExampleJunit4xTest {

	static int x = 1;
	
	@Test
	public void testX() {
		x++;
		System.out.println("Running testX");
	}
	
	@Test
	public void testY() {
		x++;
		System.out.println("Running testY");
	}
	
	@Test
	public void testZ() {
		System.out.println("Throw exception");
		throw new RuntimeException();
	}
	
	@Test
	public void testE() {
		System.out.println("Assertion will fail");
		Assert.assertEquals(false, true);
	}
	
	@Test
	public void testF() {
		System.out.println("Assertion will fail");
		Assert.assertEquals("message", "a", "b");
	}
	
	@Test(timeout = 1)
	public void testTimeOut() {
		for(int i = 0; i < 100; i++) {
			System.out.print(i);
		}
	}
	
	public static void main(String[] args) {
		JUnitCore core = new JUnitCore();
		
		Request r = Request.method(ExampleJunit4xTest.class, "testX");
		Result re = core.run(r);
		viewDetails(re);
		
		r = Request.method(ExampleJunit4xTest.class, "testY");
		re = core.run(r);
		viewDetails(re);
		
		r = Request.method(ExampleJunit4xTest.class, "testZ");
		re = core.run(r);
		viewDetails(re);
		
		r = Request.method(ExampleJunit4xTest.class, "testE");
		re = core.run(r);
		viewDetails(re);
		
		r = Request.method(ExampleJunit4xTest.class, "testF");
		re = core.run(r);
		viewDetails(re);
		
		r = Request.method(ExampleJunit4xTest.class, "testTimeOut");
		re = core.run(r);
		viewDetails(re);
		
		System.out.println("------- check the compatibility with JUnit "
				+ "3 ---");
		//check the compatibility with JUnit 3.x
		r = Request.method(edu.washington.cs.dt.samples.TestShareGlobals.class, "test1");
		re = core.run(r);
		viewDetails(re);
	}
	
	static void viewDetails(Result re) {
		System.out.println("Number of run: " + re.getRunCount()
				+ ", number of fail: " + re.getFailureCount());
		List<Failure> fails = re.getFailures();
		if(!fails.isEmpty()) {
			Failure firstFail = fails.get(0);
			System.out.println(firstFail.getTrace());
			System.out.println(firstFail.getException().getClass());
			
			String[] stacktraces = TestExecUtils.extractStackTraces(firstFail.getException());
			Pattern p = Pattern.compile(Main.excludeRegex);
			String flatString = TestExecUtils.flatStrings(stacktraces, p, TestExecUtils.JUNIT_ASSERT);
			System.out.println(flatString);
		}
		
	}
}
