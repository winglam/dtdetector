/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.tools;

import java.io.IOException;
import java.util.List;
import java.util.zip.ZipException;

import edu.washington.cs.dt.samples.junit4x.ExampleForUnitTestFinder;

import junit.framework.TestCase;

public class TestUnitTestFinder extends TestCase {
	
	public void testFindClassInJar() throws ZipException, ClassNotFoundException, IOException {
		//this jar must be in classpath
		UnitTestFinder.pathOrJarFile = "D:\\research\\testisolation\\Workspace\\DependentTestDetector\\jtopas.jar";
		UnitTestFinder finder = new UnitTestFinder();
		List<String> allTests = finder.findAllTests();
		finder.saveToFile(allTests);
	}
	
	public void testFindClassInPath() throws ZipException, ClassNotFoundException, IOException {
		//this dir must be in classpath
		UnitTestFinder.pathOrJarFile = "D:\\research\\testisolation\\Workspace\\DependentTestDetector\\bin";
		UnitTestFinder finder = new UnitTestFinder();
		List<String> allTests = finder.findAllTests();
		finder.saveToFile(allTests);
	}
	
	public void testArgs1() throws ZipException, ClassNotFoundException, IOException {
		UnitTestFinder.main(new String[]{"--help"});
	}
	
	public void testArgs2() throws ZipException, ClassNotFoundException, IOException {
		UnitTestFinder.main(new String[]{"--outputFileName=./text"});
	}
	
	public void testArgs3() throws ZipException, ClassNotFoundException, IOException {
		UnitTestFinder.main(new String[]{"--pathOrJarFile=./a.jar"});
	}
	
	public void testFindJUnit4() {
		UnitTestFinder.junit4 = true;
		UnitTestFinder finder = new UnitTestFinder();
		List<String> tests = finder.getUnitTestsFromClass(ExampleForUnitTestFinder.class);
		System.out.println(tests);
		assertEquals("[edu.washington.cs.dt.samples.junit4x.ExampleForUnitTestFinder.testX]",
				tests.toString());
	}
	
	public void tearDown() {
		UnitTestFinder.pathOrJarFile = null;
		UnitTestFinder.junit4 = false;
	}
	
}
