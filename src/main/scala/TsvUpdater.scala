
package com.github.rubyu.adupdate

import java.io._
import java.util
import java.nio.charset.StandardCharsets
import scala.collection.JavaConversions._
import com.orangesignal.csv.{QuotePolicy, Csv, CsvConfig}
import com.orangesignal.csv.handlers.StringArrayListHandler


/**
 * AnkiがExportするTSVファイルに変更を加えるクラス。
 * ・ファイル中に出現するコメントはすべて予め除去される
 * ・コメントの除去後、最初の行にタグが設定してある場合、それをそのまま出力する
 * ・クォートの仕様はAnki（Pythonのcsvモジュールを使用。quoteChar='"', escapeChar=None, doubleQuote=True）に準拠
 *
 *
 * メモ
 *
 * 不意にタブや改行が紛れ込むことはありえるので、Ankiとの互換性を満たすのが重要
 * ・#から始まる行をすべて削除
 * ・ファイルの先頭がtags:なら、それをそのままスルー
 * ・Csvとして、QuoteChar='"',escapeChar=None, doublequote=True　な処理する
 * ・列数の制約はStrictに
 *
 * http://ankisrs.net/docs/manual.html
 * ・列数を最初の行で判定する
 * ・列セパレータを最初の行で判定する
 * ・escapeChar = None
 *
 * https://github.com/dae/anki/blob/master/anki/importing/csvfile.py
 * http://docs.python.jp/2/library/csv.html
 * TextImporter.openFile
 * ・ファイルを開いて
 * ・ファイル先頭のBOM削除
 * ・全体から#で開始する行を除去
 * ・改行を\nに正規化
 * ・ファイルの先頭行が"tags:"から始まっていれば、以降のスペース切りの文字列をグローバルなタグに設定し、行を削除
 * ・デリミタを推定し、正規化（恐らく識別率は低い）
 * ・列数を取得
 *
 * TextImporter.foreignNotes
 * ・列数が一致しない行は無視される
 */

class TsvUpdater {

  val handler = new StringArrayListHandler

  val readCfg = new CsvConfig('\t', '\"', '\"')
  readCfg.setVariableColumns(true)

  val writeCfg = new CsvConfig('\t', '\"', '\"')
  writeCfg.setQuotePolicy(QuotePolicy.MINIMAL)
  writeCfg.setVariableColumns(true)

  def update(input: InputStream, output: OutputStream)(f: Array[String] => Array[String]) {
    val reader = new AnkiTsvBufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))
    val writer = new PrintWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8))
    try {
      val results = new util.ArrayList[Array[String]]()
      //todo
      Csv.load(reader, readCfg, handler) foreach { row => results.add(f(row)) }
      Csv.save(results, writer, writeCfg, handler)
    } finally {
      reader.close()
      writer.close()
    }
  }
}