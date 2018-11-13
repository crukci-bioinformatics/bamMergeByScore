package org.cruk.bioinformatics.bamMergeByScore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import htsjdk.samtools.SAMFormatException;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;

/**
 * Implements a modified trie-like data structure tailored to BAM read names.
 * Rather than a Patricia trie, this class takes advantage of the colon-separated
 * format of read names, storing reads in a tree of maps by successive name
 * components.  For example, two reads that both start with "hiseq" will be stored
 * in the same subtree.
 * 
 * @author Gord Brown
 *
 */
class SampleDataBamTrie extends SampleData {

  static class Node {
    int score = -1;
    Map<String,Node> subtree = null;
  }

  protected Node data = new Node();
  
  /**
   * Stores filename, but does nothing else.
   * 
   * @param fn filename of the BAM file to load.
   */
  public SampleDataBamTrie(Path fn) {
    super(fn);
  }
  
  private String[] split(String name) {
    return name.split(":");
  }
  
  protected void addRec(Node data, String[] names,int index,int score) {
    Node child;

    if (index < names.length) {
      if (data.subtree == null) {
        data.subtree = new HashMap<String,Node>();
        child = new Node();
        data.subtree.put(names[index], child);
      }  else {
        if (!data.subtree.containsKey(names[index])) {
          child = new Node();
          data.subtree.put(names[index], child);
        } else {
          child = data.subtree.get(names[index]);
        }
      }
      addRec(child, names, index +1, score);
    } else {
      data.score = score;
    }
  }
  
  protected int getRec(Node data, String[] names, int index) {
    if (index == names.length) {
      return data.score;
    } else {
      if (data.subtree != null && data.subtree.containsKey(names[index])) {
        return getRec(data.subtree.get(names[index]),names,index+1);
      } else {
        return -1;
      }
    }
  }
  
  protected int calcScoreFromMD(String md) {
    String[] nums = md.split("[a-zA-Z^]+");
    int total = 0;
    for (String num: nums) {
      total += Integer.parseInt(num);
    }
    return total;
  }
  
  /**
   * Find alignment score.  If "AS" attribute is not set, fake it from the CIGAR
   * string or at worst from the read length and edit distance.
   * 
   * @param rec the SAM/BAM record
   * @return a score, or 0 if no score is calculable
   */
  protected int getScore(SAMRecord rec) {
    int score = -1;
    
    Integer alnScore = rec.getIntegerAttribute("AS");
    if (alnScore != null) {
      score = alnScore.intValue();
    } else {
      String mdScore = rec.getStringAttribute("MD");
      if (mdScore != null) {
        score = calcScoreFromMD(mdScore);
      } else {
        score = -1;
      }
    }
    return score;
  }
  
  /**
   * Reads the BAM file, storing the read scores by name.  Fails if the BAM file
   * doesn't exist or can't be read, or if the htsjdk library can't understand
   * the file format, or if the thread runs out of memory.  (Since it has to
   * store all the read names, this is a non-trivial possibility for large BAM
   * files.)
   */
  public int load() throws FileNotFoundException, SAMFormatException, IOException {
    if (!Files.exists(source)) {
      throw new FileNotFoundException("unable to find " + source.getFileName().toString());
    }
    SamReaderFactory srf = SamReaderFactory.makeDefault().validationStringency(ValidationStringency.LENIENT);
    SamReader rdr = srf.open(source);
    header = rdr.getFileHeader();
    for (SAMRecord rec : rdr) {
      String name = rec.getReadName();
      int alnScore = getScore(rec);
      assert(data != null);
      assert(name != null);
      addRec(data, split(name), 0, alnScore);
      entryCount++;
    }
    rdr.close();
    return entryCount;
  }
  
  /**
   * Checks whether the read is present in the data set.
   * 
   * @return true if the read name is present, false otherwise
   */
  public boolean hasRead(String name) {
    return getRec(data,split(name),0) != -1;
  }

  /**
   * Returns the score associated with a name.
   * 
   * @return score associated with a name, or 0
   */
  public int getScore(String name) {
    return getRec(data,split(name),0);
  }
}