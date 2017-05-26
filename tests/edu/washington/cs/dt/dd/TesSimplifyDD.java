/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.dd;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TesSimplifyDD extends TestCase {
	public static Test suite() {
		return new TestSuite(TesSimplifyDD.class);
	}
	
	public void testDivideArray1() {
		int[] nine = this.createArrayWithLength(9);
		int[][] arrays = AbstractMinimizer.divideByN(nine, 2);
		assertEquals(arrays.length, 2);
		AbstractMinimizer.checkDivideResults(nine, arrays);
	}
	
	public void testDivideArray2() {
		int[] nine = this.createArrayWithLength(25);
		for(int d = 1; d < 25; d ++ ) {
		    int[][] arrays = AbstractMinimizer.divideByN(nine, d);
		    assertEquals(arrays.length, d);
		    AbstractMinimizer.checkDivideResults(nine, arrays);
		    this.checkArray(arrays, d);
		}
	}
	
	public void testSimplify() {
		AbstractMinimizer<Integer> minimizer = this.createTwoFailureMinimizer();
		List<Integer> simplified = minimizer.simplify();
		assertEquals(simplified.size(), 2);
		assertTrue(simplified.contains(10));
		assertTrue(simplified.contains(11));
	}
	
	private void checkArray(int[][] arrays, int d) {
		for(int i = 0; i < arrays.length; i++) {
			if(i != arrays.length - 1) {
				assertTrue(arrays[i+1].length - arrays[i].length < d);
			}
		}
	}
	
	private int[] createArrayWithLength(int length) {
		int[] array = new int[length];
		for(int i = 0; i < array.length; i++) {
			array[i] = i;
		}
		return array;
	}
	
	private AbstractMinimizer<Integer> createTwoFailureMinimizer() {
		List<Integer> data = new LinkedList<Integer>();
		for(int i = 0; i < 12; i++) {
			data.add(i);
		}
		return new AbstractMinimizer<Integer>(data) {
			protected boolean is_still_fail(List<Integer> d) {
				if(d.size() == 12) {
					return true;
				} else if (d.contains(11) && d.contains(10)) {
					return true;
				} else{
					return false;
				}
			}
		};
	}
}
