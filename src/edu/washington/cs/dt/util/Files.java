/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public final class Files {
  private Files() {
    throw new IllegalStateException("no instances");
  }
  
  public static boolean createIfNotExistNoExp(String path) {
	  try {
		return createIfNotExist(new File(path));
	} catch (IOException e) {
		throw new RuntimeException(e);
	}
  }
  
  public static boolean createIfNotExist(String path) throws IOException {
	  return createIfNotExist(new File(path));
  }
  
   public static boolean createIfNotExist(File f) throws IOException {
	  if(f.exists()) {
		  return false;
	  }
	  //see the parent folder
	  File parent = f.getParentFile();
	  if(!parent.exists()) {
		  parent.mkdirs();
	  }
	  return f.createNewFile();
  }
  
  // Deletes all files and subdirectories under dir.
  // Returns true if all deletions were successful.
  // If a deletion fails, the method stops attempting to delete and returns false.
  // Attempts to detect symbolic links, and fails if it finds one.
  public static boolean deleteRecursive(File dir) {
    if (dir == null) throw new IllegalArgumentException("dir cannot be null.");
    String canonicalPath = null;
    try {
      canonicalPath = dir.getCanonicalPath();
    } catch (IOException e) {
      System.out.println("IOException while obtaining canonical file of " + dir);
      System.out.println("Will not delete file or its children.");
      return false;
    }
    if (!canonicalPath.equals(dir.getAbsolutePath())) {
      System.out.println("Warning: potential symbolic link: " + dir);
      System.out.println("Will not delete file or its children.");
      return false;
    }
    if (dir.isDirectory()) {
      String[] children = dir.list();
      for (int i=0; i<children.length; i++) {
        boolean success = deleteRecursive(new File(dir, children[i]));
        if (!success) {
          return false;
        }
      }
    }

    // The directory is now empty so delete it
    return dir.delete();
  }

  public static List<String> findFilesInDir(String dirPath, String startsWith, String endsWith) {
	  return findFilesInDir(new File(dirPath), startsWith, endsWith);
  }
  
  public static List<String> findFilesInDir(File dir, String startsWith, String endsWith) {
    if (!dir.isDirectory()) throw new IllegalArgumentException("not a directory: " + dir.getAbsolutePath());
    File currentDir = dir;
    List<String> retval = new ArrayList<String>();
    for (String fileName : currentDir.list()) {
      if ((startsWith == null || fileName.startsWith(startsWith))
    		  && (endsWith == null || fileName.endsWith(endsWith)))
        retval.add(fileName);
    }
    return retval;
  }
  
  public static Collection<File> listFiles(
			// Java4: public static Collection listFiles(
					File directory, FilenameFilter filter, boolean recurse) {
				// List of files / directories
				Vector<File> files = new Vector<File>();
				// Java4: Vector files = new Vector();

				// Get files / directories in the directory
				File[] entries = directory.listFiles();

				// Go over entries
				for (File entry : entries) {
					// Java4: for (int f = 0; f < files.length; f++) {
					// Java4: File entry = (File) files[f];

					// If there is no filter or the filter accepts the
					// file / directory, add it to the list
					if (filter == null || filter.accept(directory, entry.getName())) {
						files.add(entry);
					}

					// If the file is a directory and the recurse flag
					// is set, recurse into the directory
					if (recurse && entry.isDirectory()) {
						files.addAll(listFiles(entry, filter, recurse));
					}
				}

				// Return collection of files
				return files;
			}

  public static void writeToFileWithNoExp(Collection<String> list, String fileName) {
	  StringBuilder sb = new StringBuilder();
	  for(String t : list) {
		  sb.append(t);
		  sb.append(Globals.lineSep);
	  }
	  try {
		writeToFile(sb.toString(), fileName);
	} catch (IOException e) {
		throw new RuntimeException(e);
	}
  }
  
  public static void writeToFile(String s, File file) throws IOException {
    writeToFile(s, file, false);
  }

  public static void writeToFile(String s, String fileName) throws IOException {
    writeToFile(s, fileName, false);
  }

  public static void writeToFile(String s, File file, Boolean append) throws IOException {
    BufferedWriter writer= new BufferedWriter(new FileWriter(file, append));
    try{
      writer.append(s);
    } finally {
      writer.close();
    }        
  }

  public static void writeToFile(String s, String fileName, Boolean append) throws IOException {
    writeToFile(s, new File(fileName));
  }

  /**
   * Reads the whole file. Does not close the reader.
   * Returns the list of lines.  
   */
  public static List<String> readWhole(BufferedReader reader) throws IOException {
    List<String> result= new ArrayList<String>();
    String line= reader.readLine();
    while(line != null) {
      result.add(line);
      line= reader.readLine();
    }
    return Collections.unmodifiableList(result);
  }

  /**
   * Reads the whole file. Returns the list of lines.  
   */
  public static List<String> readWhole(String fileName) throws IOException {
    return readWhole(new File(fileName));
  }
  
  /**
   * Reads the whole file. Returns the list of lines.  
   */
  public static List<String> readWholeNoExp(String fileName) {
	  try {
         return readWhole(new File(fileName));
	  }  catch (IOException e) {
		  throw new RuntimeException(e);
	  }
  }

  /**
   * Reads the whole file. Returns the list of lines.  
   */
  public static List<String> readWhole(File file) throws IOException {
    BufferedReader in = new BufferedReader(new FileReader(file));
    try{
      return readWhole(in);
    } finally{
      in.close();
    }
  }    

  /**
   * Reads the whole file. Returns the list of lines.
   * Does not close the stream.  
   */
  public static List<String> readWhole(InputStream is) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(is));
    return readWhole(in);
  }

  /**
   * Reads the whole file. Returns one big String.
   */
  public static String getFileContents(File file) throws IOException {
    StringBuilder result = new StringBuilder();    
    Reader in = new BufferedReader(new FileReader(file));
    try{
      int c;
      while ((c = in.read()) != -1)
      {
        result.append((char)c);
      }
      in.close();
      return result.toString();
    } finally{
      in.close();
    }
  }

  /**
   * Reads the whole file. Returns one big String.
   */
  public static String getFileContents(String path) throws IOException {
    return getFileContents(new File(path));
  }

  public static LineNumberReader getFileReader(String fileName) {
    return getFileReader(new File(fileName));
  }

  public static LineNumberReader getFileReader(File fileName) {
    LineNumberReader reader;
    try {
      reader = new LineNumberReader(new BufferedReader(
          new FileReader(fileName)));
    } catch (FileNotFoundException e1) {
      throw new IllegalStateException("File was not found " + fileName + " " + e1.getMessage());
    }
    return reader;
  }
  public static String addProjectPath(String string) {  
    return System.getProperty("user.dir") + File.separator + string;
  }

  public static boolean deleteFile(String path) {
    File f = new File(path);
    return f.delete();
  }

  /**
   * Reads a single long from the file.
   * Returns null if the file does not exist.
   * @throws  IllegalStateException is the file contains not just 1 line or
   *          if the file contains something.
   */
  public static Long readLongFromFile(File file) {
    if (! file.exists())
      return null;
    List<String> lines;
    try {
      lines = readWhole(file);
    } catch (IOException e) {
      throw new IllegalStateException("Problem reading file " + file + " ", e);
    }
    if (lines.size() != 1)
      throw new IllegalStateException("Expected exactly 1 line in " + file + " but found " + lines.size());
    try{
      return Long.valueOf(lines.get(0));
    } catch (NumberFormatException e) {
      throw new IllegalStateException("Expected a number (type long) in " + file + " but found " + lines.get(0));
    }
  }

  /**
   * Prints out the contents of the give file to stdout.
   */
  public static void cat(String filename) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      String line = reader.readLine();
      while (line != null) {
        System.out.println(line);
        line = reader.readLine();
      }
      reader.close();
    } catch (Exception e) {
      throw new Error(e);
    }
  }

  /**
   * Returns the number of lines in the given file.
   */
  public static int countLines(String filename) {
    int lines = 0;
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      String line = reader.readLine();
      while (line != null) {
        lines++;
        line = reader.readLine();
      }
      reader.close();
    } catch (Exception e) {
      throw new Error(e);
    }
    return lines;
  }
}
