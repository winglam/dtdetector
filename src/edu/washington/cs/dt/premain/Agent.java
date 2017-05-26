package edu.washington.cs.dt.premain;

import java.lang.instrument.Instrumentation;

import edu.washington.cs.dt.util.Files;
import edu.washington.cs.dt.util.Globals;

public class Agent {
	
	public static String fileDir = "." + Globals.fileSep + "output";
	
	public static void premain(String agentArgs, Instrumentation inst) {
		//final StaticFieldAccessInstrumenter instrumenter = new StaticFieldAccessInstrumenter();
	    //inst.addTransformer(instrumenter);
		final Instrumenter instrumenter = new Instrumenter();
	    inst.addTransformer(instrumenter);

	    
	    String writeFileName = agentArgs != null ? agentArgs : null;
	    final String writeFileFullPath = fileDir + Globals.fileSep + writeFileName + ".txt";
	    
	    //add the shut down hook
	    Runtime.getRuntime().addShutdownHook(new Thread() {
	                @Override
	                public void run() {
	                       if(writeFileFullPath != null) {
	                    	   Files.createIfNotExistNoExp(writeFileFullPath);
	                    	   Files.writeToFileWithNoExp(Tracer.getAccessedFields(), writeFileFullPath);
	                       }
	                }
	        });
	}
}
