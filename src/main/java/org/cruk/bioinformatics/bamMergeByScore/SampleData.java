package org.cruk.bioinformatics.bamMergeByScore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.SAMRecord;

/**
 * This class holds the data (file name, read scores) from a BAM file. Once a
 * file is loaded, this class can be queried to get the score of a given read,
 * or its position in the file (read number). Read number is saved so that in
 * the case of multiple hits within the file, we can select the best score (or a
 * random choice among the best scores).
 * 
 * @author Gord Brown
 *
 */
class SampleData {
  protected Path source = null;
  protected Map<String, Integer> score = new HashMap<String, Integer>();
  protected int entryCount = 0;
  protected SAMFileHeader header = null;

  /**
   * Constructor just sets the path name for this BAM file.
   * 
   * @param fn the filename to associate with this object
   */
  public SampleData(Path fn) {
    source = fn;
  }

  /**
   * Load the read names and scores for the specified BAM file. Raise exception if
   * file not found, or read error.
   * 
   * @return the number of reads loaded.
   */
  public int load() throws FileNotFoundException, IOException {
    if (!Files.exists(source)) {
      throw new FileNotFoundException("unable to find " + source.getFileName().toString());
    }
    SamReader rdr = SamReaderFactory.makeDefault().open(source);
    header = rdr.getFileHeader();
    for (SAMRecord rec : rdr) {
      String name = rec.getReadName();
      Integer alnScore = rec.getIntegerAttribute("AS");
      score.put(name, alnScore);
      entryCount++;
    }
    rdr.close();
    return entryCount;
  }

  /**
   * Return the number of records read.
   * 
   * @return number of records stored
   */
  public int size() {
    return entryCount;
  }

  /**
   * Return the alignment score of the named read.
   * 
   * @param name the read to find a score for
   * @return the score of the read, or 0 if not found
   */
  public int getScore(String name) {
    return score.getOrDefault(name, 0);
  }

  /**
   * Report whether the object has a particular read.
   * 
   * @param name the name of the read to look for
   * @return true if read found, false otherwise
   */
  public boolean hasRead(String name) {
    return score.containsKey(name);
  }

  /**
   * Return the header of this BAM file.
   * 
   * @return the header of the file
   */
  public SAMFileHeader getHeader() {
    return header;
  }
}
