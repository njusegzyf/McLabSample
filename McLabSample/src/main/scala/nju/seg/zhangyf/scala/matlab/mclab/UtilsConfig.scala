package nju.seg.zhangyf.scala.matlab.mclab

import java.nio.charset.Charset
import javax.annotation.ParametersAreNonnullByDefault

/**
 * @author Zhang Yifan
 */
@ParametersAreNonnullByDefault
private[matlab] trait UtilsConfig {

  def charset: Charset
  def inputCharset: Charset = this.charset
  def outputCharset: Charset = this.charset

}

private[matlab] object UtilsConfig {

  implicit val DEFAULT_CONFIG: UtilsConfig = new UtilsConfig {
    override val charset: Charset = Charset.forName("GB2312")
  }

}


