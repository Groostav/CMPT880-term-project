package randoop.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import plume.UtilMDE;

/**
 * Reads a list of records from a text file, where a record is partially
 * specified by the client of this class.
 * <p>
 *
 * A record is a sequence of lines, where the first line is the string
 * "START &lt;recordtype&gt;" and the last line is the string
 * "END &lt;recordtype&gt;" where &lt;recordtype&gt; is specified by the client.
 * For example:
 *
 * <pre>START person
 * <em>... arbitrary text ...</em>
 * <em>... more arbitrary text ...</em>
 * END person</pre>
 *
 * Any lines within and between records, that are only whitespace or start with
 * "#", are skipped.
 * <p>
 *
 * This class's built-in functionality extracts records from a file.
 * It then parses and processes each record, using the RecordProcessor
 * provided by the client.
 * .
 */
public class RecordListReader {

  // startMarker is "START <recordType>"
  // endMarker is "END <recordType>"
  private final String startMarker, endMarker;

  // The object in charge of doing whatever is to be done with the record.
  private final RecordProcessor processor;

  public RecordListReader(String recordType, RecordProcessor proc) {
    if (recordType == null || recordType.length() == 0)
      throw new IllegalArgumentException("Invalid record type:" + recordType);
    if (proc == null) throw new IllegalArgumentException("proc cannot be null.");
    this.processor = proc;
    this.startMarker = "START " + recordType;
    this.endMarker = "END " + recordType;
  }

  public void parse(String inFile) {
    if (inFile == null || inFile.length() == 0)
      throw new IllegalArgumentException("Illegal input file name: " + inFile);

    BufferedReader reader;
    try {
      reader = UtilMDE.bufferedFileReader(inFile);
    } catch (IOException e) {
      throw new Error(e);
    }

    parse(reader);
  }

  public void parse(File inFile) {
    if (inFile == null) throw new IllegalArgumentException("Null input file");

    BufferedReader reader;
    try {
      reader = UtilMDE.bufferedFileReader(inFile);
    } catch (IOException e) {
      throw new Error(e);
    }

    parse(reader);
  }

  public void parse(BufferedReader reader) {

    String line;
    try {
      line = nextNWCLine(reader);
      while (line != null) {
        line = line.trim();
        if (line.startsWith(startMarker)) {
          List<String> oneRecord = readOneRecord(reader);
          processor.processRecord(oneRecord);
        } else {
          throw new IllegalArgumentException("Expected \"" + startMarker + "\" but got " + line);
        }
        line = nextNWCLine(reader);
      }
    } catch (IOException e) {
      throw new Error(e);
    }
  }

  private List<String> readOneRecord(BufferedReader reader) throws IOException {
    List<String> ret = new ArrayList<String>();
    String line = nextNWCLine(reader);
    while (line != null && !line.equals(endMarker)) {
      if (line.length() == 0 || line.charAt(0) == '#') continue;
      ret.add(line);
      line = nextNWCLine(reader);
    }
    return ret;
  }

  private static String nextNWCLine(BufferedReader reader) throws IOException {
    String line = reader.readLine();
    if (line != null) line = line.trim();
    while (line != null && (line.length() == 0 || line.indexOf('#') == 0)) {
      line = reader.readLine();
      if (line != null) line = line.trim();
    }
    return line;
  }
}
