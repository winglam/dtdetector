/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.tools;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipException;

import plume.Option;
import plume.Options;

import edu.washington.cs.dt.util.CodeUtils;
import edu.washington.cs.dt.util.Files;
import edu.washington.cs.dt.util.Globals;
import edu.washington.cs.dt.util.JarViewer;
import edu.washington.cs.dt.util.Log;
import edu.washington.cs.dt.util.Utils;

public class UnitTestFinder {
	@Option("Show all options")
	public static boolean help = false;
	
	//two options
	@Option("The output file name for found unit tests")
	public static String outputFileName = "." + File.separator + "allunittests.txt";
	
	@Option("The jar file name or path (must be in classpath) where to find unit tests")
	public static String pathOrJarFile; //it can be a path or a jar
	
	@Option("Support JUnit 4.x tests")
	public static boolean junit4 = false;
	
	@Option("Find both Junit 3.x and 4.x tests")
	public static boolean junit3and4 = true;
	
	@Option("Log file")
	public static String log = null;
	
	public List<String> findAllTests() throws ClassNotFoundException, ZipException, IOException {
		File f = new File(pathOrJarFile);
		if(f.isFile()) {
			Utils.checkTrue(f.getName().endsWith(".jar"), "Only support jar files, " +
					"but was given: " + f.getName());
			return getAllTestsFromJar(f);
		} else {
			return getAllTestsFromDir(f);
		}
	}
	
	List<String> getAllTestsFromJar(File jarFile) throws ZipException, IOException, ClassNotFoundException {
		Log.logln("Looking classes in: " + jarFile);
		int totalTestClassNum = 0;
		int totalJunitTestsNum = 0;
		Collection<String> contents = JarViewer.getContentsAsStr(jarFile);
		List<String> tests = new LinkedList<String>();
		for(String content : contents) {
			if(content.endsWith(".class")) {
				Log.logln("processing class: " + content);
				String clzName = content.replace("/", ".").substring(0, content.indexOf(".class"));
//				System.out.println(clzName);
				try {
				   Class<?> clz = Class.forName(clzName);
				   List<String> junitTests = getUnitTestsFromClass(clz);
			       tests.addAll(junitTests);
			       if(!junitTests.isEmpty()) {
			           Log.logln("   has: " + junitTests.size() + " unit tests");
			           totalTestClassNum++;
			           totalJunitTestsNum += junitTests.size();
			       }
				} catch (Throwable e) {
					Log.logln("ERROR in reflectively load class: " + clzName);
					Log.logln("    An exception: " + e + " is thrown");
				}
			}
		}
		Log.logln("Number of test class (with >0 tests: " + totalTestClassNum + ", total tests: " + totalJunitTestsNum);
		return tests;
	}
	
	List<String> getAllTestsFromDir(File dir) throws ClassNotFoundException {
		Collection<File> files = Files.listFiles(dir, null, true);
		List<String> tests = new LinkedList<String>();
		for(File f : files) {
			if(f.getName().endsWith(".class")) {
				String clzName = CodeUtils.pathToClass(f, dir.getAbsolutePath());
			    System.out.println(clzName);
			    Class<?> clz = null;
			    try {
			         clz = Class.forName(clzName);
			    } catch (Throwable e) {
			    	 System.err.println("Can not load: " + clzName);
			    	 continue;
			    }
			    
			    List<String> junitTests = getUnitTestsFromClass(clz);
			    tests.addAll(junitTests);
			    
			}
		}
		return tests;
	}
	
	List<String> getUnitTestsFromClass(Class<?> clz) {
		List<String> tests = new LinkedList<String>();
		Method[] methods = clz.getMethods();
		for(Method method : methods) {
			boolean isUnitTest = false;
			if(junit3and4) {
				isUnitTest = (CodeUtils.isJUnit4XMethod(method) || CodeUtils.isJUnit3XMethod(method));
			} else {
			    if(junit4) {
			    	isUnitTest = CodeUtils.isJUnit4XMethod(method);
			    } else {
			    	isUnitTest = CodeUtils.isJUnit3XMethod(method);
			    }
			}
			
			if(isUnitTest) {
				String testName = clz.getName() + "." + method.getName();
		        tests.add(testName);
			}
		}
		return tests;
	}
	
	public void saveToFile(List<String> allTests) {
		StringBuilder sb = new StringBuilder();
		for(String t : allTests) {
			sb.append(t);
			sb.append(Globals.lineSep);
		}
		try {
			Files.writeToFile(sb.toString(), outputFileName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) throws ClassNotFoundException, ZipException, IOException {
		parse_and_validate_args(args);
		//set the log option
		if(log != null) {
		    Log.logConfig(log);
		}
		UnitTestFinder finder = new UnitTestFinder();
		List<String> allTests = finder.findAllTests();
		finder.saveToFile(allTests);
	}
	
	private static void parse_and_validate_args(String[] args) {
		Options options = new Options("UnitTestFinder usage: ", UnitTestFinder.class);
	    String[] file_args = options.parse_or_usage(args);
	    if(file_args.length != 0) {
	        Utils.flushToStd(file_args);
	        System.exit(1);
	    }
	    if(help) {
	    	Utils.flushToStd(options.usage());
	        System.exit(1);
	    }
	    List<String> errorMsg = new LinkedList<String>();
	    if(pathOrJarFile == null) {
	    	errorMsg.add("You must specify either a jar file or a path via --pathOrJarFile");
	    }
	    if(!errorMsg.isEmpty()) {
	    	Utils.flushToStd(errorMsg.toArray(new String[0]));
	    	Utils.flushToStd(options.usage());
	        System.exit(1);
	    }
	}
}