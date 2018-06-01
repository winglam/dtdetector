package edu.washington.cs.dt.samples.junit4x;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Files;

public class ExampleUsesRules {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File toWrite;

    @Before
    public void setUp() throws Exception {
        toWrite = temporaryFolder.newFile("test.txt");
    }

    @Test
    public void test1() throws Exception {
        Files.write(toWrite.toPath(), "message".getBytes());
    }
}
