/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Utils {
	
	public static String listToStr(List<String> list) {
		StringBuilder sb = new StringBuilder();
		for(String s : list) {
			sb.append(s);
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public static void checkTrue(boolean condition, String message) {
		if(!condition) {
			Log.logln("Violating condition: ");
			Log.logln(message);
			Log.log(Globals.lineSep);
			throw new RuntimeException(message);
		}
	}
	
	public static void checkNull(Object o, String message) {
		if(o == null) {
			throw new RuntimeException(message);
		}
	}
	
	public static boolean VERBOSE = true;
	public static void stdln(String str) {
		if(!VERBOSE)
			return;
		System.out.println(str);
	}
	
	public static void std(String str) {
		if(!VERBOSE)
			return;
		System.out.print(str);
	}
	
	public static void stdln(Object obj) {
		if(!VERBOSE)
			return;
		System.out.println(obj);
	}
	
	public static void std(Object obj) {
		if(!VERBOSE)
			return;
		System.out.print(obj);
	}
	
	public static void flushToStd(String[] msgs) {
	    for(String msg : msgs) {
	      System.out.println(msg);
	    }
	}
	
	/**
	   * Copy stream contents.
	   * */
	  public static int copyStream(InputStream in, OutputStream out)
	          throws IOException {
	      int size = 0;
	      // Transfer bytes from in to out
	      byte[] buf = new byte[1024];
	      int len;

	      while ((len = in.read(buf)) > 0) {
	          size += len;
	          out.write(buf, 0, len);
	      }

	      return size;
	  }
	  
	  /**
	     * Copy file contents.
	     * */
	    public static int copyFile(File inFile, File outFile) throws IOException {
	      FileInputStream in = new FileInputStream(inFile);
	      FileOutputStream out = new FileOutputStream(outFile);
	      int size = copyStream(in, out);
	      in.close();
	      out.close();

	      return size;
	  }
	
	public static<T> boolean intersect(Collection<T> t1, Collection<T> t2) {
		for(T t : t1) {
			if(t2.contains(t)) {
				return true;
			}
		}
		return false;
	}
	    
	public static<T> List<T> createList(T...ints) {
		List<T> list = new LinkedList<T>();
		for(T i : ints) {
			list.add(i);
		}
		return list;
	}
	
	public static<T> List<T> randomList(Collection<T> coll) {
		List<T> randomizedList = new ArrayList<T>();
		randomizedList.addAll(coll);
		//do randomization
		Collections.shuffle(randomizedList, new Random(System.currentTimeMillis()));
		
		checkTrue(randomizedList.size() == coll.size(), "");
		
		return randomizedList;
	}
	
	public static String convertArrayToFlatString(Object array) {
	      checkNull(array, "The array object could not be null");
	      checkTrue(array.getClass().isArray(), "The give object: " + array  + "is not an array type.");
	      StringBuilder sb = new StringBuilder();
	      
	      int length = Array.getLength(array);
	      sb.append("[");
	      for(int i = 0; i < length; i++) {
	        Object obj = Array.get(array, i);
	        if(obj == null) {
	          sb.append("null");
	        } else {
	          sb.append(obj);
	        }
	        if(i != length - 1) {
	          sb.append(", ");
	        }
	      }
	      sb.append("]");
	      
	      return sb.toString();
	    }
	
	
	public static Class<?> loadclass(String classPath, String  className) {
		// Create a File object on the root of the directory containing the class file
		String[] paths = classPath.split(Globals.pathSep);
		File[] files = new File[paths.length];
		for(int i = 0; i < paths.length; i++) {
			files[i] = new File(paths[i]);
		}

		try {
			// Convert File to a URL
			URL[] urls = new URL[files.length];
			for(int i = 0; i < files.length; i++) {
				urls[i] = files[i].toURL();
			}

			// Create a new class loader with the directory
			ClassLoader cl = new URLClassLoader(urls);

			// Load in the class; MyClass.class should be located in
			// the directory file:/c:/myclasses/com/mycompany
			Class<?> cls = cl.loadClass(className);
			return cls;
		} catch (MalformedURLException e) {
		} catch (ClassNotFoundException e) {
		}
		return null;
	}
}