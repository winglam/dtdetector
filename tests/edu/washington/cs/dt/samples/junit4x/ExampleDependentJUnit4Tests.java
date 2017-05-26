/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.samples.junit4x;

import junit.framework.Assert;

import org.junit.Test;

public class ExampleDependentJUnit4Tests {
	
	static int i = 2;
	
	@Test
	public void test1() {
		i --;
	}
	
	@Test
	public void test2() {
		i --;
	}
	
	@Test
	public void test3() {
		Assert.assertTrue(i != 0);
	}
}