package edu.washington.cs.dt.plugin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.dt.util.Command;

public class PluginMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {	
		// list to parse the arguments
		List<String> argsList = new ArrayList<String>(Arrays.asList(args));		
		
		int kIndex = argsList.indexOf("-k");
		int inputFileIndex = argsList.indexOf("-inputFile");
		int outputFileIndex = argsList.indexOf("-outputFile");
		
		// get list of tests
		if (outputFileIndex != -1) {
			// get index of output file
			int outputFileNameIndex = outputFileIndex + 1;
			if (outputFileNameIndex >= argsList.size()) {
				System.err.println("Output file argument is specified but a file name is not. Please use the format: -outputFile afilename");
				System.exit(0);
			}
			String outputFileName = "outputFile=" + argsList.get(outputFileNameIndex);
			argsList.add(outputFileName);
		} else		
		// k is specified
		if (kIndex != -1 && inputFileIndex != -1) {
			// get index of input file
			int inputFileNameIndex = inputFileIndex + 1;
			if (inputFileNameIndex >= argsList.size()) {
				System.err.println("Input file argument is specified but a file name is not. Please use the format: -inputFile afilename");
				System.exit(0);
			}		
	
			// get index of k
			int kNumIndex = kIndex + 1;
			if (kNumIndex >= argsList.size()) {
				System.err.println("k argument is specified but a number is not. Please use the format: -k x where x is a number");
				System.exit(0);
			}
			
			String kStr = argsList.get(kNumIndex);
			int k = Integer.parseInt(kStr);
			String inputFileName = argsList.get(inputFileNameIndex);
			
			// not fixed order
			if (k > 0) {		
				// get all the tests name
				List<String> testNames = getTestNames(inputFileName);
				List<String> permutations = computeAllPermutations(k, testNames);
				
				int numOfTests = permutations.size();
				// for each permutation launch a new JVM
				for (int i = 0; i < numOfTests; i++) {
					List<String> argsListCopy = new ArrayList<String>(argsList);
					argsListCopy.add("cmdLineTest=" + permutations.get(i));
					Command.exec(argsListCopy.toArray(new String[argsListCopy.size()]));
					System.out.println("\n----------------------------------------------- Finished Test " + (i + 1) + 
						" of " + numOfTests + " -----------------------------------------------\n");
				}
				return;
			} else 
				argsList.add("inputFile=" + inputFileName);	
		} 
		Command.exec(argsList.toArray(new String[argsList.size()]));
	}
	
	// WL takes a file as input and returns a List<String> of test names separated by "\n"
	// same as the method in EclipseTestRunner
	private static List<String> getTestNames(String filename) {
		if (filename == null)
			return null;
		List<String> listContents = null;
	    StringBuilder contentsBuilder = new StringBuilder();
	    
	    try {
	      //use buffering, reading one line at a time
	      //FileReader always assumes default encoding is OK!
	      BufferedReader input =  new BufferedReader(new FileReader(filename));
	      try {
	        String line = null; //not declared within while loop
	        /*
	        * readLine is a bit quirky :
	        * it returns the content of a line MINUS the newline.
	        * it returns null only for the END of the stream.
	        * it returns an empty String if two newlines appear in a row.
	        */
	        while (( line = input.readLine()) != null){
	          contentsBuilder.append(line);
	          contentsBuilder.append("\n");
	        }
	      }
	      finally {
	        input.close();
	      }
	    }
	    catch (IOException ex){
	      ex.printStackTrace();
	    }
	    
	    String strContent = contentsBuilder.toString();
	    String[] lineSplit = strContent.split("\n");
	    listContents = Arrays.asList(lineSplit);
	    	    		
		return listContents;
	}
	
	// recursive method to make all permutations of pool
	private static List<String> computeAllPermutations(int k, List<String> pool) {
		List<String> all = new LinkedList<String>();
		
		// base case
		if(k == 1) 
			return pool;
		
		for(int index = 0; index < pool.size(); index++) {
			// computing permutations for index
			String selected = pool.get(index);
			
			// compute everything that index can be combined with
			String[] newPool = new String[pool.size() - 1];;
			for(int j = 0; j < pool.size(); j++) {
				if(j<index) {
					newPool[j] = pool.get(j);
				}
				else if (j > index) {
					newPool[j-1] = pool.get(j);
				}
			}
			
			List<String> lists = computeAllPermutations(k-1, Arrays.asList(newPool));

			// combine index with everything it can be combined with
			for(String combination : lists) {
				String testName = selected + "," + combination;
				all.add(testName);
			}
		}
		
		return all;
	}
}
