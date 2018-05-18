package edu.washington.cs.dt.samples.junit4x;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ExampleBeforeClassTests {
    public static final List<Integer> xs = new ArrayList<>();

    @BeforeClass
    public static void beforeClass() {
        xs.add(1);
    }

    @Test
    public void testXsHasOneItemAndAddOne() {
        Assert.assertEquals(1, xs.size());
        xs.add(2);
    }

    @Test
    public void testXsHasTwoItems() {
        Assert.assertEquals(2, xs.size());
    }
}
