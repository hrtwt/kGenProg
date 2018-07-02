package jp.kusumotolab.kgenprog.project;

import java.nio.file.Path;
import java.util.List;

public class DefaultProjectFactory implements IProjectFactory {
  private final Path rootPath;
  private final List<SourceFile> sourceFiles;
  private final List<SourceFile> testFiles;
  private final List<ClassPath> classPaths;

  public DefaultProjectFactory(final Path rootPath, final List<SourceFile> sourceFiles,
      final List<SourceFile> testFiles, List<ClassPath> classPaths) {
    this.rootPath = rootPath;
    this.sourceFiles = sourceFiles;
    this.testFiles = testFiles;
    this.classPaths = classPaths;
  }

  @Override
  public TargetProject create() {
    return new TargetProject(rootPath, sourceFiles, testFiles, classPaths);
  }

  @Override
  public boolean isApplicable() {
    return true;
  }

}
