package edu.washington.cs.dt;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import edu.washington.cs.dt.util.Files;
import edu.washington.cs.dt.util.Utils;

public class ClusterAnalyzer {

	public final String[] files;
	
	public ClusterAnalyzer(String[] files) {
		this.files = files;
	}
	
	public ClusterAnalyzer(String dir) {
		Collection<File> allfiles = Files.listFiles(new File(dir), null, false);
		files = new String[allfiles.size()];
		int index = 0;
		for(File f : allfiles) {
			files[index] = f.getAbsolutePath();
			index++;
		}
	}
	
	public Collection<Set<String>> generateClusters() {
		Collection<Set<String>> clusters = new LinkedList<Set<String>>();
		
		Map<String, Set<String>> accessedFields = new LinkedHashMap<String, Set<String>>();
		for(String file : files) {
			Utils.checkTrue(!accessedFields.containsKey(file), "already contained? " + file);
			Set<String> content = new LinkedHashSet<String>();
			content.addAll(Files.readWholeNoExp(file));
			accessedFields.put(file, content);
		}
		
		//perform clustering
		for(String file : accessedFields.keySet()) {
			Set<String> fields = accessedFields.get(file);
			
			Set<String> matched = null;
			for(Set<String> cluster : clusters) {
				boolean intersect = false;
				//find which
				for(String f : cluster) {
					Set<String> otherFields = accessedFields.get(f);
					//if two are interecting
					if(Utils.intersect(fields, otherFields)) {
						intersect = true;
					}
				}
				if(intersect) {
					matched = cluster;
				}
				
				if(matched != null) {
					break;
				}
			}
			if(matched != null) {
				matched.add(file);
			} else {
				matched = new LinkedHashSet<String>();
				matched.add(file);
				clusters.add(matched);
			}
		}
		
		return clusters;
	}
	
	public void showBasicInfo(Collection<Set<String>> clusters) {
		int cNo = clusters.size();
		int max = Integer.MIN_VALUE;
		int min = Integer.MAX_VALUE;
		int avg = 0;
		int total = 0;
		for(Set<String> s : clusters) {
			total+=s.size();
			if(s.size() > max) {
				max = s.size();
			}
			if(s.size() < min) {
				min = s.size();
			}
		}
		avg = total / cNo;
		
		System.out.println("Number of clusters: " + cNo);
		System.out.println("  cluster max num: " + max);
		System.out.println("  cluster min num: " + min);
		System.out.println("  cluster avg num: " + avg);
		System.out.println("  cluster total num: " + total);
	}
}