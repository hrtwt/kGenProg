package jp.kusumotolab.kgenprog.project;

import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAR;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAR_TEST;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAZ;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAZ_ANONYMOUS;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAZ_INNER;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAZ_OUTER;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAZ_STATIC_INNER;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAZ_TEST;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FOO;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FOO_TEST;
import static org.assertj.core.api.Assertions.assertThat;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.build.BinaryStore;
import jp.kusumotolab.kgenprog.project.build.BuildResults;
import jp.kusumotolab.kgenprog.project.build.EmptyBuildResults;
import jp.kusumotolab.kgenprog.project.build.JavaBinaryObject;
import jp.kusumotolab.kgenprog.project.build.ProjectBuilder;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.test.MemoryClassLoader;
import jp.kusumotolab.kgenprog.project.test.TargetFullyQualifiedName;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class ProjectBuilderTest {

  @Test
  public void testBuildStringForBuildFailure01() {
    final Path rootPath = Paths.get("example/BuildFailure01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final BuildResults buildResults = projectBuilder.build(source);

    assertThat(buildResults).isInstanceOf(EmptyBuildResults.class);
    assertThat(buildResults.isBuildFailed).isTrue();
  }

  @Test
  public void testBuildStringForExample01() {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final BuildResults buildResults = projectBuilder.build(source);

    final BinaryStore binaryStore = buildResults.getBinaryStore();

    assertThat(buildResults.isBuildFailed).isFalse();
    assertThat(binaryStore.getAll()).extracting(jmo -> jmo.getFqn())
        .containsExactlyInAnyOrder(FOO, FOO_TEST);
  }

  @Test
  public void testBuildStringForExample02() {
    final Path rootPath = Paths.get("example/BuildSuccess02");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final BuildResults buildResults = projectBuilder.build(source);

    final BinaryStore binaryStore = buildResults.getBinaryStore();
    assertThat(buildResults.isBuildFailed).isFalse();
    assertThat(binaryStore.getAll()).extracting(jmo -> jmo.getFqn())
        .containsExactlyInAnyOrder(FOO, FOO_TEST, BAR, BAR_TEST);
  }

  @Test
  public void testBuildStringForExample03() {
    final Path rootPath = Paths.get("example/BuildSuccess03");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);
    final BuildResults buildResults = projectBuilder.build(source);

    final BinaryStore binaryStore = buildResults.getBinaryStore();

    System.out.println(binaryStore.getAll());
    assertThat(buildResults.isBuildFailed).isFalse();
    assertThat(binaryStore.getAll()).extracting(jmo -> jmo.getFqn())
        .containsExactlyInAnyOrder(FOO, FOO_TEST, BAR, BAR_TEST, BAZ, BAZ_TEST, BAZ_INNER,
            BAZ_STATIC_INNER, BAZ_ANONYMOUS, BAZ_OUTER);
  }

  @Test
  public void testBuildStringForBuildSuccess07() {
    final Path rootPath = Paths.get("example/BuildSuccess07");
    final List<Path> srcPaths = Arrays.asList(rootPath.resolve("src"));
    final List<Path> testPaths = Arrays.asList(rootPath.resolve("test"));
    final TargetProject targetProject = TargetProjectFactory.create(rootPath, srcPaths, testPaths,
        Collections.emptyList(), JUnitVersion.JUNIT4);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);
    final BuildResults buildResults = projectBuilder.build(source);


    final BinaryStore binaryStore = buildResults.getBinaryStore();

    assertThat(buildResults.isBuildFailed).isFalse();
    assertThat(binaryStore.getAll()).extracting(jmo -> jmo.getFqn())
        .containsExactlyInAnyOrder(FOO, FOO_TEST, BAR, BAR_TEST);
  }

  // TODO: https://github.com/kusumotolab/kGenProg/pull/154
  // @Test
  public void testRemovingOldClassFiles() throws Exception {

    // example03のビルドが成功するかテスト
    final Path rootPath03 = Paths.get("example/BuildSuccess03");
    final TargetProject targetProject03 = TargetProjectFactory.create(rootPath03);
    final ProjectBuilder projectBuilder03 = new ProjectBuilder(targetProject03);
    final GeneratedSourceCode source03 = TestUtil.createGeneratedSourceCode(targetProject03);
    final BuildResults buildResults03 = projectBuilder03.build(source03);

    assertThat(buildResults03.isBuildFailed).isFalse();

    // example02のビルドが成功するかテスト
    final Path rootPath02 = Paths.get("example/BuildSuccess02");
    final TargetProject targetProject02 = TargetProjectFactory.create(rootPath02);
    final ProjectBuilder projectBuilder02 = new ProjectBuilder(targetProject02);
    final GeneratedSourceCode generatedSourceCode02 =
        TestUtil.createGeneratedSourceCode(targetProject02);
    final BuildResults buildResults02 = projectBuilder02.build(generatedSourceCode02);

    final BinaryStore binaryStore = buildResults02.getBinaryStore();

    assertThat(buildResults02.isBuildFailed).isFalse();
    assertThat(binaryStore.getAll()).extracting(jmo -> jmo.getFqn())
        .containsExactlyInAnyOrder(FOO, FOO_TEST, BAR, BAR_TEST);
  }

  @Test
  public void testBuildForInMemoryByteCode01() throws Exception {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final BuildResults buildResults = projectBuilder.build(source);

    final BinaryStore binaryStore = buildResults.getBinaryStore();
    assertThat(binaryStore.getAll()).hasSize(2);

    // buildResultsからバイトコードを取り出す
    final JavaBinaryObject jmo = buildResults.getBinaryStore()
        .getAll()
        .stream()
        .findFirst()
        .orElse(null);
    final MemoryClassLoader loader = new MemoryClassLoader();
    final TargetFullyQualifiedName fqn = new TargetFullyQualifiedName(jmo.getFqn()
        .toString());
    loader.addDefinition(fqn, jmo.getByteCode());

    // バイトコードが正しいのでうまくロードできるはず
    loader.loadClass(fqn);
    loader.close();
  }

  @Test
  public void testBuildWithExternalBinaryFile() throws Exception {
    final Path rootPath = Paths.get("example/BuildSuccess13");
    final List<Path> sources = Arrays.asList(rootPath.resolve("src"));
    final List<Path> tests = Collections.emptyList();
    final List<Path> cps = Arrays.asList(rootPath.resolve("lib"));

    final TargetProject targetProject =
        TargetProjectFactory.create(rootPath, sources, tests, cps, JUnitVersion.JUNIT4);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final BuildResults buildResults = projectBuilder.build(source);

    assertThat(buildResults.isBuildFailed).isFalse();

    final BinaryStore binaryStore = buildResults.getBinaryStore();
    assertThat(binaryStore.getAll()).extracting(jmo -> jmo.getFqn())
        .containsExactlyInAnyOrder(FOO);
  }

  @Test
  // 差分が存在しないソースコードに対する複数回ビルドの確認テスト
  public void testMultipleBuild() throws Exception {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);

    // 1回目，成功する
    final BuildResults buildResults1 = projectBuilder.build(source);
    final BinaryStore binaryStore1 = buildResults1.getBinaryStore();
    assertThat(binaryStore1.getAll()).hasSize(2);

    // 2回目，成功する
    final BuildResults buildResults2 = projectBuilder.build(source);
    final BinaryStore binaryStore2 = buildResults2.getBinaryStore();
    assertThat(binaryStore2.getAll()).hasSize(2);
  }

  @Test
  // 差分ビルドの確認テスト （一度ビルド，astを操作，2度目のビルド）
  public void testDifferentialBuild01() throws Exception {
    final Path rootPath = Paths.get("example/BuildSuccess14");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);

    // まず普通にビルド，成功するはず
    final BuildResults buildResults1 = projectBuilder.build(source);
    final BinaryStore binaryStore1 = buildResults1.getBinaryStore();
    assertThat(binaryStore1.getAll()).hasSize(3);

    // Fooのastを書き換えてコンパイル可能に
    final GeneratedAST<?> ast = source.getAllAsts()
        .get(1);
    assertThat(ast.getPrimaryClassName()).isEqualTo(FOO.value);
    final ASTLocation location = ast.getAllLocations()
        .get(3); // コンパイルエラーが発生しない7行目を削除
    assertThat(location.inferLineNumbers().start).isSameAs(7);
    final DeleteOperation dop = new DeleteOperation();
    final GeneratedSourceCode source2 = dop.apply(source, location);

    // 少し停止
    final long waitMs = 10L;
    Thread.sleep(waitMs);

    // 再度ビルド
    final BuildResults buildResults2 = projectBuilder.build(source2);
    final BinaryStore binaryStore2 = buildResults2.getBinaryStore();

    // 2つのビルド結果を比較．まず差分がない場合，キャッシュが効くので同一オブジェクトになるはず
    final JavaBinaryObject bar1 = binaryStore1.get(BAR);
    final JavaBinaryObject bar2 = binaryStore2.get(BAR);
    assertThat(bar1).isEqualTo(bar2);
    assertThat(bar1.getLastModified()).isEqualTo(bar2.getLastModified());

    final JavaBinaryObject fooTest1 = binaryStore1.get(FOO_TEST);
    final JavaBinaryObject fooTest2 = binaryStore2.get(FOO_TEST);
    assertThat(fooTest1).isEqualTo(fooTest2);
    assertThat(fooTest1.getLastModified()).isEqualTo(fooTest2.getLastModified());

    // 差分がある場合，キャッシュが効かないので別オブジェクトでかつ生成時刻がずれるはず
    final JavaBinaryObject foo1 = binaryStore1.get(FOO);
    final JavaBinaryObject foo2 = binaryStore2.get(FOO);
    assertThat(foo1).isNotEqualTo(foo2);
    assertThat(foo1.getLastModified()).isLessThanOrEqualTo(foo2.getLastModified() - waitMs);
  }

  @Test
  // 差分ビルドの確認テスト （一度ビルド，キャッシュを操作して不正バイナリを作成，astを操作，2度目のビルドで失敗）
  public void testDifferentialBuild02() throws Exception {
    final Path rootPath = Paths.get("example/BuildSuccess14");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);

    // まず普通にビルド，成功するはず
    final BuildResults buildResults = projectBuilder.build(source);
    final BinaryStore binaryStore = buildResults.getBinaryStore();
    assertThat(binaryStore.getAll()).hasSize(3);

    // buildResultsからBarのバイトコードを取り出す
    final JavaBinaryObject jmo = buildResults.getBinaryStore()
        .getAll()
        .stream()
        .findFirst()
        .orElse(null);

    // バイナリは378バイトのはず
    assertThat(jmo.getFqn().value).isEqualTo(BAR.value);
    assertThat(jmo.getByteCode()).hasSize(378);

    // バイナリの中身を不正な値に書き換え
    final ByteArrayOutputStream os = (ByteArrayOutputStream) jmo.openOutputStream();
    os.reset();
    os.write(new byte[] {0, 0, 0, 0});
    os.flush();

    // バイナリは4バイトのはず
    assertThat(jmo.getByteCode()).hasSize(4);

    // Fooのastを書き換えてコンパイル可能に
    final GeneratedAST<?> ast = source.getAllAsts()
        .get(1);
    assertThat(ast.getPrimaryClassName()).isEqualTo(FOO.value);
    final ASTLocation location = ast.getAllLocations()
        .get(3); // コンパイルエラーが発生しない7行目を削除
    assertThat(location.inferLineNumbers().start).isSameAs(7);
    final DeleteOperation dop = new DeleteOperation();
    final GeneratedSourceCode source2 = dop.apply(source, location);

    // 再度ビルドするとキャッシュが働いて上記Barの不正バイナリをロードし，失敗するはず
    final BuildResults buildResults2 = projectBuilder.build(source2);
    assertThat(buildResults2.isBuildFailed).isTrue();
    assertThat(buildResults2).isInstanceOf(EmptyBuildResults.class);
  }

}
