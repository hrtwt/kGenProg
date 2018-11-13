package jp.kusumotolab.kgenprog.project;

import static org.assertj.core.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class PatchesTest {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  @Test
  public void testWriteModifiedSourceCode() throws IOException {
    final List<String> diff = Arrays.asList("-    a", "+    b");
    final List<String> originalSourceCodeLines = Arrays.asList("a");
    final List<String> modifiedSourceCodeLines = Arrays.asList("b");
    final Patch patch = new Patch(diff, "test", originalSourceCodeLines, modifiedSourceCodeLines);
    final Patches patches = new Patches();
    patches.add(patch);

    final File folder = tempFolder.getRoot();
    final Path folderPath = folder.toPath();
    patches.writeToFile(folderPath);

    final List<String> actual = Files.readAllLines(folderPath.resolve("test.java"));

    assertThat(actual).isEqualTo(modifiedSourceCodeLines);
  }

  @Test
  public void testWriteDiff() throws IOException {
    final List<String> diff = Arrays.asList("-    a", "+    b");
    final List<String> originalSourceCodeLines = Arrays.asList("a");
    final List<String> modifiedSourceCodeLines = Arrays.asList("b");
    final Patch patch = new Patch(diff, "test", originalSourceCodeLines, modifiedSourceCodeLines);
    final Patches patches = new Patches();
    patches.add(patch);

    final File folder = tempFolder.getRoot();
    final Path folderPath = folder.toPath();
    patches.writeToFile(folderPath);

    final List<String> actual = Files.readAllLines(folderPath.resolve("test.patch"));

    assertThat(actual).isEqualTo(diff);
  }

  @Test
  public void testNotWriteFile() throws IOException {
    final List<String> diff = Arrays.asList("-    a", "+    b");
    final List<String> originalSourceCodeLines = Arrays.asList("a");
    final List<String> modifiedSourceCodeLines = Arrays.asList("b");
    final Patch patch = new Patch(diff, "test", originalSourceCodeLines, modifiedSourceCodeLines);
    final Patches patches = new Patches();
    patches.add(patch);

    final File folder = tempFolder.getRoot();
    patches.writeToLogger();

    final File[] files = folder.listFiles();
    assertThat(files.length).isEqualTo(0);
  }
}