package nju.seg.zhangyf.scala.matlab.mclab

import java.nio.charset.{ Charset, StandardCharsets }
import java.nio.file.{ Files, Paths }
import javax.annotation.ParametersAreNonnullByDefault

import com.google.common.base.Charsets

/**
 * @author Zhang Yifan
 */
@ParametersAreNonnullByDefault
object Sample extends App {

  def readMatlabSample(): Unit = {
    val matlabSampleCodeCharset: Charset = Charset.forName("GB2312")
    val matlabSampleCodesRootDir = """/MatlabSampleCode"""
    val matlabSampleCodeUrl = Sample.getClass.getResource(s"$matlabSampleCodesRootDir/test/test.m")
    Files.readString(Paths.get(matlabSampleCodeUrl.toURI), matlabSampleCodeCharset)
  }

  //region Run as app

  Sample.readMatlabSample()

  //endregion Run as app

}
