package jp.kusumotolab.kgenprog.output;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Patches {

  private static Logger log = LoggerFactory.getLogger(Patches.class);

  private final List<FileDiff> diffs = new ArrayList<>();

  public void add(final FileDiff diff) {
    this.diffs.add(diff);
  }

  public FileDiff get(final int index) {
    return diffs.get(index);
  }

  public void writeToFile(final Path outDir) {
    try {
      if (Files.notExists(outDir)) {
        Files.createDirectories(outDir);
      }
    } catch (final IOException e) {
      log.error(e.getMessage());
    }

    for (final FileDiff fileDiff : diffs) {
      fileDiff.write(outDir);
    }
  }

  public void writeToLogger() {
    for (final FileDiff fileDiff : diffs) {
      log.info(System.lineSeparator() + fileDiff.getDiff());
    }
  }
}
