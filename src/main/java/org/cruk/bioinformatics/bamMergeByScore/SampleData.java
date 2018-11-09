package org.cruk.bioinformatics.bamMergeByScore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import htsjdk.samtools.SAMFileHeader;

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
abstract class SampleData {
  protected Path source = null;
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
   * file not found, or read error. Should be instantiated (Map, Trie, etc) by
   * subclasses.
   * 
   * @return the number of reads loaded.
   */
  abstract int load() throws FileNotFoundException, IOException;

  /**
   * Return the number of records read.
   * 
   * @return number of records stored
   */
  int size() {
    return entryCount;
  }

  /**
   * Return the amount of memory used by the object.
   * 
   * @return memory used
   */
  /*
   * abstract long memory();
   */

  /**
   * Return the alignment score of the named read.
   * 
   * @param name the read to find a score for
   * @return the score of the read, or 0 if not found
   */
  abstract int getScore(String name);

  /**
   * Report whether the object has a particular read.
   * 
   * @param name the name of the read to look for
   * @return true if read found, false otherwise
   */
  abstract boolean hasRead(String name);

  /**
   * Return the header of this BAM file.
   * 
   * @return the header of the file
   */
  public SAMFileHeader getHeader() {
    return header;
  }
}