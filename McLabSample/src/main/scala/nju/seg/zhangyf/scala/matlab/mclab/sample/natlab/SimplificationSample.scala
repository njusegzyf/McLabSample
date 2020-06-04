package nju.seg.zhangyf.scala.matlab.mclab.sample.natlab

import java.nio.charset.{ Charset, StandardCharsets }
import java.nio.file.{ Path, Paths }
import java.util
import java.util.stream.Collectors
import javax.annotation.ParametersAreNonnullByDefault

import ast._
import natlab.{ CompilationProblem, Parse }
import natlab.toolkits.rewrite.Simplifier
import natlab.toolkits.rewrite.simplification._

/**
 * @author Zhang Yifan
 */
@ParametersAreNonnullByDefault
private[natlab] object SimplificationSample extends App {

  def simplify(): Unit = {

    val matlabSampleCodeCharset: Charset = Charset.forName("GB2312")
    val matlabSampleCodeRootDir = """/MatlabSampleCode"""

    val outputFileRootPath = Paths.get("""Z:/""")

    val matlabSampleCodeUrl = SimplificationSample.getClass.getResource(s"$matlabSampleCodeRootDir/test/test.m")
    val matlabSampleCodePath = Paths.get(matlabSampleCodeUrl.toURI)

    val input: Program = parseFile(matlabSampleCodePath, fileCharset = matlabSampleCodeCharset)
    val simp: Simplifier = new Simplifier(input.asInstanceOf[ASTNode[_]],
                                          FullSimplification.getStartSet)
    val actual: ASTNode[_ <: ASTNode[_]] = simp.simplify()

    println(actual.getPrettyPrinted)
    // actualProgram.write(Paths.get(outputFileRootPath.toString))
  }

  def parseFile(filePath: Path, fileCharset: Charset = StandardCharsets.UTF_8): Program = {
    val errList: util.ArrayList[CompilationProblem] = new util.ArrayList[CompilationProblem]
    // val prog: Program = Parse.parseNatlabFile(filePath.toString, fileCharset, errList)
    val prog: Program = Parse.parseNatlabFile(filePath, fileCharset, errList)
    if (prog == null) {
      throw new Exception(s"Error parsing file " + filePath.toString + ":\n" + errList.stream.map(_.toString).collect(Collectors.joining("\n")))
    }
    prog
  }

  //region Run as an app

  SimplificationSample.simplify()

  //endregion Run as an app
}

//region Generated test class

/*
package natlab;

import ast.*;
import natlab.toolkits.rewrite.Simplifier;
import natlab.toolkits.rewrite.simplification.*;
import natlab.toolkits.rewrite.Validator;
import natlab.McLabCore;

public class MultiRewritePassTests extends RewritePassTestBase
{
    public void ._McLabSample_target_classes_GenList_SimplificationSampleTests() throws Exception
    {
        ASTNode actual, expected;
        Simplifier simp;
        boolean test = true;
        StringBuilder errmsg = new StringBuilder();

        actual = parseFile( "test/SimplificationSampleSource:GenList/passtest04.in" );
        simp = new Simplifier( actual, SimplificationSample.getStartSet() );
        actual = simp.simplify();
        expected = parseFile( "test/SimplificationSampleSource:GenList/passtest04.out");
        test &= checkEquiv( actual, expected, "test_SimplificationSampleSource:GenList_passtest04", errmsg);

        assertTrue( errmsg.toString(), test );
    }
}
 */

//endregion Generated test class
