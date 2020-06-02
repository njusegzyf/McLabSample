package mclint.refactoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import mclint.MatlabProgram;
import mclint.transform.Transformer;
import natlab.refactoring.Exceptions.NameConflict;
import natlab.toolkits.analysis.core.Def;
import natlab.toolkits.analysis.core.UseDefDefUseChain;
import natlab.utils.NodeFinder;
import ast.ASTNode;
import ast.Function;
import ast.FunctionList;
import ast.GlobalStmt;
import ast.Name;
import ast.Program;
import ast.Script;

class RenameVariable extends Refactoring {
  private Name node;
  private String newName;
  private UseDefDefUseChain udduChain;
  private boolean renameGlobally;

  private List<Refactoring> globalRenames = new ArrayList<>();

  public RenameVariable(RefactoringContext context, Name node, String newName,
      boolean renameGlobally) {
    super(context);
    this.node = node;
    this.newName = newName;
    this.udduChain = node.getMatlabProgram().analyze().getUseDefDefUseChain();
    this.renameGlobally = renameGlobally;
  }

  public RenameVariable(RefactoringContext context, Name node, String newName) {
    this(context, node, newName, true);
  }

  private boolean targetNameIsGlobal() {
    return getAllDefsOfTargetName().anyMatch(def -> def instanceof GlobalStmt);
  }

  private void checkPreconditionsForFunctionOrScript(ASTNode<?> ast, MatlabProgram program) {
    Name decl = findGlobalNamed(node.getID(), ast);
    if (decl != null) {
      // FIXME(isbadawi): Now that contexts aren't bound to a specific program, this is wrong.
      // Might need to change the API for RenameVariable.
      Refactoring rename = new RenameVariable(context, decl, newName, false);
      if (!rename.checkPreconditions()) {
        addErrors(rename.getErrors());
      }
      globalRenames.add(rename);
    }
  }

  @Override
  public boolean checkPreconditions() {
    if (renameGlobally && targetNameIsGlobal()) {
      for (MatlabProgram program : context.getProject().getMatlabPrograms()) {
        Program ast = program.parse();
        if (ast instanceof Script) {
          checkPreconditionsForFunctionOrScript(ast, program);
        } else {
          for (Function f : ((FunctionList) ast).getFunctions()) {
            checkPreconditionsForFunctionOrScript(f, program);
          }
        }
      }
      return getErrors().isEmpty();
    }

    ASTNode<?> parent = getParentFunctionOrScript(node);
    Optional<Name> conflictingName = NodeFinder.find(Name.class, parent)
        .filter(name -> name.getID().equals(newName))
        .findFirst();
    if (conflictingName.isPresent()) {
      addError(new NameConflict(conflictingName.get()));
      return false;
    }
    return true;
  }

  private static ASTNode<?> getParentFunctionOrScript(ASTNode<?> node) {
    ast.Function f = NodeFinder.findParent(ast.Function.class, node);
    if (f != null) {
      return f;
    }
    return NodeFinder.findParent(Script.class, node);
  }

  private Stream<Def> getAllDefsOfTargetName() {
    return NodeFinder.find(Def.class, getParentFunctionOrScript(node))
        .filter(def -> !udduChain.getDefinedNamesOf(node.getID(), def).isEmpty());
  }

  private static Name findGlobalNamed(final String name, ASTNode<?> tree) {
    return NodeFinder.find(Name.class, tree)
        .filter(node -> node.getParent().getParent() instanceof GlobalStmt)
        .filter(node -> node.getID().equals(name))
        .findFirst()
        .orElse(null);
  }

  @Override
  public void apply() {
    if (renameGlobally && !globalRenames.isEmpty()) {
      for (Refactoring rename : globalRenames) {
        rename.apply();
      }
      return;
    }

    Transformer transformer = context.getTransformer(node);
    getAllDefsOfTargetName()
        .flatMap(def -> Stream.concat(udduChain.getUsesOf(node.getID(), def).stream(),
                                      udduChain.getDefinedNamesOf(node.getID(), def).stream()))
        .forEach(name -> transformer.replace(name, new Name(newName)));
  }
}
