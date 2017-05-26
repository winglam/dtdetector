package edu.washington.cs.dt.tools;

import java.util.Set;

import edu.washington.cs.dt.util.Log;
import junit.framework.TestCase;

public class TestBoundedDependentTestFinder extends TestCase {

	public void testToyExamples() {
		this.runTests(TestRandomizedDependentTestFinder.exampleFile, 
				"./bounded_1_toy_results.txt", 1);
		this.runTests(TestRandomizedDependentTestFinder.exampleFile,
				"./bounded_2_toy_results.txt", 2);
		this.runTests(TestRandomizedDependentTestFinder.exampleFile,
				"./bounded_3_toy_results.txt", 3);
	}
	
	public void testJFreeChart_manual() {
		this.runTests(TestRandomizedDependentTestFinder.jfreechart_manual, 
				"./bounded_1_jfreechart_manualtxt", 1);
	}
	
	public void testJFreeChart_manual_2() {
		this.runTests(TestRandomizedDependentTestFinder.jfreechart_manual, 
				"./bounded_2_jfreechart_manualtxt", 2);
	}
	
	public void testXMLSecurity_manual() {
		this.runTests(TestRandomizedDependentTestFinder.xmlSecurityFile_manual, 
				"./bounded_1_xmlsecurity_manualtxt", 1);
		this.runTests(TestRandomizedDependentTestFinder.xmlSecurityFile_manual,
				"./bounded_2_xmlsecurity_manual.txt", 2);
//		this.runTests(TestRandomizedDependentTestFinder.xmlSecurityFile_manual,
//				"./bounded_3_xmlsecurity_manual.txt", 3);
	}
	
	public void testXMLSecurity_manual_safe() {
		this.runTests("./tests/edu/washington/cs/dt/tools/xml-security-safe-manual-tests.txt", 
				"./bounded_1_xmlsecurity_safe_manualtxt", 1);
		this.runTests(TestRandomizedDependentTestFinder.xmlSecurityFile_manual,
				"./bounded_2_xmlsecurity_safe_manual.txt", 2);
//		this.runTests(TestRandomizedDependentTestFinder.xmlSecurityFile_manual,
//				"./bounded_3_xmlsecurity_manual.txt", 3);
	}
	
	public void testCrystal_manual() {
		this.runTests(TestRandomizedDependentTestFinder.crystalFile_manual, 
				"./bounded_1_crystal_manualtxt", 1);
		this.runTests(TestRandomizedDependentTestFinder.crystalFile_manual,
				"./bounded_2_crystal_manual.txt", 2);
	}
	
	public void testJodatime_manual() {
		this.runTests(TestRandomizedDependentTestFinder.jodatimeFile_manual, 
				"./bounded_1_jodatime_manualtxt", 1);
	}
	
	public void testJodatime_manual_sample_2() {
		runTestsWithSampling("./bounded_2_jodatime_sample_2.txt",
				TestRandomizedDependentTestFinder.jodatimeFile_manual, 2,
				1000, 0.03f);
	}
	
	public void testJodatime_auto_sample_2() {
		runTestsWithSampling("./bounded_2_jodatime_auto_sample_2.txt",
				TestRandomizedDependentTestFinder.jodatimeFile_auto, 2,
				1000, 0.03f);
	}
	
	
	//run
	public void testSynoptic_manual_sampled() {
		this.runTests(TestRandomizedDependentTestFinder.synopticFile_manual, 
				"./bounded_1_synoptic_manualtxt", 1);
	}
	
	
	
	//for automated tests
	public void testXMLSecurity_auto() {
		this.runTests(TestRandomizedDependentTestFinder.xmlSecurityFile_auto, 
				"./bounded_1_xmlsecurity_auto.txt", 1);
	}
	
	//run
	public void testXMLSecurity_auto_sample_2() {
		this.runTestsWithSampling("./bounded_2_xmlsecurity_sampled_auto.txt",
				TestRandomizedDependentTestFinder.xmlSecurityFile_auto, 
				 2, 1000, 0.03f);
	}
	
	public void testCrystal_auto() {
		this.runTests(TestRandomizedDependentTestFinder.crystalFile_auto, 
				"./bounded_1_crystal_auto.txt", 1);
	}
	
	public void testCrystal_auto_sample_2() {
		this.runTestsWithSampling("./bounded_2_crystal_sampled_auto.txt",
				TestRandomizedDependentTestFinder.crystalFile_auto, 
				 2, 1000, 0.03f);
	}
	
	public void testJodatime_auto() {
		this.runTests(TestRandomizedDependentTestFinder.jodatimeFile_auto, 
				"./bounded_1_jodattime_auto.txt", 1);
	}
	
	//run
	public void testSynoptic_auto_1() {
		this.runTests(TestRandomizedDependentTestFinder.synopticFile_auto,
				"./bounded_1_synoptic_auto.txt", 1);
	}
	
	public void testSynoptic_auto_test() {
		this.runTests(TestRandomizedDependentTestFinder.synopticFile_auto,
				"./bounded_1_synoptic_auto_TEST.txt", 1);
	}
	
	public void testSynoptic_auto_sampled_2() {
		this.runTestsWithSampling("./bounded_2_synoptic_sampled_auto.txt",
				TestRandomizedDependentTestFinder.synopticFile_auto,
				 2, 1000, 0.03f);
	}
	
	public void runTests(String testFile, String fileName, int k) {
		Log.logConfig(fileName);
		BoundedDependentTestFinder.verbose = true;
		BoundedDependentTestFinder boundedDTFinder
		    = new BoundedDependentTestFinder(testFile, k);
		Set<String> dts = boundedDTFinder.findDependentTests();
		for(String t : dts) {
			System.out.println("    " + t);
		}
	}
	
	public void runTestsWithSampling(String logFileName, String testFile, int k,
			int sampleSize, float rate) {
		Log.logConfig(logFileName);
		BoundedDependentTestFinder.verbose = true;
		BoundedDependentTestFinder boundedDTFinder
		    = new BoundedDependentTestFinder(testFile, k);
		boundedDTFinder.useRandomDiscarder(sampleSize, rate);
		Set<String> dts = boundedDTFinder.findDependentTests();
		for(String t : dts) {
			System.out.println("    " + t);
		}
	}
}
