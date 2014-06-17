
package com.github.rubyu.wok

import org.kohsuke.args4j.{Option => Opt, Argument => Arg}
import org.kohsuke.args4j.CmdLineParser
import scala.collection.JavaConversions._


class InsertOrSetFieldOption {
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

  @Arg(index = 1)
  private var _script: String = _

  def script: String = {
    Option(_script) match {
      case None => throw new ManagedFailure("'script' missing")
      case Some(x) => x
    }
  }

  @Arg(index = 2)
  private var _args: Array[String] = _

  def args: List[String] = {
    Option(_args) match {
      case None => List[String]()
      case Some(x) => x.toList
    }
  }

  def parseArgument(args: List[String]) = {
    new CmdLineParser(this).parseArgument(args)
    this
  }

  def validate() = {
    column
    script
    this
  }
}
