package org.cruk.bioinformatics.bamMergeByScore;

import java.util.List;

import org.hamcrest.CoreMatchers;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.junit.LoggerContextRule;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

public class CommandParsingTest {
  public static final String CONFIG = "log4j2-test.xml";
  BamMergeByScore merger = null;
  private ListAppender listAppender;
  private Logger savedLogger;

  @ClassRule
  public static LoggerContextRule context = new LoggerContextRule(CONFIG);

  @Before
  public void setUpCli() {
    merger = new BamMergeByScore();
    savedLogger = merger.log;
    listAppender = (ListAppender) context.getRequiredAppender("List");
    listAppender.clear();
    merger.log = context.getLogger();
    merger.configureOptions();
  }

  @After
  public void replaceLogger() {
    merger.log = savedLogger;
  }

  @Test
  public void testSanity() {
    assertTrue(true);
  }

  @Test
  public void testBasicCommand() {
    String[] args = { "--output", "zork.bam", "alpha", "bravo" };
    int rc = merger.parseCmdLine(args);
    assertEquals(0, rc);
    assertEquals(2, merger.inputs.size());
    assertEquals("zork.bam", merger.cli.getOptionValue("output"));
  }

  @Test
  public void testLogging() {
    String[] args = { "--output", "zork.bam", "--split", "alpha", "bravo" };
    int rc = merger.parseCmdLine(args);
    List<String> events = listAppender.getMessages();
    assertEquals(-1, rc);
    assertEquals(1, events.size());
    assertThat(events.get(0), CoreMatchers.containsString("Command line parsing failed"));
  }
}
