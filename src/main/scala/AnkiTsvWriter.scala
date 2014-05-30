
package com.github.rubyu.adupdate

import java.io._


class AnkiTsvWriter(out: Writer) {

  /**
   * 与えられた行をタブ切りCSV形式でoutに書き込む。
   * データについて、EOL、QUOTE、DELIMが含まれている場合に、クォーティングを行う。
   * 改行コードは"\r\n"固定とする。
   *
   * Ankiのexporting.pyでは
   * 1. "\n" -> "<br>"
   * 2. "\t" -> " " * 8
   * 3. "<style>...</style>" -> ""
   * 4. QUOTEが含まれている場合にのみ、クォーティング
   * が行われているが、データを破壊するのは良くないのでそのまま保存する。
   */
  def write(row: List[String]) {
    val output = row.map { text =>
      if (text.contains("\r") | text.contains("\n") | text.contains("\t") | text.contains("\"")) {
        "\"" + text.replace("\"", "\"\"") + "\""
      } else {
        text
      }
    }.mkString("\t")
    if (output.size > 0) {
      out.write(output)
      out.write("\r\n")
      out.flush()
    }
  }
}