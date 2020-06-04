package nju.seg.zhangyf.scala.matlab.mclab.sample.matlab

import java.io.{ IOException, PrintWriter, StringReader }
import java.nio.charset.Charset
import java.nio.file.Paths
import java.util
import javax.annotation.ParametersAreNonnullByDefault

import org.antlr.runtime.{ CommonTokenStream, Token }

import matlab.{ MatlabParser, TextPosition, TrivialScanner }

import nju.seg.zhangyf.scala.matlab.mclab.UtilsMatlab

/**
 * @see ScannerTestTool
 *
 * @author Zhang Yifan
 */
@ParametersAreNonnullByDefault
object ReadTokenSample extends App {

  def readToken(): Unit = {

    val matlabSampleCodeCharset: Charset = Charset.forName("GB2312")
    val matlabSampleCodeRootDir = """/MatlabSampleCode"""
    //    implicit val config: UtilsConfig = new UtilsConfig {
    //      override def charset: Charset = matlabSampleCodeCharset
    //    }

    val outputFilePath = Paths.get("""Z:/outToken.txt""")
    val outputFileCharset: Charset = matlabSampleCodeCharset

    val matlabSampleCodeUrl = ReadTokenSample.getClass.getResource(s"$matlabSampleCodeRootDir/test/test.m")
    val matlabSampleCodePath = Paths.get(matlabSampleCodeUrl.toURI)
    // Files.readString(matlabSampleCodePath, matlabSampleCodeCharset)

    UtilsMatlab.createAndUsingMatlabLexer(matlabSampleCodePath) { lexer =>
      import resource._

      for {
        out <- managed(new PrintWriter(outputFilePath.toFile, outputFileCharset))
        tokens: CommonTokenStream = new CommonTokenStream(lexer)
        tokenList: util.List[Token] = tokens.getTokens.asInstanceOf[util.List[Token]]
      } {
        if (lexer.hasProblem) {
          val prob = lexer.getProblems.get(0)
          import scala.jdk.javaapi.CollectionConverters._
          asScala(tokenList).find { tok =>
            val isProb = tok.getLine > prob.getLine || (tok.getLine == prob.getLine && tok.getCharPositionInLine + 1 >= prob.getColumn)
            if (!isProb) { printToken(out, tok) }
            isProb
          }.foreach { tok =>
            out.print('~')
            out.print(' ')
            out.print(prob.getLine)
            out.print(' ')
            out.println(prob.getColumn)
            System.err.println(prob)
          }
        }
        else {
          import scala.jdk.javaapi.CollectionConverters._
          asScala(tokenList).foreach { printToken(out, _) }
        }
      }
    }

  }

  @throws[IOException]
  def printToken(out: PrintWriter, tok: Token): Unit = {
    out.print(MatlabParser.tokenNames(tok.getType))
    out.print(' ')
    val startLine = tok.getLine
    val startCol = tok.getCharPositionInLine + 1
    val lastPos = getLastPosition(tok.getText)
    if (lastPos.getLine == 1) {
      out.print(startLine)
      out.print(' ')
      out.print(startCol)
      out.print(' ')
      out.print(lastPos.getColumn)
    }
    else {
      out.print(startLine)
      out.print(' ')
      out.print(startCol)
      out.print(' ')
      out.print(startLine + lastPos.getLine - 1)
      out.print(' ')
      out.print(lastPos.getColumn)
    }
    out.print(' ')
    out.print('=')
    out.print(stringifyValue(tok.getText))
    out.println()
  }

  def stringifyValue(value: Any): String = {
    if (value == null) return null
    value.toString.replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r")
  }

  @throws[IOException]
  def getLastPosition(text: String): TextPosition = {
    val scanner = new TrivialScanner(new StringReader(text))
    var lastPos: TextPosition = null
    var isBreak = false
    while (!isBreak) {
      val temp = scanner.nextPos
      if (temp == null) {
        isBreak = true
      } else {
        lastPos = temp
      }
    }
    lastPos
  }

  //region Run as app

  ReadTokenSample.readToken()

  //endregion Run as app

}
