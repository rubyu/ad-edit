
package com.github.rubyu.adupdate

import org.kohsuke.args4j.{Option => Opt, Argument => Arg}
import org.kohsuke.args4j.CmdLineParser
import scala.collection.JavaConversions._
import annotation.tailrec


class InsertOrSetFieldOption {
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
   * `--exec`以降に指定される最初のコマンドに標準入力として与えられる列の列番号。
   */
  @Opt(name = "--source")
  private var _source: String = _

  def source: Option[Int] = {
    Option(_source) match {
      case Some(x) => x.toInt match {
        case n if n < 0 => None
        case n => Some(n)
      }
      case None => None
    }
  }

  /**
   * コマンドの実行結果のフォーマット。
   */
  @Opt(name = "--format")
  private var _format: String = _

  def format: String = {
    Option(_format) match {
      case Some(x) => x.toLowerCase match {
        case s if s.matches("^(jpe?g|png|tif?f|gif|svg)$") => s
        case s if s.matches("^(wav|mp3|ogg|flac|mp4|swf|mov|mpe?g|mkv|m4a)$") => s
        case s if s.matches("^(html?|te?xt)$") => s
        case _ => throw new IllegalArgumentException
      }
      case None => throw new IllegalArgumentException
    }
  }

  /**
   * `--exec`以降のすべてのパラメータ。
   */
  private var _exec = List[String]()

  def commands: List[List[String]] = {
    //List[String]をパイプ文字(|)で分割して、List[List[String]]に変換する。
    def split(commands: List[String]) = {
      @tailrec
      def split(list: List[List[String]], commands: List[String]): List[List[String]] = {
        commands.indexOf("|") match {
          case -1 if commands.isEmpty => throw new IllegalArgumentException
          case -1 => list :+ commands
          case 0 => throw new IllegalArgumentException
          case n => split(list :+ commands.take(n), commands.drop(n+1))
        }
      }
      split(List[List[String]](), commands)
    }
    split(_exec)
  }

  /**
   * 引数を処理して、フィールドに値を設定する。
   * args4jの仕様で、固定引数を柔軟に処理できないため、手動で`--exec`以降をパースして処理している。
   */
  def parseArgument(args: List[String]) = {
    val (before, after) = args indexOf "--exec" match {
      case n if n >= 0 => (args take n, args drop n+1)
      case -1 => (args, List[String]())
    }
    new CmdLineParser(this).parseArgument(before)
    _exec = after
    this
  }

  def validate() = {
    field
    source
    format
    commands
    this
  }
}
