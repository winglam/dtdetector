package edu.washington.cs.dt.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipException;

import edu.washington.cs.dt.util.JarViewer;
import edu.washington.cs.dt.util.Log;
import edu.washington.cs.dt.util.Utils;

public class PublicClassFinder {

	List<String> getAllNonInnerPublicClassesFromJar(File jarFile) throws ZipException, IOException, ClassNotFoundException {
		Log.logln("Looking classes in: " + jarFile);
		Collection<String> contents = JarViewer.getContentsAsStr(jarFile);
		List<String> classes = new LinkedList<String>();
		for(String content : contents) {
			if(content.endsWith(".class")) {
				Log.logln("processing class: " + content);
				String clzName = content.replace("/", ".").substring(0, content.indexOf(".class"));
//				System.out.println(clzName);
				try {
					Class<?> clz = Utils.loadclass(jarFile.getAbsolutePath(), clzName);
				   if(Modifier.isPublic(clz.getModifiers()) && clzName.indexOf("$") == -1) {
					   classes.add(clz.getName());
				   }
				} catch (Throwable e) {
					Log.logln("ERROR in reflectively load class: " + clzName);
					Log.logln("    An exception: " + e + " is thrown");
					e.printStackTrace();
				}
			}
		}
		return classes;
	}
	
	public static void main(String[] args) throws ZipException, ClassNotFoundException, IOException {
		String jarName = "/Users/winglam/GoogleDrive/research/dt-impact/tools/randoop/experiment-inputs-outputs/xml-security-without-tests.jar";
		PublicClassFinder finder = new PublicClassFinder();
		List<String> clzList = finder.getAllNonInnerPublicClassesFromJar(new File(jarName));
		System.out.println(clzList.size());
		for(String c : clzList) {
			System.out.println(c);
		}
	}
	
}