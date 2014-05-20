
package com.github.rubyu.adupdate

import scala.util.control.Exception._
import java.io._
import com.orangesignal.csv.Csv
import com.orangesignal.csv.handlers.StringArrayListHandler
import java.util

import com.orangesignal.csv.{CsvReader, CsvConfig}

object Main {
  def main(args: Array[String]) {

    /*
  --exec (optional)  command ...

	{{ field-n }}			//headerがあれば楽だが、リストとレシピは分離するべきなのでわかりづらい
	{{ input-media-dir }}
	{{ output-media-dir }}

	--exec-source      n (optional)
	--input            deck (required)
	--output           deck (required)
	--field            n (required)
	--field-type	     { html, image, audio } (default is html)
  --row		          n (optional)
	--overwrite       (optional)
	--update          (optional)
	--test	          (optional)		//--exec句がない場合は--exec-sourceの内容をそのまま標準出力に書く
     */

    val option = new CliOption
    option.parseArgument(args toList)

    if (option.help) {
      //printUsage(System.out, parser)
      System.exit(0)
    }

    //required, must exist
    val input = allCatch opt new File(option.input)
    val inputMediaDir = allCatch opt new File(input.get.getAbsolutePath + ".media")

    //required, must not already exist
    val output = allCatch opt new File(option.output)
    val outputMediaDir = allCatch opt new File(output.get.getAbsolutePath + ".media")

    //optional
    val execSource = allCatch opt option.execSource.toInt

    //required
    val field = allCatch opt option.field.toInt

    //default is html
    //todo specify the type by case classes
    val fieldType = allCatch opt Option(option.fieldType) getOrElse("html")

    //optional
    val row = allCatch opt option.execSource.toInt


    //check required values
    //todo check arguments

    if (input.isEmpty) {
      //--input is required
      System.exit(1)
    }

    if (!input.get.exists) {
      //input file path is invalid
      System.exit(1)
    }

    if (!option.test) {
      if (output.isEmpty) {
        //--output is required
        System.exit(1)
      }

      if (!option.overwrite && output.get.exists) {
        //output file already exists
        System.exit(1)
      }

      if (field.isEmpty) {
        //--field is required
        System.exit(1)
      }
    }

    //prepare
    if (option.overwrite && outputMediaDir.nonEmpty && outputMediaDir.get.exists) {
      //delete output-media-dir
    }

    //make input-media-dir
    if (inputMediaDir.nonEmpty && !inputMediaDir.get.exists) {

    }

    //make output-media-dir
    if (outputMediaDir.nonEmpty && !outputMediaDir.get.exists) {

    }

    //do job

    System.exit(0)
  }
}


/**
 *
 * 下記のAnkiの仕様を満たして、かつ簡易に実装できるようにするため、さらに
 * ・入出力にescape, quoteを使用しない。
 * という制約を加えている。
 *
 *
 * http://ankisrs.net/docs/manual.html
 * ・列数を最初の行で判定する
 * ・列セパレータを最初の行で判定する
 * ・escapeChar = None
 *
 * https://github.com/dae/anki/blob/master/anki/importing/csvfile.py
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

  val readCfg = new CsvConfig()
  readCfg.setSeparator('\t')

  val writeCfg = new CsvConfig()
  writeCfg.setSeparator('\t')

  def row2str(arr: Array[String]) = arr map { s => s.replace("\t", "").replace("\n", "") } mkString "\t"

  def line2row(input: Reader): Array[String] = Csv.load(input, readCfg, handler).get(0)

  def update(input: InputStream, output: OutputStream)(f: Array[String] => Array[String]) {
    val reader = new BufferedReader(new InputStreamReader(input, "utf-8"))
    val writer = new PrintStream(new BufferedOutputStream(output), true, "utf-8")
    val lines = Iterator.continually(reader.readLine()) takeWhile(_ != null) toList

    for ((line, i) <- lines.zipWithIndex) {
      if (0 == i && line.startsWith("tags:") ||
        line.startsWith("#")) {
        writer.print(line)
      } else {
        writer.print(row2str(f(line2row(new StringReader(line)))))
      }

      if (i < lines.size - 1) {
        writer.print(System.lineSeparator)
      }
    }
    writer.flush()
  }
}


object OuterProcess {
  //template, bindings
  def call(commands: List[List[String]], source: Option[String], row: Option[Int]): String = {
    ""
  }
}
