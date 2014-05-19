
package com.github.rubyu.adupdate

import org.kohsuke.args4j.{Option => Opt}
import org.kohsuke.args4j.CmdLineParser
import scala.collection.JavaConversions._


class CliOption {

  /**
   * `--exec`以降のすべてのパラメータ。
   */
  var exec = List.empty[List[String]]

  /**
   * `--exec`以降に指定される最初のコマンドに標準入力として与えられる列の列番号。
   */
  @Opt(name = "--exec-source")
  var execSource: String = _

  /**
   * 入力ファイル。
   */
  @Opt(name = "--input")
  var input: String = _

  /**
   * 出力ファイル。
   */
  @Opt(name = "--output")
  var output: String = _

  /**
   * 処理結果が書き込まれる列番号。
   */
  @Opt(name = "--field")
  var field: String = _

  /**
   * 処理結果のタイプ。
   */
  @Opt(name = "--field-type")
  var fieldType: String = _

  /**
   * 処理対象行番号。
   */
  @Opt(name = "--row")
  var row: String = _

  /**
   * `--input`で指定されたファイルがすでに存在していた場合に、ファイル・ディレクトリを消去して続行する。
   */
  @Opt(name = "--overwrite")
  var overwrite: Boolean = _

  /**
   * 処理結果を書き込む際に、フィールドにすでにデータが存在していた場合、上書きする。
   */
  @Opt(name = "--update")
  var update: Boolean = _

  /**
   * 処理結果を実際には書き込まず、標準出力にバイパスする。
   */
  @Opt(name = "--test")
  var test: Boolean = _

  /**
   * ヘルプメッセージを出力する。
   */
  @Opt(name = "-h", aliases = Array("--help"))
  var help: Boolean = _

  /**
   * 引数を処理して、フィールドに値を設定する。
   * args4jの仕様で、固定引数を柔軟に処理できないため、手動で`--exec`以降をパースして処理している。
   * @param args
   */
  def parseArgument(args: List[String]) {
    val (options, commands) = args indexOf "--exec" match {
      case n if n >= 0 => (args take n, args drop n+1)
      case -1 => (args, List.empty[String])
    }

    new CmdLineParser(this) parseArgument(options)

    def split(list: List[List[String]], commands: List[String]): List[List[String]] = {
      commands indexOf "|" match {
        case n if n > 0 => (commands take n) :: split(list, (commands drop n+1))
        case  0 => split(list, (commands drop 1))
        case -1 => if (commands isEmpty) list else commands :: list
      }
    }

    if (commands nonEmpty)
      exec = split(List.empty[List[String]], commands)
  }
}
