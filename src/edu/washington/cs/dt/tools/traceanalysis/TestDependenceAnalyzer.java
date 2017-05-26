package edu.washington.cs.dt.tools.traceanalysis;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import plume.Pair;

import edu.washington.cs.dt.util.Files;
import edu.washington.cs.dt.util.Utils;

public class TestDependenceAnalyzer {

	public final TestExecTraceReader fieldAccessReader;
	
	private Map<String, Pair<Set<FieldAccess>, Set<FieldAccess>>> fieldReadWrites = null;
	private List<String> testExecOrder = null;
	
	private Set<String> ignoredFields = new HashSet<String>();
	
	public TestDependenceAnalyzer(TestExecTraceReader reader) {
		Utils.checkNull(reader, "");
		this.fieldAccessReader = reader;
	}
	
	public void setIgnoredFields(Collection<String> fields) {
		this.ignoredFields.addAll(fields);
	}
	
	public void doTraceAnalysis() {
		this.fieldReadWrites = this.fieldAccessReader.readFieldAccessInfo();
		this.testExecOrder = this.fieldAccessReader.getTestOrder();
	}
	
	public void setTestOrder(String fileName) {
		this.testExecOrder = Files.readWholeNoExp(fileName);
	}
	
	Map<String, Pair<Set<FieldAccess>, Set<FieldAccess>>> getFieldReadWrites() {
	    return fieldReadWrites;	
	}
	
	//for each read field
	private Map<String, Set<String>> allLastWriteCache = new LinkedHashMap<String, Set<String>>();
	
	public Set<String> getAllLastWriteTests(String testName) {
		if(allLastWriteCache.containsKey(testName)) {
			return allLastWriteCache.get(testName);
		}
		Set<FieldAccess> reads = this.fieldReadWrites.get(testName).a;
		Set<String> testSet = new LinkedHashSet<String>();
		for(FieldAccess fa : reads) {
			if(this.ignoredFields.contains(fa.fieldName)) {
				continue;
			}
			String lastWriteTest = this.getLastWriteTest(testName, fa);
			if(lastWriteTest != null) {
			    testSet.add(lastWriteTest);
//			    if(testName.endsWith("test110")) {
			        System.out.println("  " + testName + " depends on " + lastWriteTest + " AT: "+ fa);
//			    }
			}
		}
		allLastWriteCache.put(testName, testSet);
		return testSet;
	}
	
	private Map<String, Map<FieldAccess, String>> lastWriteCache =
			new LinkedHashMap<String, Map<FieldAccess, String>>();
	
	public String getLastWriteTest(String testName, FieldAccess fa) {
//		if(testName.equals("randoop.xmlsecurity.RandoopTest0.test110")) {
//			System.out.println("Analyzing: " + testName);
//			System.out.println("   read access: " + fa);
//		}
		if(lastWriteCache.containsKey(testName)) {
			if(lastWriteCache.get(testName).containsKey(fa)) {
				return lastWriteCache.get(testName).get(fa);
			}
		}
		String lastWriteTest = null;
		int index = this.testExecOrder.indexOf(testName);
		for(int i = index - 1; i >=0; i --) {
			String tName = this.testExecOrder.get(i);
//			if(testName.equals("randoop.xmlsecurity.RandoopTest0.test110")) {
//				System.out.println("    check test: " + tName + ", " + i);
//			}
			Set<FieldAccess> writes = this.fieldReadWrites.get(tName).b;
			if(writes.contains(fa)) {
				lastWriteTest = tName;
//				if(testName.equals("randoop.xmlsecurity.RandoopTest0.test110")) {
//					System.out.println("  lastWrite: " + lastWriteTest);
//				}
				break;
			}
		}
		if(!lastWriteCache.containsKey(testName)) {
			lastWriteCache.put(testName, new LinkedHashMap<FieldAccess, String>());
		}
		if(lastWriteTest != null) {
			lastWriteCache.get(testName).put(fa, lastWriteTest);
		}
		return lastWriteTest;
	}
	
	public List<String> getSafeTests() {
		List<String> safeTests = new LinkedList<String>();
		for(String testName : this.testExecOrder) {
			//may not be executed
			if(!this.fieldReadWrites.containsKey(testName)) {
				continue;
			}
			Set<String> lastWriteTests = this.getAllLastWriteTests(testName);
			if(lastWriteTests.isEmpty()) {
				safeTests.add(testName);
			}
		}
		return safeTests;
	}
	
	public List<Pair<String, String>> getPariwiseTests() {
		List<Pair<String, String>> pairList = new LinkedList<Pair<String, String>>();
		for(String test1 : this.testExecOrder) {
			for(String test2 : this.testExecOrder) {
				//in the execution order of test1, test2
				if(!test1.equals(test2)) {
					if(!this.fieldReadWrites.containsKey(test2)) {
						continue;
					}
					Set<String> allLastWrites = this.getAllLastWriteTests(test2);
					//when to skip
					if(allLastWrites.isEmpty()) {
						continue;
					}
					if(allLastWrites.size() == 1 && allLastWrites.contains(test1)) {
						continue;
					}
					pairList.add(Pair.of(test1, test2));
				}
			}
		}
		return pairList;
	}
}