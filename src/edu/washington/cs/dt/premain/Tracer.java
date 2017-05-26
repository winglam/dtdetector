package edu.washington.cs.dt.premain;

import java.util.Collection;
import java.util.LinkedList;

public class Tracer {
	
	public static Collection<String> accessedFields = new LinkedList<String>();
	
	public static void traceField(String field) {
		accessedFields.add(field);
	}
	
	public static Collection<String> getAccessedFields() {
		return accessedFields;
	}

}
