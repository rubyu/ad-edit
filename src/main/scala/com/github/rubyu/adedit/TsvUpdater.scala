
package com.github.rubyu.adedit

import java.io._
import java.nio.charset.StandardCharsets
import com.github.rubyu.adedit.AnkiTsvParser.result._


/**
 * タブ切りCSVファイルに変更を加えるクラス。
 * 入出力はUTF-8でエンコードされていると仮定する。
 * ファイル中に出現するTags, Commentは無視される。
 * クォートの仕様はAnkiに準拠する。
 * 列数は可変でよい。
 * 改行コードは"\r\n"に書き換えられる。
 */

class TsvUpdater {

  def update(input: InputStream, output: OutputStream)(f: List[String] => List[String]) {
    val reader = new AnkiTsvReader(new InputStreamReader(input, StandardCharsets.UTF_8))
    val writer = new AnkiTsvWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8))
    reader foreach {
      case elem: Row => writer.write(f(elem.value))
      case elem: Comment =>
      case elem: Tags =>
      case elem: InvalidString =>
        System.err.println(s"invalid string('${elem.value}') found; at line ${reader.lastSuccess.getOrElse(-1) + 2}")
        System.err.flush()
    }
  }
}