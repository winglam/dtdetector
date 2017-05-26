/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.samples.junit4x;

import org.junit.Ignore;
import org.junit.Test;

public class ExampleForUnitTestFinder {
	@Test
	public void testX() {
	}
	
	@Test
	@Ignore("Ignore this")
	public void testY() {
	}
	
	public void testZ() {
		
	}
}
