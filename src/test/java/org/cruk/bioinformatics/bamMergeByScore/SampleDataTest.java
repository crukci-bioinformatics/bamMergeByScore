package org.cruk.bioinformatics.bamMergeByScore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

//import org.hamcrest.CoreMatchers;
import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class SampleDataTest {
  protected String sampleData = "src/test/testData/sampleData.bam";

  @Test
  public void testSanity() {
    Path dataP = Paths.get(sampleData);
    SampleData data = new SampleData(dataP);
    try {
      data.load();
    } catch (FileNotFoundException fne) {
      fail("File not found exception: " + dataP.toString());
    } catch (IOException io) {
      fail("IO Exception: " + dataP.toString());
    }
    assertEquals(1000, data.size());
  }

  @Test
  public void testHasRead() {
    Path dataP = Paths.get(sampleData);
    String readName = "K00252:335:HWMMGBBXX:2:1101:3204:2545";
    SampleData data = new SampleData(dataP);
    try {
      data.load();
    } catch (FileNotFoundException fne) {
      fail("File not found exception: " + dataP.toString());
    } catch (IOException io) {
      fail("IO Exception: " + dataP.toString());
    }
    assertTrue(data.hasRead(readName));
  }

  @Test
  public void testReadScore() {
    Path dataP = Paths.get(sampleData);
    String readName = "K00252:335:HWMMGBBXX:2:1101:23815:2527";
    SampleData data = new SampleData(dataP);
    try {
      data.load();
    } catch (FileNotFoundException fne) {
      fail("File not found exception: " + dataP.toString());
    } catch (IOException io) {
      fail("IO Exception: " + dataP.toString());
    }
    assertEquals(23, data.getScore(readName));
  }

}
