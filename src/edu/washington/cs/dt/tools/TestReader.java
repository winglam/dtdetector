package edu.washington.cs.dt.tools;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.washington.cs.dt.util.Files;
import edu.washington.cs.dt.util.Globals;
import edu.washington.cs.dt.util.Utils;

public class TestReader {
	
	public static String xml_manual_pairwise = "E:\\testisolation\\dependence-aware-folder\\xml_security_manual_pairwise\\";
    public static String jodatime_manual_pairwise = "E:\\testisolation\\dependence-aware-folder\\sampled_jodatime_pairwise\\";
	public static String crystal_manual_pairwise = "E:\\testisolation\\dependence-aware-folder\\crystal_manual_pairwise\\";
    public static String synotpic_manual_pairwise = "E:\\testisolation\\dependence-aware-folder\\synoptic_manual_pairwise\\";
	
    public static String xmlsecurity_auto_pairwise_folder = "E:\\testisolation\\dependence-aware-folder\\xml-security-auto-pairwise\\";
    public static String synoptic_auto_pairwise_folder = "E:\\testisolation\\dependence-aware-folder\\synoptic-auto-pairwise\\";
    public static String jodatime_auto_pairwise_folder = "E:\\testisolation\\dependence-aware-folder\\joda-time-auto-pairwise\\";
    public static String crystal_auto_pairwise_folder = "E:\\testisolation\\dependence-aware-folder\\crystal-auto-pairwise\\";
    
	public static String jodattime_pairwisefile = "E:\\testisolation\\dependence-aware-folder\\exec_pairwise";
	public static String crystal_pairwisefile = "E:\\testisolation\\dependence-aware-folder\\crystal_execute_pairwise";
	public static String synoptic_pairwisefile = "E:\\testisolation\\dependence-aware-folder\\synoptic_execute_pairwise";
	
	public static String xmlsecurity_auto_pairwisefile = "E:\\testisolation\\dependence-aware-folder\\xmlsecurity_auto_exec_pairwise\\";
	public static String synoptic_auto_pairwisefile = "E:\\testisolation\\dependence-aware-folder\\synoptic_auto_execute_pairwise\\";
	public static String jodatime_auto_pairwisefile = "E:\\testisolation\\dependence-aware-folder\\jodatime_auto_exec_pairwise\\";
	public static String crystal_auto_pairwisefile = "E:\\testisolation\\dependence-aware-folder\\crystal_auto_execute_pairwise\\";
	
	public static Set<List<String>>  readTestsFromAllFiles(String folderName, int numPerFile) {
		Set<List<String>> tests = new LinkedHashSet<List<String>>();
		
		Collection<File> files = Files.listFiles(new File(folderName), null, false);
//		System.out.println(files.size());
		for(File f : files) {
			List<String> testsInFile = Files.readWholeNoExp(f.getAbsolutePath());
			Utils.checkTrue(testsInFile.size() == numPerFile, "Not equal");
			tests.add(testsInFile);
		}
		
		return tests;
	}
	
	private static Random r = new Random();
	private static String sep = "----";
	public static void sampleTestsAndWriteToDisk(int samplenum,  String fileName, String destDir) {
		List<String> list = Files.readWholeNoExp(fileName);
		int size = list.size() /3;
		int count = 0;
		while(count < samplenum) {
			int offset = r.nextInt(size) * 3;
			String test1 = list.get(offset);
			String test2 = list.get(offset + 1);
			Utils.checkTrue(!test1.startsWith(sep), "wrong: " + test1);
			Utils.checkTrue(!test2.startsWith(sep), "wrong: " + test2);
			
			Files.writeToFileWithNoExp(Arrays.asList(test1, test2), destDir + Globals.pathSep + count + ".txt");
			count++;
		}
	}
	
	public static void writeAllTestsToDisk(String fileName, String destDir) {
		List<String> list = Files.readWholeNoExp(fileName);
		int size = list.size();
		int index = 0;
		while(index < size) {
			String test1 = list.get(index);
			String test2 = list.get(index + 1);
			Utils.checkTrue(!test1.startsWith(sep), "wrong: " + test1);
			Utils.checkTrue(!test2.startsWith(sep), "wrong: " + test2);
			
			Files.writeToFileWithNoExp(Arrays.asList(test1, test2), destDir + Globals.pathSep + index + ".txt");
			index = index + 3;
		}
	}
	
	public static void main(String[] args) {
//		Set<List<String>> allTests = readTestsFromAllFiles(xml_manual_pairwise, 2);
//		System.out.println("test num: " + allTests.size());
//		sampleTestsAndWriteToDisk(1000, jodattime_pairwisefile, jodatime_manual_pairwise);
//		sampleTestsAndWriteToDisk(1000, jodattime_pairwisefile, crystal_manual_pairwise);
//		writeAllTestsToDisk(synoptic_pairwisefile, synotpic_manual_pairwise);
//		writeAllTestsToDisk(xmlsecurity_auto_pairwisefile, xmlsecurity_auto_pairwise_folder);
//		sampleTestsAndWriteToDisk(1000, synoptic_auto_pairwisefile, synoptic_auto_pairwise_folder);
//		sampleTestsAndWriteToDisk(1000, jodatime_auto_pairwisefile, jodatime_auto_pairwise_folder);
		sampleTestsAndWriteToDisk(1000, crystal_auto_pairwisefile, crystal_auto_pairwise_folder);
	}
	
}
