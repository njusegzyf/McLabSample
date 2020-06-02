package natlab.refactoring;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import mclint.refactoring.Refactoring;
import mclint.refactoring.RefactoringContext;
import mclint.transform.StatementRange;
import mclint.transform.Transformer;
import natlab.toolkits.analysis.core.Def;
import natlab.toolkits.analysis.core.LivenessAnalysis;
import natlab.toolkits.analysis.core.ReachingDefs;
import natlab.toolkits.analysis.varorfun.VFDatum;
import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import natlab.utils.NodeFinder;
import analysis.Analysis;
import ast.AssignStmt;
import ast.ExprStmt;
import ast.Function;
import ast.FunctionList;
import ast.GlobalStmt;
import ast.MatrixExpr;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.Row;
import ast.Stmt;

public class ExtractFunction extends Refactoring {
  private StatementRange extractionRange;
  private String extractedFunctionName;
  private Transformer transformer;

  private Function extracted;

  private Map<String, Set<Def>> reachingBefore;
  private Map<String, Set<Def>> reachingAfter;
  private Set<String> liveBefore;
  private Set<String> liveAfter;
  private Map<String, VFDatum> kinds;

  private Set<String> addedGlobals = new HashSet<>();
  private Set<String> addedInputs = new HashSet<>();
  
  public ExtractFunction(RefactoringContext context, StatementRange extractionRange,
      String extractedFunctionName) {
    super(context);
    this.context = context;
    this.extractionRange = extractionRange;
    this.extractedFunctionName = extractedFunctionName;
    this.transformer = context.getTransformer(extractionRange.getEnclosingFunction());
  }

  private void extractStatements() {
    extracted = new Function();
    extracted.setName(new Name(extractedFunctionName));
    for (Stmt stmt : extractionRange) {
      extracted.addStmt(transformer.copy(stmt));
    }
  }

  private <T extends Analysis> T analyze(T analysis) {
    analysis.analyze();
    return analysis;
  }
  private void analyzeBeforeAndAfter() {
    Function original = extractionRange.getEnclosingFunction();
    ReachingDefs reachingAnalysisOrig = analyze(new ReachingDefs(original));
    ReachingDefs reachingAnalysisNew = analyze(new ReachingDefs(extracted));
    LivenessAnalysis liveAnalysisOrig = analyze(new LivenessAnalysis(original));
    LivenessAnalysis liveAnalysisNew = analyze(new LivenessAnalysis(extracted));
    VFPreorderAnalysis kindAnalysis = analyze(new VFPreorderAnalysis(original));

    Stmt startStmt = extractionRange.getStartStatement();
    Stmt endStmt = extractionRange.getEndStatement();

    reachingBefore = reachingAnalysisOrig.getOutFlowSets().get(startStmt);
    reachingAfter = reachingAnalysisNew.getOutFlowSets().get(extracted);
    liveBefore = liveAnalysisNew.getInFlowSets().get(extracted);
    liveAfter = liveAnalysisOrig.getOutFlowSets().get(endStmt);
    kinds = kindAnalysis.getFlowSets().get(original);
  }

  private Set<String> liveVariablesAtInputOfNewFunction() {
    return liveBefore.stream()
        .filter(id -> kinds.getOrDefault(id, VFDatum.UNDEF).isVariable())
        .collect(Collectors.toSet());
  }

  private Set<String> liveVariablesDefinedInNewFunction() {
    return liveAfter.stream()
        .filter(id -> kinds.getOrDefault(id, VFDatum.UNDEF).isVariable() &&
                      reachingAfter.containsKey(id))
        .collect(Collectors.toSet());
  }

  private boolean containsGlobalStmt(Set<Def> defs) {
    return defs.stream().anyMatch(def -> def instanceof GlobalStmt);
  }

  private boolean originallyGlobal(String id) {
    return containsGlobalStmt(reachingBefore.get(id));
  }

  private boolean globalInNewFunction(String id) {
    return containsGlobalStmt(reachingAfter.get(id));
  }

  private void makeGlobal(String id) {
    if (addedGlobals.contains(id)) {
      return;
    }
    GlobalStmt globalDecl =
        new GlobalStmt(new ast.List<Name>().add(new Name(id)));
    extracted.getStmts().insertChild(globalDecl, 0);
    addedGlobals.add(id);
  }

  private boolean originallyDefined(String id) {
    return !(reachingBefore.get(id).isEmpty() ||
        reachingBefore.get(id).contains(ReachingDefs.UNDEF));
  }

  private void makeInputParameter(String id) {
    if (addedInputs.contains(id)) {
      return;
    }
    extracted.addInputParam(new Name(id));
    addedInputs.add(id);
  }

  private void makeOutputParameter(String id) {
    extracted.addOutputParam(new Name(id));
  }

  private void reportVariableMightBeUndefined(String id) {
    addError(new Exceptions.FunctionInputCanBeUndefined(new Name(id)));
  }

  private void reportOutputMightBeUndefined(String id) {
    addError(new Exceptions.FunctionOutputCanBeUndefined(new Name(id)));
  }

  private void makeExtractedFunctionSiblingOfOriginal() {
    Function original = extractionRange.getEnclosingFunction();
    ast.List<Function> list = 
        NodeFinder.findParent(FunctionList.class, original).getFunctions();
    ast.List<Stmt> stmts = extracted.getStmts().fullCopy();
    while (extracted.getNumStmt() != 0) {
      extracted.getStmts().removeChild(0);
    }
    
    transformer.insert(list, extracted, list.getIndexOfChild(original) + 1);
    for (Stmt stmt : stmts) {
      transformer.insert(extracted.getStmts(), stmt, extracted.getNumStmt());
    }
  }

  private void replaceExtractedStatementsWithCallToExtractedFunction() {
    Stmt call = makeCallToExtractedFunction();
    for (int i = 0; i < extractionRange.size(); ++i) {
      transformer.remove(extractionRange.getStartStatement());
    }
    transformer.insert(extractionRange.getStatements(), call,
        extractionRange.getStartIndex());
  }

  private Stmt makeCallToExtractedFunction() {
    ParameterizedExpr call = new ParameterizedExpr();
    call.setTarget(new NameExpr(new Name(extractedFunctionName)));
    for (Name input : extracted.getInputParams()) {
      call.getArgs().add(new NameExpr(new Name(input.getID())));
    }

    switch (extracted.getNumOutputParam()) {
    case 0:
      return new ExprStmt(call);
    case 1:
      return new AssignStmt(
          new NameExpr(new Name(extracted.getOutputParam(0).getID())), call);
    default:
      Row row = new Row();
      for (Name name : extracted.getOutputParams()) {
        row.addElement(new NameExpr(new Name(name.getID())));
      }
      return new AssignStmt(
          new MatrixExpr(new ast.List<Row>().add(row)), call);
    }
  }

  public Function getExtractedFunction() {
    return extracted;
  }

  @Override
  public void apply() {
    extractStatements();
    analyzeBeforeAndAfter();

    for (String id : liveVariablesAtInputOfNewFunction()) {
      if (originallyGlobal(id)) {
        makeGlobal(id);
      } else if (originallyDefined(id)) {
        makeInputParameter(id);
      } else {
        reportVariableMightBeUndefined(id);
      }
    }

    for (String id : liveVariablesDefinedInNewFunction()) {
      if (globalInNewFunction(id)) {
        continue;
      }
      if (reachingAfter.get(id).contains(ReachingDefs.UNDEF)) {
        makeOutputParameter(id);
        makeInputParameter(id);
        reportOutputMightBeUndefined(id);
      } else {
        makeOutputParameter(id);
      }
    }

    // TODO this doesn't quite match what's in the thesis; more checks related
    // to preservation of kinds are described.

    makeExtractedFunctionSiblingOfOriginal();
    replaceExtractedStatementsWithCallToExtractedFunction();
  }

  @Override
  public boolean checkPreconditions() {
    // TODO figure out real preconditions
    return true;
  }
}