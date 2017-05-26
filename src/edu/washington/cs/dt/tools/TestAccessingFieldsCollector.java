/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.tools;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.dt.util.Command;
import edu.washington.cs.dt.util.Files;
import edu.washington.cs.dt.util.Globals;
import edu.washington.cs.dt.util.Utils;
import plume.Option;
import plume.Options;

public class TestAccessingFieldsCollector {
	
	@Option("See all options")
	public static boolean help = false;
	
	@Option("The location of the agent jar")
	public static String agentjar = "./agent/agent.jar";
	
	@Option("The default classpath string")
	public static String classPath = Globals.getClassPath();
	
	@Option("A file containing all tests to be execute")
	public static String testFile = null;
	
	@Option("The default output dir")
	public static String outputDir = "." + Globals.fileSep + "output";
	
	public static void main(String[] args) {
		new TestAccessingFieldsCollector().nonStaticMain(args);
	}
	
	private void nonStaticMain(String[] args) {
		parse_and_valid_args(args);
		//run each test in isolation
		Collection<String> allTests = Files.readWholeNoExp(testFile);
		for(String test : allTests) {
			System.out.println("executing test: " + test);
			
			List<String> commandList = new LinkedList<String>();
			commandList.add("java");
			commandList.add("-javaagent:" + agentjar + "=" + test);
			commandList.add("-cp");
			commandList.add(classPath);
			commandList.add("edu.washington.cs.dt.util.SimpleTestRunner");
			commandList.add(test);
			
			System.out.println("executing: " + commandList);
			Command.exec(commandList.toArray(new String[0]));
		}
	}
	
	private void parse_and_valid_args(String[] args) {
		Options options = new Options("TestAccessingFieldsCollector usage: ", TestAccessingFieldsCollector.class);
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
	    if(testFile == null) {
	    	errorMsg.add("You must specify either a file containing all tests via --testFile");
	    }
	    if(!errorMsg.isEmpty()) {
	    	Utils.flushToStd(errorMsg.toArray(new String[0]));
	    	Utils.flushToStd(options.usage());
	        System.exit(1);
	    }
	}
}