package org.cruk.bioinformatics.bamMergeByScore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
//import java.util.HashMap;
//import java.util.Map;

import org.apache.commons.collections4.trie.PatriciaTrie;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;

/**
 * This class holds the data (file name, read scores) from a BAM file. Once a
 * file is loaded, this class can be queried to get the score of a given read,
 * or its position in the file (read number). Read number is saved so that in
 * the case of multiple hits within the file, we can select the best score (or a
 * random choice among the best scores).
 * 
 * NOTA BENE: This data structure does not outperform a built-in Java HashMap.
 * There is no benefit to using it. (But I had to try it, to find out!)
 * 
 * @author Gord Brown
 *
 */
class SampleDataTrie extends SampleData {

  protected PatriciaTrie<Integer> score = new PatriciaTrie<Integer>();

  /**
   * Constructor just sets the path name for this BAM file.
   * 
   * @param fn the filename to associate with this object
   */
  public SampleDataTrie(Path fn) {
    super(fn);
  }

  /**
   * Load the read names and scores for the specified BAM file. Raise exception if
   * file not found, or read error.
   * 
   * @return the number of reads loaded.
   */
  int load() throws FileNotFoundException, IOException {
    if (!Files.exists(source)) {
      throw new FileNotFoundException("unable to find " + source.getFileName().toString());
    }
    SamReaderFactory srf = SamReaderFactory.makeDefault().validationStringency(ValidationStringency.LENIENT);
    SamReader rdr = srf.open(source);
    header = rdr.getFileHeader();
    for (SAMRecord rec: rdr) {
      String name = rec.getReadName();
      Integer alnScore = rec.getIntegerAttribute("AS");
      score.put(name, alnScore);
      entryCount++;
    }
    rdr.close();
    return entryCount;
  }

  // long memory() {
  // long total = 0;
  // total += instrumentation.getObjectSize(score);
  // for (Map.Entry<String, Integer> entry : score.entrySet()) {
  // total += instrumentation.getObjectSize(entry.getKey());
  // total += instrumentation.getObjectSize(entry.getValue());
  // }
  // return total;
  // }

  /**
   * Return the alignment score of the named read.
   * 
   * @param name the read to find a score for
   * @return the score of the read, or 0 if not found
   */
  int getScore(String name) {
    return score.getOrDefault(name, 0);
  }

  /**
   * Report whether the object has a particular read.
   * 
   * @param name the name of the read to look for
   * @return true if read found, false otherwise
   */
  boolean hasRead(String name) {
    return score.containsKey(name);
  }

  // public static void premain(String args, Instrumentation inst) {
  // instrumentation = inst;
  // }
}