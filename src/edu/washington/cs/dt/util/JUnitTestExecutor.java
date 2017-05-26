/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.util;

import java.util.regex.Pattern;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import edu.washington.cs.dt.RESULT;
import edu.washington.cs.dt.main.Main;


/**
 * This class is in too low level. I made it package-visible
 * on purpose. 
 * */
class JUnitTestExecutor {

	private String result = null;
	private String stackTrace = TestExecUtils.noStackTrace;
	private String fullStackTrace = TestExecUtils.noStackTrace;
	
	public final Class<?> junitTest;
	public final String junitMethod;
	public final String fullMethodName;
	
	//package.class.method
	public JUnitTestExecutor(String fullMethodName) throws ClassNotFoundException {
		this.fullMethodName = fullMethodName;
		String className = this.fullMethodName.substring(0, this.fullMethodName.lastIndexOf("."));
		try {
			Class<?> clzName = Class.forName(className);
			this.junitTest = clzName;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		this.junitMethod = this.fullMethodName.substring(this.fullMethodName.lastIndexOf(".") + 1);
	}
	
	public JUnitTestExecutor(String className, String junitMethod) {
		try {
			Class<?> clzName = Class.forName(className);
			this.junitTest = clzName;
			this.junitMethod = junitMethod;
			this.fullMethodName = this.junitTest.getName() + "." + junitMethod;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	public JUnitTestExecutor(Class<?> junitTest, String junitMethod) {
		this.junitTest = junitTest;
		this.junitMethod = junitMethod;
		this.fullMethodName = this.junitTest.getName() + "." + junitMethod;
	}
	
	public void executeWithJUnit4Runner() {
        JUnitCore core = new JUnitCore();
		Request r = Request.method(this.junitTest, this.junitMethod);
		Result re = core.run(r);
		//FIXME the run count can be > 1, e.g., testLazy in hibernate
		if(re.getRunCount() > 1) {
			Log.logln("FIXME: Running: " + this.junitMethod + ", count: " + re.getRunCount());
		}
		
		if(re.getFailureCount() == 0) {
			result = RESULT.PASS.name();
		} else {
			if(re.getFailureCount() > 1) {
				//FIXME is there any nested test? so getFailureCount() will > 1
				//e.g., running org.hibernate.test.annotations.id.sequences.JoinColumnOverrideTest.testBlownPrecision
				//gives 2 failures
				Log.logln("FIXME: Running: " + this.fullMethodName + ", failure count: " + re.getFailureCount());
			}
			//check whether it is a failure or an error
            Failure f = re.getFailures().get(0);
            Throwable excep = f.getException();
            if(isJUnitAssertionFailure(excep)) {
            	result = RESULT.FAILURE.name();
            } else {
            	result = RESULT.ERROR.name();
            }
            //get the stack trace
            stackTrace = flatStackTrace(excep);
            fullStackTrace = TestExecUtils.flatStrings(TestExecUtils.extractStackTraces(excep));
		}
	}
	
	public static boolean isJUnitAssertionFailure(Throwable exception) {
		return exception.getClass().equals(junit.framework.AssertionFailedError.class)
		      || exception.getClass().equals(junit.framework.ComparisonFailure.class);
	}
	
	public static String flatStackTrace(Throwable exception) {
		String[] stacktraces = TestExecUtils.extractStackTraces(exception);
		Pattern p = Pattern.compile(Main.excludeRegex);
		String flatString = TestExecUtils.flatStrings(stacktraces, p, TestExecUtils.JUNIT_ASSERT);
		return flatString;
	}
	
	public String getResult() {
		return this.result;
	}
	
	public String getStackTrace() {
		return this.stackTrace;
	}
	
	public String getFullStackTrace() {
		return this.fullStackTrace;
	}
}
