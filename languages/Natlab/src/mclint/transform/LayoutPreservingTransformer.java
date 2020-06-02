package mclint.transform;

import java.io.IOException;
import java.io.Reader;

import mclint.util.AstUtil;
import mclint.util.Parsing;
import ast.ASTNode;
import ast.Program;

import com.google.common.io.CharStreams;

/**
 * A utility for performing layout-preserving AST transformations.
 * 
 * Given the text of an input program, this class maintains a token stream together with an AST
 * and keeps them synchronized as the AST changes.
 *
 */
class LayoutPreservingTransformer implements Transformer {
  private TokenStream tokenStream;
  private Program program;

  static LayoutPreservingTransformer of(Reader source) throws IOException {
    String code = CharStreams.toString(source);
    Program program = Parsing.string(code);
    TokenStream tokens = TokenStream.create(code);
    return new LayoutPreservingTransformer(program, tokens);
  }
  
  @Override
  public void remove(ASTNode<?> node) {
    tokenStream.removeAstNode(node);
    AstUtil.remove(node);
  }
  
  @Override
  public void replace(ASTNode<?> oldNode, ASTNode<?> newNode) {
    tokenStream.replaceAstNode(oldNode, newNode);
    AstUtil.replace(oldNode, newNode);
  }
  
  @Override
  public void insert(ASTNode<?> node, ASTNode<?> newNode, int i) {
    tokenStream.insertAstNode(node, newNode, i);
    node.insertChild(newNode, i);
  }
  
  @Override
  public <T extends ASTNode<?>> T copy(T node) {
    return tokenStream.copyAstNode(node);
  }

  public Program getProgram() {
    return program;
  }
  
  public String reconstructText() {
    return tokenStream.asString();
  }
  
  private LayoutPreservingTransformer(Program program, TokenStream tokens) {
    this.program = program;
    this.tokenStream = tokens;
  }
}
