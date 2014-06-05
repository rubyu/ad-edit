
package com.github.rubyu.adupdate

import org.kohsuke.args4j.{Option => Opt, Argument => Arg}
import org.kohsuke.args4j.CmdLineParser
import scala.collection.JavaConversions._


class DropFieldOption {
  /**
   * 対象の列番号。
   */
  @Arg(index = 0)
  private var _field: String = _

  def field: Int = {
    Option(_field) match {
      case Some(x) => x.toInt match {
        case n if n < 0 => throw new IllegalArgumentException
        case n => n
      }
      case None => throw new IllegalArgumentException
    }
  }

  /**
   * 引数を処理して、フィールドに値を設定する。
   * @param args
   */
  def parseArgument(args: List[String]) = {
    new CmdLineParser(this).parseArgument(args)
    this
  }

  def validate() = {
    field
    this
  }
}
