package nju.seg.zhangyf.scala.matlab.mclab.sample.natlab

import java.nio.charset.Charset
import java.nio.file.{ Files, Paths }
import javax.annotation.ParametersAreNonnullByDefault

import natlab.MultiRewritePassTestGenerator

import nju.seg.zhangyf.scala.matlab.mclab.sample.matlab.TranslateToNatlabSample

/**
 * @author Zhang Yifan
 */
@ParametersAreNonnullByDefault
object MultiRewritePassTestGeneratorSample extends App {

  def run(): Unit = {

    val masterlistFileCharset: Charset = Charset.forName("UTF8")
    val masterlistFileName = """SimplificationSample.masterlist"""
    val masterlistFileUrl = TranslateToNatlabSample.getClass.getResource(s"/GenList/$masterlistFileName")

    Files.createDirectories(Paths.get("Z:/gen/natlab"))
    new MultiRewritePassTestGenerator().generate(Array(masterlistFileUrl.getFile, "Z:/gen"))
  }

  //region Run as an app

  MultiRewritePassTestGeneratorSample.run()

  //endregion Run as an app

}
