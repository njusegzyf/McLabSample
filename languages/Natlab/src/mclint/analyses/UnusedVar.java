package mclint.analyses;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import mclint.Lint;
import mclint.LintAnalysis;
import mclint.Message;
import mclint.Project;
import mclint.util.DefinitionVisitor;
import natlab.toolkits.analysis.core.LivenessAnalysis;
import natlab.utils.NodeFinder;
import ast.ForStmt;
import ast.Function;
import ast.Name;
import ast.Stmt;

public class UnusedVar extends DefinitionVisitor implements LintAnalysis {
  private LivenessAnalysis liveVar;

  /* We shouldn't warn that output parameters aren't used. */
  private Set<String> outputParams = new HashSet<>();

  protected Lint lint;

  private void reportUnused(Name node) {
    lint.report(Message.regarding(node, "UNUSED_VAR",
        String.format("Unused variable %s.", node.getID())));
  }

  public UnusedVar(Project project) {
    super(project.asCompilationUnits());
  }

  @Override
  public void caseFunction(Function node) {
    Set<String> outputParamsCopy = new HashSet<>(outputParams);
    outputParams.addAll(node.getOutputParams().stream()
        .map(Name::getID)
        .collect(Collectors.toSet()));
    super.caseFunction(node);
    outputParams.retainAll(outputParamsCopy);
  }

  private boolean isLive(Name node) {
    Stmt parentStmt = NodeFinder.findParent(Stmt.class, node);
    Set<String> setToCheck;
    if (parentStmt.getParent() instanceof ForStmt) {
      Stmt first = ((ForStmt) (parentStmt.getParent())).getStmt(0);
      setToCheck = liveVar.getInFlowSets().get(first);
    } else {
      setToCheck = liveVar.getOutFlowSets().get(parentStmt);
    }
    return setToCheck.contains(node.getID());
  }

  @Override
  public void caseLHSName(Name node) {
    if (!outputParams.contains(node.getID()) && !isLive(node)) {
      reportUnused(node);
    }
  }

  @Override
  public void caseInParam(Name node) {
    Stmt firstStatement = NodeFinder.findParent(Function.class, node).getStmt(0);
    if (!(liveVar.getInFlowSets().containsKey(firstStatement) &&
        liveVar.getInFlowSets().get(firstStatement).contains(node.getID()))) {
      reportUnused(node);
    }
  }

  @Override
  public void analyze(Lint lint) {
    this.lint = lint;
    this.liveVar = lint.getKit().getLiveVariablesAnalysis();
    tree.analyze(this);
  }
}
