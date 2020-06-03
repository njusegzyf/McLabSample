package nju.seg.zhangyf.scala.matlab.mclab

import java.io.{ BufferedReader, FileReader }
import java.nio.charset.Charset
import java.nio.file.{ Files, Path }
import javax.annotation.ParametersAreNonnullByDefault

import org.antlr.runtime.ANTLRReaderStream

import matlab.MatlabLexer

/**
 * @author Zhang Yifan
 */
@ParametersAreNonnullByDefault
private[matlab] object UtilsMatlab extends UtilsTrait {

  /** Construct a lexer that will read from the specified file and uses the lexer. */
  private[matlab] def createAndUsingMatlabLexer(filePath: Path)
                                               (useLexer: MatlabLexer => Unit)
                                               (implicit config : UtilsConfig)
  : Unit = {
    require(filePath != null && Files.isReadable(filePath))
    require(config != null)
    require(useLexer != null)

    val fileCharset: Charset = config.inputCharset
    import resource._
    for {
      in <- managed(new BufferedReader(new FileReader(filePath.toFile, fileCharset)))
      lexer = new MatlabLexer(new ANTLRReaderStream(in))
    } {
      useLexer(lexer)
    }
  }

}
