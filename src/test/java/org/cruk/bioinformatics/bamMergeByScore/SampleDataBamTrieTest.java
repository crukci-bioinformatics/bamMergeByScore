package org.cruk.bioinformatics.bamMergeByScore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import htsjdk.samtools.SAMFormatException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class SampleDataBamTrieTest {
  
  protected String sampleData = "src/test/testData/sampleData.bam";
  protected String badData = "src/test/testData/notABamFile.bam";

  @Test
  public void testSanity() {
    SampleDataBamTrie t = new SampleDataBamTrie(Paths.get("nonsenseFilename.bam"));
    assertEquals(false,t.hasRead("Zork"));
  }
  
  @Test
  public void testAddRecBasic() {
    String[] sampleName = {"alpha"};
    SampleDataBamTrie t = new SampleDataBamTrie(Paths.get("nonsenseFilename.bam"));
    t.addRec(t.data, sampleName, 0, 99);
    assertTrue(t.hasRead("alpha"));
    assertEquals(99,t.getScore("alpha"));
  }
  
  @Test
  public void testLoadNoFile() {
    SampleDataBamTrie t = new SampleDataBamTrie(Paths.get("nonsenseFilename.bam"));
    boolean okay = false;
    try {
      t.load();
      fail("FileNotFound not thrown!");
    } catch (FileNotFoundException fnfe) {
      okay = true;
    } catch (IOException ioe) {
      fail("Caught IOException, not FileNotFoundException");
    }
    assertTrue(okay);
  }

  @Test
  public void testLoadBadFile() {
    Path p = Paths.get(badData);
    SampleDataBamTrie t = new SampleDataBamTrie(p);
    boolean okay = false;
    try {
      t.load();
      fail("Expected SAMFormatException");
    } catch (FileNotFoundException fnfe) {
      fail("Caught FileNotFoundException");
    } catch (IOException ioe) {
      fail("Caught IOException");
    } catch (SAMFormatException sfe) {
      okay = true;
    }
    assertTrue(okay);
  }

  @Test
  public void testLoadGoodFile() {
    Path p = Paths.get(sampleData);
    SampleDataBamTrie t = new SampleDataBamTrie(p);
    boolean okay = false;
    try {
      t.load();
      okay = true;
    } catch (FileNotFoundException fnfe) {
      fail("Caught FileNotFoundException");
    } catch (IOException ioe) {
      fail("Caught IOException");
    } catch (SAMFormatException sfe) {
      fail("Caught SAMFormatException");
    }
    assertTrue(okay);
  }
  
  @Test
  public void testFindScore() {
    Path p = Paths.get(sampleData);
    SampleDataBamTrie t = new SampleDataBamTrie(p);
    try {
      t.load();
    } catch (FileNotFoundException fnfe) {
      fail("Caught FileNotFoundException");
    } catch (IOException ioe) {
      fail("Caught IOException");
    } catch (SAMFormatException sfe) {
      fail("Caught SAMFormatException");
    }
    assertEquals(20,t.getScore("K00252:335:HWMMGBBXX:2:1101:3204:2545"));
    assertEquals(13,t.getScore("K00252:335:HWMMGBBXX:2:1101:21481:2527"));
    assertEquals(-1,t.getScore("this:is:not:a:legal:score"));
    assertEquals(-1,t.getScore("K00252:335:HWMMGBBXX:2:1101:21481:2527:Zounds"));
    assertEquals(-1,t.getScore("K00252:335:HWMMGBBXX:2:1101:21481"));
  }
}