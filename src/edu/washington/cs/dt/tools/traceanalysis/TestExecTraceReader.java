package edu.washington.cs.dt.tools.traceanalysis;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.washington.cs.dt.util.Files;

import plume.Pair;

//given a folder containing all test execution traces
//return all tests and its read/write fields 
public class TestExecTraceReader {
	
	static String TRACE = "_trace.txt";
	
	static String READ = "READ";
	
	static String WRITE = "WRITE";
	
	public final String folderName;
	
	public TestExecTraceReader(String folderName) {
		this.folderName = folderName;
	}
	
	private List<String> testOrder = new ArrayList<String>();
	
	public List<String> getTestOrder() {
		return testOrder;
	}
	
	public void setTestOrder(String fileName) {
		this.testOrder = Files.readWholeNoExp(fileName);
	}
	
	//a test ==> read field set,  write field set
	public Map<String, Pair<Set<FieldAccess>, Set<FieldAccess>>> readFieldAccessInfo() {
		Map<String, Pair<Set<FieldAccess>, Set<FieldAccess>>> map = new LinkedHashMap<String, Pair<Set<FieldAccess>, Set<FieldAccess>>>();
		
		Collection<File> files = Files.listFiles(new File(this.folderName), null, false);
		for(File file : files) {
			//parse each file
			String testName = file.getName().substring(0, file.getName().indexOf(TRACE));
			//addd to the order list
			testOrder.add(testName);
			
			Set<FieldAccess> reads = new LinkedHashSet<FieldAccess>();
			Set<FieldAccess> writes = new LinkedHashSet<FieldAccess>();
			List<String> lines = Files.readWholeNoExp(file.getAbsolutePath());
			for(String line : lines) {
				if(line.equals("null")) {
					continue;
				}
//				System.out.println(file.getAbsolutePath());
				FieldAccess fa = FieldAccess.parseFieldAccess(line);
				if(fa == null) {
					continue;
				}
				if(line.startsWith(READ)) {
					if(!writes.contains(fa)) {
						//read before write
						reads.add(fa);
					}
				} else if (line.startsWith(WRITE)) {
					writes.add(fa);
				} else {
					throw new Error(line);
				}
			}
			map.put(testName, Pair.of(reads, writes));
		}
		
		return map;
	}
	
	public static void main(String[] args) {
		String crystalTestFolder = "E:\\testisolation\\dt-instrument-folder\\crystal\\dt-output-folder";
		TestExecTraceReader traceReader = new TestExecTraceReader(crystalTestFolder);
		Map<String, Pair<Set<FieldAccess>, Set<FieldAccess>>> map = traceReader.readFieldAccessInfo();
		for(String test : traceReader.getTestOrder()) {
			System.out.println(test);
			System.out.println("   read: " + map.get(test).a.size());
			System.out.println("   write: " + map.get(test).b.size());
		}
	}
}