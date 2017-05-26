package edu.washington.cs.dt.premain;

import edu.washington.cs.dt.tools.TestAccessingFieldsCollector;
import junit.framework.TestCase;

public class TestRunningInstrumentedJUnits extends TestCase {

	public void test1() {
		String[] args = new String[]{
				"--testFile", "./tests/edu/washington/cs/dt/premain/sampletests.txt"};
		TestAccessingFieldsCollector.main(args);
	}
	
}
