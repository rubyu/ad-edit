
package com.github.rubyu.wok

import java.io._
import java.nio.charset.StandardCharsets
import com.orangesignal.csv.{CsvWriter, CsvReader, QuotePolicy, CsvConfig}
import scala.collection.JavaConversions._

/**
 * タブ切りCSVファイルに変更を加えるクラス。
 * 入出力はUTF-8でエンコードされていると仮定する。
 * ファイル中に出現するTags, Commentは無視される。
 * クォートの仕様はAnkiに準拠する。
 * 列数は可変でよい。
 * 改行コードは"\r\n"に書き換えられる。
 */

class TsvUpdater {

  val readCfg = new CsvConfig('\t', '\"', '\"')
  readCfg.setVariableColumns(true)

  val writeCfg = new CsvConfig('\t', '\"', '\"')
  writeCfg.setQuotePolicy(QuotePolicy.MINIMAL)
  writeCfg.setVariableColumns(true)

  def update(input: InputStream, output: OutputStream)(f: List[String] => List[String]) {
    val reader = new CsvReader(new InputStreamReader(input, StandardCharsets.UTF_8), readCfg)
    val writer = new CsvWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8), writeCfg)
    try {
      Iterator.continually(reader.readValues()) takeWhile(_ != null) foreach {
        case row if row.size == 1 && row(0).isEmpty =>
        case row =>
          writer.writeValues(f(row.toList))
          writer.flush()
      }
    } finally {
      reader.close()
      writer.close()
    }
  }
}