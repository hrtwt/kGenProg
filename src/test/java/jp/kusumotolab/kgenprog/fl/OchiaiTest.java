package jp.kusumotolab.kgenprog.fl;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.TargetProject;
import jp.kusumotolab.kgenprog.project.test.TestProcessBuilder;

public class OchiaiTest {

  @Test
  public void testForExample01() {

    final Path rootDir = Paths.get("example/example01");
    final Path outDir = rootDir.resolve("_bin");
    final TargetProject targetProject = TargetProject.generate(rootDir);
    final Variant initialVariant = targetProject.getInitialVariant();
    final TestProcessBuilder builder = new TestProcessBuilder(targetProject, outDir);

    final FaultLocalization fl = new Ochiai();
    final List<Suspiciouseness> suspeciousnesses = fl.exec(targetProject, initialVariant, builder);

    suspeciousnesses.sort(comparing(Suspiciouseness::getValue, reverseOrder()));

    assertThat(suspeciousnesses.get(0).getValue(), is(0.7071067811865475d));
    assertThat(suspeciousnesses.get(1).getValue(), is(0.5d));
    assertThat(suspeciousnesses.get(2).getValue(), is(0.5d));
    assertThat(suspeciousnesses.get(3).getValue(), is(0.5d));
  }

  @Test
  public void testForExample02() {

    final Path rootDir = Paths.get("example/example02");
    final Path outDir = rootDir.resolve("_bin");
    final TargetProject targetProject = TargetProject.generate(rootDir);
    final Variant initialVariant = targetProject.getInitialVariant();
    final TestProcessBuilder builder = new TestProcessBuilder(targetProject, outDir);

    final FaultLocalization fl = new Ochiai();
    final List<Suspiciouseness> suspeciousnesses = fl.exec(targetProject, initialVariant, builder);

    suspeciousnesses.sort(comparing(Suspiciouseness::getValue, reverseOrder()));

    assertThat(suspeciousnesses.get(0).getValue(), is(0.7071067811865475d));
    assertThat(suspeciousnesses.get(1).getValue(), is(0.5d));
    assertThat(suspeciousnesses.get(2).getValue(), is(0.5d));
    assertThat(suspeciousnesses.get(3).getValue(), is(0.5d));
  }


}