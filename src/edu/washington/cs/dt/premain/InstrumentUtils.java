package edu.washington.cs.dt.premain;

import java.util.Arrays;
import java.util.List;

import org.objectweb.asm.tree.MethodNode;

public class InstrumentUtils {
	/**
	    * A list of non transformable prefixes for classes.
	    * */
	   public static List<String> nonTransformedPrefixes = Arrays
	      .asList(new String[] {
	    	  "java/",
		      "javax/",
	    	  "sun/",
	    	  "sunw/",
	    	  "com/sun/",
	    	  "com/oracle/",
	    	  "org/hamcrest/",
	    	  "edu/washington/cs/dt/",
	    	  "plume/",
	//          "org/junit/",
	//          "junit/",
	          "org/eclipse/jdt/internal/",
	          "org/objectweb/asm/",
	          "org/xmlpull/"
	   });
	   
	   public static String transClassNameDotToSlash(String name) {
	        return name.replace('.', '/');
	    }

	        /**
	         * Replaces the slash in the name to a dot
	         * */
	  public static String transClassNameSlashToDot(String name) {
	        return name.replace('/', '.');
	  }
	    
	  public static boolean isInit(MethodNode m) {
            return m.name.equals("<init>");
      }
	  
	  public static boolean isClinit(MethodNode m) {
		  return m.name.equals("<clinit>");
	  }
	        

	   /**
	    * Does the given class name have a non-transformable prefix?
	    * */
	   public static boolean shouldInstrumentThisClass(String className) {
	       for (String p : nonTransformedPrefixes) {
	           if (className.startsWith(p)) {
	               return false;
	           }
	       }

	   return true;
	   }

	public static boolean isSynthetic(String name) {
		return name.contains("$");
	}
	
}