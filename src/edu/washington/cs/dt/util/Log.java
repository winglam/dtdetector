/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.util;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

public final class Log {

  public static FileWriter log = null;
  
  public static final String lineSep = System.getProperty("line.separator");

  private Log() {
    throw new IllegalStateException("can not be initialized");
  }

  public static final ByteArrayOutputStream bos;
  public static final PrintStream systemOutErrStream;
  public static final PrintStream err;
  public static final PrintStream out;

  static {
    bos = new ByteArrayOutputStream();
    systemOutErrStream = new PrintStream(bos);
    err = System.err;
    out = System.out;
  }
  
  public static void logConfig(String logFile) {
    try {
      Files.createIfNotExist(logFile);
      Log.log = new FileWriter((logFile));
  } catch (IOException e) {
      e.printStackTrace();
  }
  }
  
  public static void logln(String s, boolean flag) {
	  if(flag) {
		  logln(s);
	  }
  }
  
  public static void logln(String s) {
	  if (! isLoggingOn()) return;

	    try {
	      log.write(s);
	      log.write(Log.lineSep);
	      log.flush();
	    } catch (IOException e) {
	      e.printStackTrace();
	      System.exit(1);
	    }
  }
  
  public static void log(String s) {
	  if (! isLoggingOn()) return;

	    try {
	      log.write(s);
	      log.flush();
	    } catch (IOException e) {
	      e.printStackTrace();
	      System.exit(1);
	    }
  }

  public static void logSeparator() {
	  logln("----------------------");
  }

  public static boolean isLoggingOn() {
    return log != null;
  }
}