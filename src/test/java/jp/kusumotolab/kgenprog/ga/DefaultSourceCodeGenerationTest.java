package jp.kusumotolab.kgenprog.ga;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.TargetProject;
import org.junit.Test;

public class DefaultSourceCodeGenerationTest {

  @Test
  public void noneOperationTest() {
    final TargetProject targetProject = TargetProject.generate("example/example01");
    final SourceCodeGeneration defaultSourceCodeGeneration = new DefaultSourceCodeGeneration();
    final Gene simpleGene = new SimpleGene(new ArrayList<>());
    final Base noneBase = new Base(null, new NoneOperation());
    final List<Gene> genes = simpleGene.generateNextGenerationGenes(Arrays.asList(noneBase));

    // noneBaseを適用した単一のGeneを取り出す
    final Gene gene = genes.get(0);

    final GeneratedSourceCode generatedSourceCode =
        defaultSourceCodeGeneration.exec(gene, targetProject);
    final GeneratedSourceCode initialSourceCode =
        targetProject.getInitialVariant().getGeneratedSourceCode();

    // ファイル数は同じはず
    assertThat(generatedSourceCode.getFiles().size(), is(initialSourceCode.getFiles().size()));

    // NoneOperationにより全てのソースコードが初期ソースコードと等価であるはず
    for (int i = 0; i < targetProject.getSourceFiles().size(); i++) {
      final String expected = initialSourceCode.getFiles().get(i).getSourceCode();
      final String actual = generatedSourceCode.getFiles().get(i).getSourceCode();
      assertThat(actual, is(expected));
    }
  }

  // TODO: None以外のOperationでテストする必要有り
}
