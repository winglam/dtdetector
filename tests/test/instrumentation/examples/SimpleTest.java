package test.instrumentation.examples;

import junit.framework.TestCase;

public class SimpleTest extends TestCase {

	public static Object o = null;
	
	public void test1() {
		o = "hello";
		System.out.println(o.toString());
	}
	
	public void test2() {
		o.hashCode();
	}
	
	public void test3() {
		Inner.accessX();
	}
}

class Inner {
	//seems JVM has special treatment for object o
	public static final Object o = "hello";
	
	public static void accessX() {
		o.hashCode();
	}
}
