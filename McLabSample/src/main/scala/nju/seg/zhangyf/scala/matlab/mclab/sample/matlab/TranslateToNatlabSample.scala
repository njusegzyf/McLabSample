package nju.seg.zhangyf.scala.matlab.mclab.sample.matlab

import java.io.{ BufferedReader, FileReader, StringReader }
import java.nio.charset.Charset
import java.nio.file.{ Files, Paths }
import java.util
import javax.annotation.ParametersAreNonnullByDefault

import org.antlr.runtime.ANTLRReaderStream

import matlab.{ CompositePositionMap, FunctionEndScanner, MatlabParser, OffsetTracker, PositionMap, TextPosition, TranslationProblem }

/**
 * @author Zhang Yifan
 */
@ParametersAreNonnullByDefault
object TranslateToNatlabSample extends App {

  def translateSingleFileToNatlab(): Unit = {

    val matlabSampleCodeCharset: Charset = Charset.forName("GB2312")
    val matlabSampleCodeRootDir = """/MatlabSampleCode"""

    val outputFilePath = Paths.get("""Z:/test_ex-outNatlab.txt""")
    val outputFileCharset: Charset = matlabSampleCodeCharset

    // OK: copy.m, test_ex
    val matlabSampleCodeUrl = TranslateToNatlabSample.getClass.getResource(s"$matlabSampleCodeRootDir/test/test_ex.m")
    val matlabSampleCodePath = Paths.get(matlabSampleCodeUrl.toURI)

    import resource._
    for {
      in <- managed(new BufferedReader(new FileReader(matlabSampleCodePath.toFile, matlabSampleCodeCharset)))
    } {
      val offsetTracker: OffsetTracker = new OffsetTracker(new TextPosition(1, 1))
      val problems: util.ArrayList[TranslationProblem] = new util.ArrayList[TranslationProblem]
      val destText = MatlabParser.translate(new ANTLRReaderStream(in), 1, 1, offsetTracker, problems)

      if (problems.isEmpty) {
        Files.writeString(outputFilePath, destText, outputFileCharset)
      } else {
        println(s"Error translate $matlabSampleCodePath to Natlab.")
        //        val failureStringBuffer = new StringBuffer
        //        import scala.collection.JavaConversions._
        //        for (prob <- problems) {
        //          failureStringBuffer.append("~" + prob + "\n") //TODO-AC: cross-platform?
        //        }
        //        return new TranslatorTestBase.ActualTranslation(posMap, failureStringBuffer.toString)
      }
    }
  }

  def translateComplexFileToNatlab(): Unit = {

    val matlabSampleCodeCharset: Charset = Charset.forName("GB2312")
    val matlabSampleCodeRootDir = """/MatlabSampleCode"""

    val outputFilePath = Paths.get("""Z:/test-outNatlab.txt""")
    val outputFileCharset: Charset = matlabSampleCodeCharset

    val matlabSampleCodeUrl = TranslateToNatlabSample.getClass.getResource(s"$matlabSampleCodeRootDir/test/test.m")
    val matlabSampleCodePath = Paths.get(matlabSampleCodeUrl.toURI)

    import resource._

    val result: FunctionEndScanner.Result =
      (for {
        in <- managed(new BufferedReader(new FileReader(matlabSampleCodePath.toFile, matlabSampleCodeCharset)))
      } yield {
        val preScanner = new FunctionEndScanner(in)
        preScanner.translate
      }).opt.get

    var prePosMap: PositionMap = null

    def createNewReader(): BufferedReader = result match {
      case _: FunctionEndScanner.NoChangeResult =>
        new BufferedReader(new FileReader(matlabSampleCodePath.toFile, matlabSampleCodeCharset)) //just re-open original file

      case transResult: FunctionEndScanner.TranslationResult =>
        prePosMap = transResult.getPositionMap
        new BufferedReader(new StringReader(transResult.getText))

      case errorResult: FunctionEndScanner.ProblemResult =>
        import scala.jdk.javaapi.CollectionConverters._
        asScala(errorResult.getProblems) foreach { prob => System.err.println("~" + prob) }
        throw (new IllegalArgumentException("Encountered problems while filling in optional function ends."))
    }

    for {
      in <- managed(createNewReader())
    } {
      val offsetTracker = new OffsetTracker(new TextPosition(1, 1))
      val problems = new util.ArrayList[TranslationProblem]
      val destText = MatlabParser.translate(new ANTLRReaderStream(in), 1, 1, offsetTracker, problems)

      var posMap = offsetTracker.buildPositionMap
      if (prePosMap != null) {
        posMap = new CompositePositionMap(posMap, prePosMap)
      }

      if (problems.isEmpty) {
        Files.writeString(outputFilePath, destText, outputFileCharset)
      } else {
        println(s"Error translate $matlabSampleCodePath to Natlab.")
      }
    }
  }

  //region Run as app

  TranslateToNatlabSample.translateSingleFileToNatlab()
  TranslateToNatlabSample.translateComplexFileToNatlab()

  //endregion Run as app

}
