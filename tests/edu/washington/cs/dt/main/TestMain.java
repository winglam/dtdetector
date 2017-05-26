/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipException;

import edu.washington.cs.dt.util.Utils;

import junit.framework.TestCase;

public class TestMain extends TestCase {

	public void testArgs1() {
		Main.main(new String[]{"--help"});
	}
	
	public void testArgs2() {
		Main.main(new String[]{"--minimize=true"});
	}
	
	public void testArgs3() {
		Main.main(new String[]{"--minimize=true", "--isolate"});
	}
	
	public void testArgs4() {
		Main.main(new String[]{"--minimize=true", "--isolate", "--reverse"});
	}
	
	public void testArgs5() {
		try {
		    Main.main(new String[]{"--minimize=true", "--isolate", "--tests=nontext"});
		    fail();
		} catch (RuntimeException e) {
			Utils.checkTrue(e.getCause() instanceof FileNotFoundException, "wrong exception types");
		}
	}
	
	public void testArgs6() {
		Main.main(new String[]{"--minimize=true", "--combination"});
	}
	
	public void testSimpleExamples1() throws ZipException, ClassNotFoundException, IOException {
		Main.main(new String[]{"--minimize=true", "--isolate", "--tests=./exampletests.txt"});
	}
	
	public void testSimpleExamples2() throws ZipException, ClassNotFoundException, IOException {
		Main.main(new String[]{"--minimize=true", "--reverse", "--tests=./exampletests.txt"});
	}
	
	public void testSimpleExamplesDumpResults() throws ZipException, ClassNotFoundException, IOException {
		Main.main(new String[]{"--minimize=false", "--isolate",
				"--tests=./exampletests.txt",
				"--fixedOrderReport=./fixed_order_tmp.txt",
				"--isolationReport=./isolation_tmp.txt"});
	}
	
	public void testSimpleExamplesRandom() throws ZipException, ClassNotFoundException, IOException {
		Main.main(new String[]{"--minimize=true", "--randomize", "--tests=./exampletests.txt"});
	}
	
	public void testSimpleExamplesRandomNoExecSeq() throws ZipException, ClassNotFoundException, IOException {
		Main.main(new String[]{"--minimize=true", "--randomize", "--printexecseq=false", "--tests=./exampletests.txt"});
	}
	
	public void testSimpleExamples3() throws ZipException, ClassNotFoundException, IOException {
		long start = System.currentTimeMillis();
		Main.main(new String[]{"--minimize=true", "--combination", "--k=2", "--tests=./exampletests.txt"});
		System.out.println("With cache: " + (System.currentTimeMillis() - start));
	}
	
	public void testSimpleExamples3WithoutCache() throws ZipException, ClassNotFoundException, IOException {
		long start = System.currentTimeMillis();
		Main.main(new String[]{"--minimize=true", "--combination", "--k=2", "--tests=./exampletests.txt", "--enablecache=false"});
		System.out.println("Without cache: " + (System.currentTimeMillis() - start));
	}
	
	public void testJUnit4DependentTests() {
		Main.main(new String[]{"--minimize=false", "--junit4=true", "--isolate", "--tests=./tests/edu/washington/cs/dt/samples/junit4x/junit4dependenttests.txt"});
	}
}
