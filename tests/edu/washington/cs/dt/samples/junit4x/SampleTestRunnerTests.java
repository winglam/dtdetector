package edu.washington.cs.dt.samples.junit4x;

import edu.washington.cs.dt.samples.SampleTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SampleTestRunner.class)
public class SampleTestRunnerTests {
    @Test
    public void test1() {
        Assert.assertTrue(SampleTestRunner.thisMustBeTrueForTests);
    }
}
