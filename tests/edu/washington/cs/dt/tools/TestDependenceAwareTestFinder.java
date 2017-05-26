package edu.washington.cs.dt.tools;

import java.util.List;
import java.util.Set;

import edu.washington.cs.dt.util.Files;
import edu.washington.cs.dt.util.Log;
import junit.framework.TestCase;

public class TestDependenceAwareTestFinder extends TestCase {
	
	public void testXMLSecurityPairwise_manual() {
		runTests(TestRandomizedDependentTestFinder.xmlSecurityFile_manual, TestReader.xml_manual_pairwise,
				"./dependence-aware_2_xmlsecurity_manual.txt", 2);
	}
	
	public void testJFreechartIsolation_manual() {
		String logFileName = "./dependence-aware_jfreechart_1_manual.txt";
		String testFile = TestRandomizedDependentTestFinder.jfreechart_manual;
		int k = 1;
		String safeFile = "./tests/edu/washington/cs/dt/tools/jfreechart_safetests.txt";
		this.runWithSafeTests(logFileName, testFile, safeFile, k);
	}
	
	public void testJodatimeIsolation_manual() {
		String logFileName = "./dependence-aware_jodatime_1_manual.txt";
		String testFile = TestRandomizedDependentTestFinder.jodatimeFile_manual;
		int k = 1;
		String safeFile = "./tests/edu/washington/cs/dt/tools/joda-time-manual-safetests.txt";
		this.runWithSafeTests(logFileName, testFile, safeFile, k);
	}
	
	public void testJodatimePairwise_manual() {
		runTests(TestRandomizedDependentTestFinder.jodatimeFile_manual, TestReader.jodatime_manual_pairwise,
				"./dependence-aware_2_jodatime_sample_manual.txt", 2);
	}
	
	public void testSynopticPairwise_manual() {
		runTests(TestRandomizedDependentTestFinder.synopticFile_manual, TestReader.synotpic_manual_pairwise,
				"./dependence-aware_2_synoptic_all_manual.txt", 2);
	}
	
	public void testCrystalIsolation_manual() {
		String logFileName = "./dependence-aware_crystal_1_manual.txt";
		String testFile = TestRandomizedDependentTestFinder.crystalFile_manual;
		int k = 1;
		String safeFile = "./tests/edu/washington/cs/dt/tools/crystal-manual-safetests.txt";
		this.runWithSafeTests(logFileName, testFile, safeFile, k);
	}
	
	public void testCrystalPairwise_manual() {
		runTests(TestRandomizedDependentTestFinder.crystalFile_manual, TestReader.crystal_manual_pairwise,
				"./dependence-aware_2_crystal_all_manual.txt", 2);
	}
	
	public void testSynopticIsolation_manual() {
		String logFileName = "./dependence-aware_synoptic_1_manual.txt";
		String testFile = TestRandomizedDependentTestFinder.synopticFile_manual;
		int k = 1;
		String safeFile = "./tests/edu/washington/cs/dt/tools/synoptic-manual-safetests.txt";
		this.runWithSafeTests(logFileName, testFile, safeFile, k);
	}
	
	//below are for auto tests
	public void testXMLSecurity_1_auto() {
		String logFileName = "./dependence-aware_xmlsecurity_1_auto.txt";
		String testFile = TestRandomizedDependentTestFinder.xmlSecurityFile_auto;
		int k = 1;
		String safeFile = "./tests/edu/washington/cs/dt/tools/xml-security-auto-safetests.txt";
		this.runWithSafeTests(logFileName, testFile, safeFile, k);
	}
	
	public void testXMLSecurity_2_auto() {
		runTests(TestRandomizedDependentTestFinder.xmlSecurityFile_auto, TestReader.xmlsecurity_auto_pairwise_folder,
				"./dependence-aware_2_xmlsecurity_sample_auto.txt", 2);
	}
	
	public void testSynoptic_2_auto_sample() {
		runTests(TestRandomizedDependentTestFinder.synopticFile_auto, TestReader.synoptic_auto_pairwise_folder,
				"./dependence-aware_2_synoptic_sample_auto.txt", 2);
	}
	
	//
	
	public void testJodatime_1_auto() {
		String logFileName = "./dependence-aware_jodatime_1_auto.txt";
		String testFile = TestRandomizedDependentTestFinder.jodatimeFile_auto;
		int k = 1;
		String safeFile = "./tests/edu/washington/cs/dt/tools/jodatime-auto-safetests.txt";
		this.runWithSafeTests(logFileName, testFile, safeFile, k);
	}
	
	public void testJodatime_2_sample_auto() {
		runTests(TestRandomizedDependentTestFinder.jodatimeFile_auto, TestReader.jodatime_auto_pairwise_folder,
				"./dependence-aware_2_jodatime_sample_auto.txt", 2);
	}
	
	public void testCrystal_1_auto() {
		String logFileName = "./dependence-aware_crystal_1_auto.txt";
		String testFile = TestRandomizedDependentTestFinder.crystalFile_auto;
		int k = 1;
		String safeFile = "./tests/edu/washington/cs/dt/tools/crystal-auto-safetests.txt";
		this.runWithSafeTests(logFileName, testFile, safeFile, k);
	}
	
	public void testCrystal_2_sample_auto() {
		runTests(TestRandomizedDependentTestFinder.crystalFile_auto, TestReader.crystal_auto_pairwise_folder,
				"./dependence-aware_2_crystal_sample_auto.txt", 2);
	}
	
	public void runWithSafeTests(String logFileName, String testFile, String safeFile, int k) {
		Log.logConfig(logFileName);
		BoundedDependentTestFinder.verbose = true;
		BoundedDependentTestFinder boundedDTFinder
		    = new BoundedDependentTestFinder(testFile, k);
		List<String> safeTests = Files.readWholeNoExp(safeFile);
		boundedDTFinder.addSafeTests(safeTests);
		Set<String> dts = boundedDTFinder.findDependentTests();
		for(String t : dts) {
			System.out.println("    " + t);
		}
	}

	public void runTests(String testFile, String folderName, String logFileName, int k) {
		Log.logConfig(logFileName);
		DependenceAwareDependentTestFinder.verbose = true;
		DependenceAwareDependentTestFinder dafinder
		    = new DependenceAwareDependentTestFinder(testFile, folderName, k);
		Set<String> dts = dafinder.findDependentTests();
		for(String t : dts) {
			System.out.println("    " + t);
		}
	}
	
}
