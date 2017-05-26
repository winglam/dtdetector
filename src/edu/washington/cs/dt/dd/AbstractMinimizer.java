/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.dd;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.dt.util.Log;
import edu.washington.cs.dt.util.Utils;

public abstract class AbstractMinimizer<T> {
	
	public final List<T> data;
	
	/**
	 * How many round
	 * */
	private int count = 0;
	
	public AbstractMinimizer(List<T> data) {
		Utils.checkNull(data, "The given list can not be null.");
		Utils.checkTrue(!data.isEmpty(), "can not be empty.");
		this.data = data;
	}
	
	public int getCount() {
		return count;
	}
	
	protected int size() {
		return data.size();
	}
	
	protected T get(int index) {
		check_index(index);
		return data.get(index);
	}
	
	protected void check_index(int index) {
		Utils.checkTrue(index >= 0 && index < data.size(), "The index: "
				+ index + " is not valid, the size is: " + data.size());
	}
	
	protected boolean apply_data(int[] data_indices) {
		List<T> list = new LinkedList<T>();
		for(int index : data_indices) {
			check_index(index);
			list.add(get(index));
		}
		count++;
		return is_still_fail(list);
	}
	
	protected List<T> getData(int[] data_indices) {
		List<T> list = new LinkedList<T>();
		for(int index : data_indices) {
			check_index(index);
			list.add(get(index));
		}
		return list;
	}
	
	public List<T> minimize() {
		int[] indices = this.findMinIndices();
		List<T> ret = new LinkedList<T>();
		for(int index : indices) {
			ret.add(get(index));
		}
		return ret;
	}
	
	public List<T> simplify() {
		int[] indices = this.findSimplifiedIndices();
		List<T> ret = new LinkedList<T>();
		for(int index : indices) {
			ret.add(get(index));
		}
		return ret;
	}
	
	private int[] findMinIndices() {
		int[] indices = ddmin();
		checkNoDup(indices);
		return indices;
	}
	
	private int[] findSimplifiedIndices() {
		int[] indices = this.ddsimplify();
		checkNoDup(indices);
		return indices;
	}
	
	/**
	 * The core part of implementing the ddmin algorithm.
	 * */
	private int[] ddmin() {
		//check the initial setting
		Utils.checkTrue(this.is_still_fail(this.data), "All the configurations should be faield: " + this.data);
		Utils.checkTrue(!this.apply_data(new int[]{}), "The empty configuration should not fail");
		//create a list of full indices of the data
		int[] full_indices = new int[this.data.size()];
		for(int i = 0; i < full_indices.length; i++) {
			full_indices[i] = i;
		}
		//call the real minimization algorithm
//		return  ddmin2(full_indices, 2);
		return dd2(full_indices, new int[]{});
	}
	
	/**
	 * The design invariant is "c + r leads to test fails", but
	 * "r leads test pass"
	 * */
	private int[] dd2(int[] c, int[] r) {
		Log.logln("c size: " + c.length + ", r size: " + r.length);
		checkNoOverlap(c, r);
		boolean result_cr = this.apply_data(merge(c, r));
		boolean result_r = this.apply_data(r);
		Log.logln("result of c + r: " + result_cr + ",  length: " + (c.length + r.length));
		Log.logln("result of r: " + result_r + ", length: " + r.length);
		Utils.checkTrue(result_cr, "Test should fail.");
		if(result_r) {
			Log.logln(this.getData(r).toString());
		}
		Utils.checkTrue(!result_r, "Test should pass.");
		if(c.length == 1) {
			//found the erroouneous configuration
			return c;
		}
		//divide c into c1, and c2, two sub-arrays
		int[] c1 = firstHalf(c);
		int[] c2 = secondHalf(c);
		Utils.stdln("c1 after divide: " + Utils.convertArrayToFlatString(c1));
		Log.logln("c1 size: " + c1.length);
		Utils.stdln("c2 after divide: " + Utils.convertArrayToFlatString(c2));
		Log.logln("c2 size: " + c2.length);
		checkSubArrays(c, c1, c2);
		int[] rc1 = merge(r, c1);
		int[] rc2 = merge(r, c2);
		boolean result_rc1 = this.apply_data(rc1);
		boolean result_rc2 = this.apply_data(rc2);
		Utils.stdln("result of c1 and r, fails: " + result_rc1 + ": " + Utils.convertArrayToFlatString(rc1));
		Utils.stdln("result of c2 and r, fails: " + result_rc2 + ": " + Utils.convertArrayToFlatString(rc2));
		Log.logln("result of c1 and r, fail?  " + result_rc1 + ", size: " + rc1.length);
		Log.logln("result of c2 and r, fail?  " + result_rc2 + ", size: " + rc2.length);
		//has a failure
		if(result_rc1 || result_rc2) {
			//pass
//			System.out.println("result_rc1: " + result_rc1 + ", result_rc2: " + result_rc2);
//			System.out.println("c1: " + FDUtils.convertArrayToFlatString(c1));
//			System.out.println("c2: " + FDUtils.convertArrayToFlatString(c2));
//			System.out.println("r: " + FDUtils.convertArrayToFlatString(r));
			int[] ret = new int[0];
			if(result_rc1 /*&& !result_rc2*/) { //multiple answer
				Utils.stdln("** c1 and r fails, calling dd2(c1, r)");
				Log.logln("search c1 and r, c1 size: " + c1.length + ", r size: " + r.length);
				int[] rc1_result = dd2(c1, r);
				ret = merge(ret, rc1_result);
			}
			if(result_rc2 ) {  //multiple answers
				Utils.stdln("** c2 and r fails, calling dd2(c2, r)");
				Log.logln("search c2 and r, c2 size: " + c2.length + ", r size: " + r.length);
				int[] rc2_result = dd2(c2, r);
				ret = merge(ret, rc2_result);
			}
//			ret = merge(ret, r);
//			FDUtils.checkTrue(this.apply_data(ret), "The result of dd (merge) should fail!, length: " + ret.length
//					+ ", content: " + FDUtils.convertArrayToFlatString(ret));
			return ret;
		} else {
			Utils.stdln("-- interference, search dd2(c1, rc2) and dd2(c2, rc1) ");
			Log.logln("interference case.");
			Log.logln("call dd2 recursively: c1: " + c1.length + ", rc2: " + rc2.length + " result of rc2: "
					+ result_rc2);
			int[] result1 = dd2(c1, rc2);
			Log.logln("call dd2 recursively: c2: " + c2.length + ", rc1: " + rc1.length + " result of rc1: "
					+ result_rc1);
			int[] result2 = dd2(c2, rc1);
			Log.logln("merge result: result1: " + result1.length + ", result2: " + result2.length);
			int[] ret = merge(result1, result2);
//			FDUtils.checkTrue(this.apply_data(ret), "The result of dd interference should fail: "
//					+ FDUtils.convertArrayToFlatString(ret));
			return ret;
		}
	}
	
//	private int[] ddmin2(int[] cs, int n) {
//		//xxx
//		return null;
//	}
	
	private int[] ddsimplify() {
		Utils.checkTrue(this.is_still_fail(this.data), "All the configurations should be faield: " + this.data);
		Utils.checkTrue(!this.apply_data(new int[]{}), "The empty configuration should not fail");
		//create a list of full indices of the data
		int[] full_indices = new int[this.data.size()];
		for(int i = 0; i < full_indices.length; i++) {
			full_indices[i] = i;
		}
		return ddsimplify2(full_indices, 2);
	}
	
	private int[] ddsimplify2(int[] c, int n) {
		Utils.checkTrue(this.is_indices_fail(c), "The configuration before calling simplify2 should fail.");
		Utils.stdln("start ddsimplify2, c[]: " + Utils.convertArrayToFlatString(c));
		int[][] cns = divideByN(c, n);
		Utils.stdln("divide by: " + n);
		int count = 0;
		for(int[] cn : cns) {
			Utils.stdln("count: " + (count++) + ", out of n: " + n + ",  cn: " + Utils.convertArrayToFlatString(cn));
			if(this.is_indices_fail(cn)) {
				Utils.stdln("cn fails");
				return ddsimplify2(cn, 2);
			} else {
				int[] cnbar = complement(c, cn);
				Utils.stdln("compute cnbar: " + Utils.convertArrayToFlatString(cnbar));
				if(this.is_indices_fail(cnbar)) {
					Utils.stdln("cnbar fails.");
					return ddsimplify2(cnbar, Math.max(n - 1, 2));
				}
				if(n < c.length) {
					Utils.stdln("divide in fine grain number: " + Math.min(c.length, 2*n));
					return ddsimplify2(c, Math.min(c.length, 2*n));
				}
//				System.out.println("continue;");
//				continue;
				return c;
			}
		}
		//is this correct???
		Utils.stdln("return c: " + Utils.convertArrayToFlatString(c));
		return c;
	}
	
	/**
	 * if ints has odd length, the first half has one more element
	 * */
	protected static int[] firstHalf(int[] ints) {
		int length = ints.length;
		Utils.checkTrue(length > 0, "length > 0");
		int endExcludeIndex = (length % 2 == 0) ? length/2 : (length + 1)/2;
		int[] ret = new int[endExcludeIndex];
		for(int i = 0; i < endExcludeIndex; i++) {
			ret[i] = ints[i];
		}
		return ret;
	}
	
	protected static int[] secondHalf(int[] ints) {
		int length = ints.length;
		Utils.checkTrue(length > 0, "length > 0");
		int startIncludeIndex = (length % 2 == 0) ? length/2 : (length + 1)/2;
		int[] ret = new int[length - startIncludeIndex];
		for(int i = startIncludeIndex; i < ints.length; i++) {
			ret[i - startIncludeIndex] = ints[i];
		}
		return ret;
	}
	
	protected static int[]  complement(int[] whole_array, int[] minus_array) {
		checkNoDup(whole_array);
		checkNoDup(minus_array);
		for(int i : minus_array) {
			Utils.checkTrue(checkElementIn(whole_array, i), "Element: " + i + " is not in array.");
		}
		int length = whole_array.length - minus_array.length;
		int[] ret = new int[length];
		int count = 0;
		for(int i = 0; i < whole_array.length; i++) {
			if(!checkElementIn(minus_array, whole_array[i])) {
				ret[count++] = whole_array[i];
			}
		}
		Utils.checkTrue(count + minus_array.length == whole_array.length, "Length not correct, count: "
				+ count + ", minus_array: " + minus_array.length + " whole_array: " + whole_array.length);
		return ret;
	}
	
	protected static int[] merge(int[] array1, int[] array2) {
		checkNoDup(array1);
		checkNoDup(array2);
		int total_size = array1.length;
		for(int i : array2) {
			if(!checkElementIn(array1, i)) {
				total_size++;
			}
		}
		//know the size of array
		int[] ret = new int[total_size];
		int index = 0;
		for(int i = 0; i < array1.length; i++) {
			ret[index++] = array1[i];
		}
		for(int i = 0; i < array2.length; i++) {
			if(!checkElementIn(array1, array2[i])) {
				ret[index++] = array2[i];
			}
		}
		//return the array
		return ret;
	}
	
	protected static void checkSubArrays(int[] whole, int[] sub1, int[] sub2) {
		int[] merged = merge(sub1, sub2);
		Utils.checkTrue(whole.length == merged.length, "Length not equal");
		for(int i : whole) {
			checkElementIn(merged, i);
		}
	}
	
	protected static void checkNoOverlap(int[] array1, int[] array2) {
		for(int i : array1) {
			Utils.checkTrue(!checkElementIn(array2, i), "Element: " + i + " should not in array2.");
		}
	}
	
	protected static boolean checkElementIn(int[] array, int element) {
		for(int i : array) {
			if(i == element) {
				return true;
			}
		}
		return false;
	}
	
	protected static void checkNoDup(int[] indices) {
		Arrays.sort(indices);
		for(int i = 0; i < indices.length; i++) {
			if(i == indices.length - 1) {
				continue;
			}
			Utils.checkTrue(indices[i] != indices[i+1], "Duplicates for i and i+1, when i is: " + i +
					", and the value is: " + indices[i]);
		}
	}
	
	protected static int[][] divideByN(int[] indices, int n) {
		Utils.checkTrue(n > 0, "The divide size must be > 0");
		int size = indices.length <= n ? indices.length : n;
		int[][] rets = new int[size][];
		Arrays.sort(indices);
		
		if(size == indices.length) {
			for(int i = 0; i < size; i++) {
				rets[i] = new int[]{indices[i]};
			}
			return rets;
		}
		
		//start to divide
		int persize = indices.length / n;
		for(int i = 0; i < n; i++) {
			//each array from [i, i + persize), excuding the last one
			if(i == n - 1) {
				int[] dividedArray = new int[indices.length - i*persize];
				for(int j = 0; j < dividedArray.length; j++) {
					dividedArray[j] = indices[i*persize + j];
				}
				rets[i] = dividedArray;
			} else {
				int[] dividedArray = new int[persize];
				for(int j = 0; j < dividedArray.length; j++) {
					dividedArray[j] = indices[i*persize + j];
				}
				rets[i] = dividedArray;
			}
		}
		
		return rets;
	}
	
	protected static void checkDivideResults(int[] indices, int[][] rets) {
		int length = 0;
		for(int[] ret : rets) {
			length += ret.length;
		}
		Utils.checkTrue(indices.length == length, "Length not equal, length: "
				+ length + ", expected: " + indices.length);
		int[] all = new int[length];
		int index = 0;
		for(int[] ret : rets) {
			for(int r : ret) {
				all[index++] = r;
			}
		}
		//next we need to check indices == all
		Arrays.sort(indices);
		Arrays.sort(all);
		for(int i = 0; i < indices.length; i++) {
			Utils.checkTrue(indices[i] == all[i], "the " + i + "-th elememnt" 
					+ " is not equal: indices[i]: " + indices[i] + ", all[i]: "
					+ all[i]);
		}
	}
	
	private boolean is_indices_fail(int[] indices) {
		List<T> dlist = new LinkedList<T>();
		for(int index : indices) {
			dlist.add(this.data.get(index));
		}
		return this.is_still_fail(dlist);
	}
	
	/**
	 * True means the failure occurs again. False means the failure does not
	 * occurs.
	 * */
	protected abstract boolean is_still_fail(List<T> data);
}