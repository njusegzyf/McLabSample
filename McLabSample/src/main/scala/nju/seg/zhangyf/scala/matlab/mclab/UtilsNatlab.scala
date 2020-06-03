package nju.seg.zhangyf.scala.matlab.mclab

import java.io.{ BufferedReader, FileReader }
import java.nio.charset.Charset
import java.nio.file.{ Files, Path }
import javax.annotation.ParametersAreNonnullByDefault

import natlab.NatlabScanner

/**
 * @author Zhang Yifan
 */
@ParametersAreNonnullByDefault
private[matlab] object UtilsNatlab extends UtilsTrait {

  /** Construct a scanner that will read from the specified file and uses the scanner. */
  private[matlab] def createAndUsingNatlabScanner(filePath: Path)
                                                 (useScanner: NatlabScanner => Unit)
                                                 (implicit config: UtilsConfig)
  : Unit = {
    require(filePath != null && Files.isReadable(filePath))
    require(useScanner != null)

    val fileCharset: Charset = config.inputCharset
    import resource._
    for {
      in <- managed(new BufferedReader(new FileReader(filePath.toFile, fileCharset)))
      lexer = new NatlabScanner(in)
    } {
      useScanner(lexer)
    }
  }

}
