/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.dd;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.dt.util.Utils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestMultiNonInterferenceFaultMinimizer extends TestCase {
	
	public static Test suite() {
		return new TestSuite(TestMultiNonInterferenceFaultMinimizer.class);
	}
	
	public void testNonInterference1() {
		List<Integer> list = Utils.createList(new Integer[]{0, 1, 2, 3, 4, 5, 6, 7});
		multi_faults_tests(8, list);
	}
	
	public void testMultiFault2() {
		List<Integer> list = Utils.createList(new Integer[]{0, 3, 4, 5, 7});
		multi_faults_tests(8, list);
	}
	
	public void testMultiFault3() {
		List<Integer> list = Utils.createList(new Integer[]{0, 1, 2, 3});
		multi_faults_tests(8, list);
	}
	
	public void testMultiFault4() {
		List<Integer> list = Utils.createList(new Integer[]{ 5, 6, 7});
		multi_faults_tests(8, list);
	}
	
	public void testMultiFault5() {
		List<Integer> list = Utils.createList(new Integer[]{1, 7});
		multi_faults_tests(8, list);
	}
	
	public void testMultiFault6() {
		List<Integer> list = Utils.createList(new Integer[]{1, 3, 7});
		multi_faults_tests(9, list);
	}
	
	public void testMultiFault7() {
		List<Integer> list = Utils.createList(new Integer[]{1, 3, 4, 5, 7});
		multi_faults_tests(9, list);
	}
	
	public void testMultiFault8() {
		List<Integer> list = Utils.createList(new Integer[]{1, 2, 3, 4, 5, 7, 8});
		multi_faults_tests(9, list);
	}
	
	static void multi_faults_tests(int length, List<Integer> failed_indices) {
		Utils.stdln(" ===== start of a noninference test ===== ");
		Utils.checkTrue(failed_indices.size() > 0, "non-empty failed indices");
		for(int failure_index : failed_indices) {
			Utils.checkTrue(length > failure_index && failure_index >= 0, "");
		}
		List<Integer> ints = new LinkedList<Integer>();
		for(int i = 0 ; i < length; i++) {
			ints.add(i);
		}
		Utils.stdln("All original inputs: " + ints);
		AbstractMinimizer<Integer> minimizer = new MultiNonInterferenceFaultsMinimizer(ints, failed_indices);
		List<Integer> results = minimizer.minimize();
		Utils.stdln("the failed_indices: " + failed_indices);
		Utils.stdln("minimized results: " + results);
		assertTrue(results.size() == failed_indices.size());
		Collections.sort(failed_indices);
		Collections.sort(minimizer.data);
		assertTrue(results.equals(failed_indices));
		Utils.stdln(" ----- end of a noninference test ---- ");
		Utils.stdln("count of divide: " + minimizer.getCount());
	}
}

class MultiNonInterferenceFaultsMinimizer extends AbstractMinimizer<Integer> {

	public final List<Integer> failed_indices;
	
	public MultiNonInterferenceFaultsMinimizer(List<Integer> data, List<Integer> failed_indices) {
		super(data);
		for(int i : failed_indices) {
		    super.check_index(i);
		}
		this.failed_indices = failed_indices;
	}

	@Override
	protected boolean is_still_fail(List<Integer> data) {
		for(int d : data) {
			if(this.failed_indices.contains(d)) {
				return true;
			}
		}
		return false;
	}
}