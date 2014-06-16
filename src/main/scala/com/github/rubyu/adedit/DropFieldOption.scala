
package com.github.rubyu.wok

import org.kohsuke.args4j.{Option => Opt, Argument => Arg}
import org.kohsuke.args4j.CmdLineParser
import scala.collection.JavaConversions._


class DropFieldOption {
  /**
   * 対象の列番号。
   */
  @Arg(index = 0)
  private var _column: String = _

  def column: Int = {
    Option(_column) match {
      case Some(x) => x.toInt match {
        case n if n < 0 => throw new ManagedFailure("'column' must be greater than or equal to zero")
        case n => n
      }
      case None => throw new ManagedFailure("'column' missing")
    }
  }

  def parseArgument(args: List[String]) = {
    new CmdLineParser(this).parseArgument(args)
    this
  }

  def validate() = {
    column
    this
  }
}
