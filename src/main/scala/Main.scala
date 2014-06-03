
package com.github.rubyu.adupdate

import java.io._
import scala.util.control.Exception._
import scala.collection.JavaConversions._


object Main {

  /**
   * 指定された列を削除する関数を返す。
   * @param field 0以上の整数
   * @return
   */
  def dropField(field: Int): List[String] => List[String] = { row =>
    if (field < row.size) {
      val buffer = row.toBuffer
      buffer.remove(field)
      buffer.toList
    } else {
      row
    }
  }

  /**
   * 指定された列に、f()が生成したデータを挿入する関数を返す。
   * @param field 0以上の整数
   * @return
   */
  def insertField(field: Int, f: List[String] => String): List[String] => List[String] = { row =>
    val buffer = row.padTo(field, "").toBuffer
    buffer.insert(field, f(row))
    buffer.toList
  }

  /**
   * 指定された列に、f()が生成したデータで上書きする関数を返す。
   * @param field 0以上の整数
   * @return
   */
  def updateField(field: Int, f: List[String] => String): List[String] => List[String] = { row =>
    val buffer = row.padTo(field+1, "").toBuffer
    buffer.update(field, f(row))
    buffer.toList
  }

  trait ExecuteResult
  case class ImageResult(value: Array[Byte], ext: String) extends ExecuteResult
  case class AudioResult(value: Array[Byte], ext: String) extends ExecuteResult
  case class StringResult(value: String) extends ExecuteResult


  def executeCommands(template: Template, source: Option[Int]): List[String] => Array[Byte] = { row =>
    val input = allCatch opt row(source.get) getOrElse("")
    OuterProcess.execute(template.layout(row), input)
  }

  def main(args: Array[String]) {

    /*
    insert-field field
    set-field field
      field           0から始まるフィールド番号
      //--row         {n, none}
			//--file		    Input file. If - STDIN will be read.
			--format	      拡張子
			//--media-dir	    default is "collection.media"
			//--test        --exec句がない場合は--exec-sourceの内容をそのまま出力する
			--source	     {n, none} default is none //あるいはechoで手動で？
			--exec

		drop-field field
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

