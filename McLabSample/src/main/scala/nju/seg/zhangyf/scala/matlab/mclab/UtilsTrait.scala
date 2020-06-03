package nju.seg.zhangyf.scala.matlab.mclab

import java.io.{ BufferedReader, FileReader }
import java.nio.charset.Charset
import java.nio.file.{ Files, Path }
import javax.annotation.ParametersAreNonnullByDefault

import org.antlr.runtime.ANTLRReaderStream

import matlab.MatlabLexer
import natlab.NatlabScanner

/**
 * @author Zhang Yifan
 */
@ParametersAreNonnullByDefault
private[matlab] trait UtilsTrait {

  implicit def getDefaultConfig: UtilsConfig = UtilsConfig.DEFAULT_CONFIG

}
