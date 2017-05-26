/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PermutationGenerator {
	
	/*start from 1, 2, 3, 4, ..., total*/
	public final int total;
	public final int[] pool;
	
	/*must <= total*/
	public final int select;
	
	/*an iterator to create permutation */
	private final Iterator<int[]> iterator;
	
	/*FIXME not optimized yet*/
	private final List<int[]> permutations;
	
	public PermutationGenerator(int total, int select) {
		Utils.checkTrue(total > 0, "Total: " + total + " must > 0");
		Utils.checkTrue(select <= total, "Select: " + select + " must <= total");
		Utils.checkTrue(select > 0, "Select must > 0");
		this.total = total;
		this.pool = createPool(total);
		this.select = select;
		//be aware, can not swap the order of the following 2 stmts
		this.permutations = computeAllPermutations(select, pool);
		this.iterator = iterator();
	}
	
	public int getPermutationNum() {
		int r = 1;
		for(int k = 0; k < select; k++) {
			r = r*(total - k);
		}
		return r;
	}
	
	public boolean hasNext() {
		return this.iterator.hasNext();
	}
	
	/* get the index of permutation generated, note that index is
	 * starting from 1, not 0*/
	public int[] getNext() {
		return this.iterator.next();
	}
	
	private Iterator<int[]> iterator() {
		return new Iterator<int[]>() {
			int index = 0;
			final int totalNum = getPermutationNum();
			
			@Override
			public boolean hasNext() {
				return index < totalNum;
			}
			@Override
			public int[] next() {
				int[] r = permutations.get(index);
				index = index + 1;
				return r;
			}
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	private List<int[]> computeAllPermutations(int k, int[] pool) {
		List<int[]> all = new LinkedList<int[]>();
		if(k == 1) {
			for(int n : pool) {
				all.add(new int[]{n});
			}
			return all;
		}
		
		for(int index = 0; index < pool.length; index++) {
			int selected = pool[index];
			
			int[] newPool = new int[pool.length - 1];
			for(int j = 0; j < pool.length; j++) {
				if(j<index) {
					newPool[j] = pool[j];
				}
				else if (j > index) {
					newPool[j-1] = pool[j];
				}
			}
			List<int[]> lists = computeAllPermutations(k-1, newPool);
			
			for(int[] list : lists) {
				//create select + list
				int[] r = new int[list.length + 1];
				r[0] = selected;
				for(int j = 0; j < list.length; j++) {
					r[j+1] = list[j];
				}
				all.add(r);
			}
		}
		
		return all;
	}
	
	private int[] createPool(int size) {
		int[] pool = new int[size];
		for(int i = 0; i < size; i++) {
			pool[i] = i;
		}
		return pool;
	}
}