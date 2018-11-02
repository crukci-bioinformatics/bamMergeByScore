package org.cruk.bioinformatics.bamMergeByScore;

import java.io.File;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Merge BAM-formatted files, keeping the highest-scoring hits. Given two or
 * more BAM files in which the same source reads have been aligned to multiple
 * references (e.g. human and mouse in a xenograft experiment, or fly and GFP in
 * a GFP knock-in experiment), this package merges the BAM files, keeping the
 * better hit. Optionally, the output can be merged into one file, or kept as
 * separate files corresponding with the incoming BAM's, but filtered to remove
 * lower-quality hits.
 * 
 * @author Gord Brown
 *
 */
public class BamMergeByScore {
  protected Logger log = LogManager.getLogger(BamMergeByScore.class);
  protected Options options = new Options();

  protected void configureOptions() {
    Option o;
    o = new Option("-o", "--output", true, "merge output into one file (named)");
    o.setType(File.class);
    options.addOption(o);
    o = new Option("-s", "--split", false, "keep output in separate files, named based on input");
    options.addOption(o);
  }

  /**
   * Parse the command line, check for at least one input file. Op
   * 
   * @param args
   *          command-line arguments to parse
   * @return zero if successful, non-zero otherwise
   */
  protected int parseCmdLine(String[] args) {
    int rc = 0;
    CommandLine cli = null;
    List<String> inputs = null;

    try {
      cli = new DefaultParser().parse(options, args);
      inputs = cli.getArgList();
      if (inputs.size() < 1) {
        log.error("Expecting at least one input argument; got {}.", inputs.size());
        rc = -1;
      }
    } catch (ParseException pe) {
      log.error("Command line parsing failed: " + pe.toString());
      rc = -1;
    }
    return rc;
  }

  /**
   * Main processing steps: parse the command line, open relevant files, process,
   * tidy up.
   * 
   * @param args
   *          command-line arguments, pre-parsing
   * @return zero if successful, non-zero othewise
   */
  protected int run(String[] args) {
    int rc = 0;
    configureOptions();
    rc = parseCmdLine(args);

    return rc;
  }

  /**
   * Main entry point to invoke a merge operation.
   * 
   * @param args
   *          the command-line arguments
   */
  public static void main(String[] args) {
    BamMergeByScore bs = new BamMergeByScore();
    int rc = bs.run(args);
    System.exit(rc);
  }

}
