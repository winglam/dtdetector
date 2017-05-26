/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.dd;

import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.dt.util.Utils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestOneFaultMinimizer extends TestCase {
	
	public static Test suite() {
		return new TestSuite(TestOneFaultMinimizer.class);
	}
	
	public void testOneFaultIndex1() {
		one_failure_testcase(8, 3);
	}
	
	public void testOneFaultIndex2() {
		one_failure_testcase(15, 7);
	}
	
	public void testOneFaultIndex3() {
		one_failure_testcase(1, 0);
	}
	
	public void testOneFaultIndex4() {
		one_failure_testcase(33, 10);
	}
	
	public void testOneFaultIndex5() {
		one_failure_testcase(77, 30);
	}
	
	private void one_failure_testcase(int length, int failure_index) {
		Utils.checkTrue(length > failure_index && failure_index >= 0, "");
		List<Integer> ints = new LinkedList<Integer>();
		for(int i = 0 ; i < length; i++) {
			ints.add(i);
		}
		Utils.stdln("Original inputs: " + ints);
		AbstractMinimizer<Integer> minimizer = new OneFailureIntergerMinimizer(ints, failure_index);
		List<Integer> results = minimizer.minimize();
		assertTrue(results.size() == 1);
		assertTrue(results.get(0) == failure_index);
		Utils.stdln("----- end with one round ---- ");
		Utils.stdln("count of divide: " + minimizer.getCount());
	}
}

class OneFailureIntergerMinimizer extends AbstractMinimizer<Integer> {

	public final int failed_index;
	
	public OneFailureIntergerMinimizer(List<Integer> data, int failed_index) {
		super(data);
		super.check_index(failed_index);
		this.failed_index = failed_index;
	}

	/**
	 * Only if data contains "3", it fails.
	 * */
	@Override
	protected boolean is_still_fail(List<Integer> data) {
		for(int d : data) {
			if(d == failed_index) {
				return true;
			}
		}
		return false;
	}
}
