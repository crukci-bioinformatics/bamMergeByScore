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

import org.github.jamm.MemoryMeter;
import org.junit.Test;

public class SampleDataTest {
  protected String sampleData = "src/test/testData/sampleData.bam";
  protected String bigData = "src/test/testData/testBigBam.bam";
  protected String biggerData = "src/test/testData/testBiggerBam.bam";
  protected String reallyBigData = "../bamMerge_testData/testBigBam.bam";

  public static String friendly(long bytes) {
    int unit = 1024;
    if (bytes < unit) return bytes + " B";
    int exp = (int) (Math.log(bytes) / Math.log(unit));
    String pre = "KMGTPE".charAt(exp-1) + "i";
    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
  }

  @Test
  public void testSanity() {
    Path dataP = Paths.get(sampleData);
    SampleData data = new SampleDataMap(dataP);
    try {
      MemoryMeter mm = new MemoryMeter();
      data.load();
      long m = mm.measure(data);
      long md = mm.measureDeep(data);
      long c = mm.countChildren(data);
      System.out.println("smallMap m: "+friendly(m)+", md: "+friendly(md)+" c: "+friendly(c));
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
    SampleData data = new SampleDataMap(dataP);
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
    SampleData data = new SampleDataMap(dataP);
    try {
      data.load();
    } catch (FileNotFoundException fne) {
      fail("File not found exception: " + dataP.toString());
    } catch (IOException io) {
      fail("IO Exception: " + dataP.toString());
    }
    assertEquals(23, data.getScore(readName));
  }

  @Test
  public void testTrie() {
    Path dataP = Paths.get(sampleData);
    SampleData data = new SampleDataTrie(dataP);
    try {
      MemoryMeter mm = new MemoryMeter();
      data.load();
      long m = mm.measure(data);
      long md = mm.measureDeep(data);
      long c = mm.countChildren(data);
      System.out.println("smallTrie m: "+friendly(m)+", md: "+friendly(md)+" c: "+friendly(c));
      
    } catch (FileNotFoundException fne) {
      fail("File not found exception: " + dataP.toString());
    } catch (IOException io) {
      fail("IO Exception: " + dataP.toString());
    }
    assertEquals(1000, data.size());
  }

  @Test
  public void testBigBam() {
    Path dataP = Paths.get(bigData);
    SampleData data = new SampleDataMap(dataP);
    try {
      MemoryMeter mm = new MemoryMeter();
      data.load();
      long m = mm.measure(data);
      long md = mm.measureDeep(data);
      long c = mm.countChildren(data);
      System.out.println("bigMap m: "+friendly(m)+", md: "+friendly(md)+" c: "+friendly(c));
    } catch (FileNotFoundException fne) {
      fail("File not found exception: " + dataP.toString());
    } catch (IOException io) {
      fail("IO Exception: " + dataP.toString());
    }
    assertEquals(99928, data.size());
  }

  @Test
  public void testBigTrie() {
    Path dataP = Paths.get(bigData);
    SampleData data = new SampleDataTrie(dataP);
    try {
      MemoryMeter mm = new MemoryMeter();
      data.load();
      long m = mm.measure(data);
      long md = mm.measureDeep(data);
      long c = mm.countChildren(data);
      System.out.println("bigTrie m: "+friendly(m)+", md: "+friendly(md)+" c: "+friendly(c));
    } catch (FileNotFoundException fne) {
      fail("File not found exception: " + dataP.toString());
    } catch (IOException io) {
      fail("IO Exception: " + dataP.toString());
    }
    assertEquals(99928, data.size());
  }

  @Test
  public void testBiggerBam() {
    Path dataP = Paths.get(biggerData);
    SampleData data = new SampleDataMap(dataP);
    try {
      MemoryMeter mm = new MemoryMeter();
      data.load();
      long m = mm.measure(data);
      long md = mm.measureDeep(data);
      long c = mm.countChildren(data);
      System.out.println("biggerMap m: "+friendly(m)+", md: "+friendly(md)+" c: "+friendly(c));
    } catch (FileNotFoundException fne) {
      fail("File not found exception: " + dataP.toString());
    } catch (IOException io) {
      fail("IO Exception: " + dataP.toString());
    }
    assertEquals(400274, data.size());
  }

  @Test
  public void testBiggerTrie() {
    Path dataP = Paths.get(biggerData);
    SampleData data = new SampleDataTrie(dataP);
    try {
      MemoryMeter mm = new MemoryMeter();
      data.load();
      long m = mm.measure(data);
      long md = mm.measureDeep(data);
      long c = mm.countChildren(data);
      System.out.println("biggerTrie m: "+friendly(m)+", md: "+friendly(md)+" c: "+friendly(c));
    } catch (FileNotFoundException fne) {
      fail("File not found exception: " + dataP.toString());
    } catch (IOException io) {
      fail("IO Exception: " + dataP.toString());
    }
    assertEquals(400274, data.size());
  }

  /*
   * @Test public void testBigMemory() { Path dataP = Paths.get(bigData);
   * SampleData data = new SampleDataMap(dataP); try { data.load(); } catch
   * (FileNotFoundException fne) { fail("File not found exception: " +
   * dataP.toString()); } catch (IOException io) { fail("IO Exception: " +
   * dataP.toString()); } long m = data.memory();
   * System.out.println("object memory: " + m); assertEquals(99928, data.size());
   * }
   */
}
