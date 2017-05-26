package edu.washington.cs.dt.tools.traceanalysis;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import plume.Pair;
import edu.washington.cs.dt.util.Files;
import edu.washington.cs.dt.util.Globals;
import junit.framework.TestCase;

public class TestTestDependenceAnalyzer extends TestCase {

	public void testJFreechartManual() {
		String jfreechartTestFolder = "E:\\testisolation\\dt-instrument-folder\\jfreechart\\dt-output-folder";
		TestExecTraceReader traceReader = new TestExecTraceReader(jfreechartTestFolder);
		TestDependenceAnalyzer analyzer = new TestDependenceAnalyzer(traceReader);
		analyzer.doTraceAnalysis();
		
		List<String> allSafeTests = analyzer.getSafeTests();
		System.out.println("No of safe tests: " + allSafeTests.size());
		
		StringBuilder sb = new StringBuilder();
		for(String test : allSafeTests) {
			System.out.println(test);
			sb.append(test);
			sb.append(Globals.lineSep);
		}
		Files.writeToFileWithNoExp(allSafeTests, "./jfreechart_safetests.txt");
		
//		List<Pair<String, String>> pairs = analyzer.getPariwiseTests();
//		System.out.println("None of pair wise tests: " + pairs.size());
//		String dir = "E:\\testisolation\\dependence-aware-folder\\jfreechart_manual_pairwise\\";
//		int count = 0;
//		for(Pair<String, String> p : pairs) {
//			Files.writeToFileWithNoExp(Arrays.asList(p.a, p.b), dir + count + ".txt");
//			count++;
//		}
	}
	
	public void testCrystalManual() {
			String crystalTestFolder = "E:\\testisolation\\dt-instrument-folder\\crystal\\dt-output-folder";
			TestExecTraceReader traceReader = new TestExecTraceReader(crystalTestFolder);
			TestDependenceAnalyzer analyzer = new TestDependenceAnalyzer(traceReader);
			analyzer.doTraceAnalysis();
			//
			Set<String> testSet = analyzer.getAllLastWriteTests("crystal.model.DataSourceTest.testSetRemoteCmd");
			System.out.println(testSet);
			
			List<String> allSafeTests = analyzer.getSafeTests();
			System.out.println("No of safe tests: " + allSafeTests.size());
			for(String test : allSafeTests) {
				System.out.println(test);
			}
			
			List<Pair<String, String>> pairs = analyzer.getPariwiseTests();
			System.out.println("None of pair wise tests: " + pairs.size());
			String dir = "E:\\testisolation\\dependence-aware-folder\\crystal_manual_pairwise\\";
			int count = 0;
			for(Pair<String, String> p : pairs) {
				Files.writeToFileWithNoExp(Arrays.asList(p.a, p.b), dir + count + ".txt");
				count++;
			}
	}
	
	public void testCrystalAuto() {
		String crystalTestFolder = "E:\\testisolation\\dt-instrument-folder\\crystal\\auto_dt-output-folder";
		TestExecTraceReader traceReader = new TestExecTraceReader(crystalTestFolder);
		TestDependenceAnalyzer analyzer = new TestDependenceAnalyzer(traceReader);
		analyzer.doTraceAnalysis();
		
		List<String> allSafeTests = analyzer.getSafeTests();
		System.out.println("No of safe tests: " + allSafeTests.size());
		for(String test : allSafeTests) {
			System.out.println(test) ;
		}
		
//		List<Pair<String, String>> pairs = analyzer.getPariwiseTests();
//		System.out.println("None of pair wise tests: " + pairs.size());
	}
	
	public void testXMLSecurityAuto() {
		String xmlSecurityAuto = "E:\\testisolation\\dt-instrument-folder\\xml-security\\dt-output-folder";
		TestExecTraceReader traceReader = new TestExecTraceReader(xmlSecurityAuto);
		TestDependenceAnalyzer analyzer = new TestDependenceAnalyzer(traceReader);
		analyzer.doTraceAnalysis();
		analyzer.setTestOrder("./tests/edu/washington/cs/dt/tools/xmlsecurity-auto-test-list.txt");
		
		analyzer.setIgnoredFields(Arrays.asList("Lrandoop/xmlsecurity/RandoopTest1;.debug",
				"Lrandoop/xmlsecurity/RandoopTest0;.debug"));
		
		List<String> allSafeTests = analyzer.getSafeTests();
//		Set<String> tests = analyzer.getAllLastWriteTests("randoop.xmlsecurity.RandoopTest0.test110");
//		System.out.println("Depend tests: " + tests);
//		
//		System.out.println("--------");
//		Set<FieldAccess> wfas = analyzer.getFieldReadWrites().get("randoop.xmlsecurity.RandoopTest0.test40").b;
//		for(FieldAccess fa : wfas) {
//			System.out.println(fa);
//		}
//		System.out.println("---");
//		Set<FieldAccess> rfas = analyzer.getFieldReadWrites().get("randoop.xmlsecurity.RandoopTest0.test110").a;
//		for(FieldAccess fa : rfas) {
//			System.out.println(fa);
//			System.out.println("  -- " + wfas.contains(fa));
//		}
		System.out.println("No of safe tests: " + allSafeTests.size());
		for(String test : allSafeTests) {
			System.out.println(test) ;
		}
		
		System.out.println("pairwsie size: " + analyzer.getPariwiseTests().size());
	}
	
}
