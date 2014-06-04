
package com.github.rubyu.adupdate

import java.io._
import scala.util.control.Exception._
import java.nio.charset.StandardCharsets
import java.security.MessageDigest


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

  trait ExecuteResult {
    def html: String
  }

  trait BinaryResult extends ExecuteResult {
    val value: Array[Byte]
    val ext: String
    lazy val name = s"${value.sha1}.$ext"

    def save(parent: File) {
      val file = new File(parent, name)
      if (!file.exists()) {
        val stream = new BufferedOutputStream(new FileOutputStream(file))
        try {
          stream.write(value)
        } finally {
          stream.close()
        }
      }
    }
  }

  case class ImageResult(value: Array[Byte], ext: String) extends BinaryResult {
    def html = s"<img src=$name>"
  }

  case class AudioResult(value: Array[Byte], ext: String) extends BinaryResult {
    def html = s"[sound:$name]"
  }

  case class PlainTextResult(value: String) extends ExecuteResult {
    def escape(text: String) = {
      val s = new StringBuilder
      val len = text.length
      var pos = 0
      while (pos < len) {
        text.charAt(pos) match {
          case '<' => s.append("&lt;")
          case '>' => s.append("&gt;")
          case '&' => s.append("&amp;")
          case '"' => s.append("&quot;")
          case '\'' => s.append("&#39;")
          case '\n' => s.append('\n')
          case '\r' => s.append('\r')
          case '\t' => s.append('\t')
          case c => if (c >= ' ') s.append(c)
        }
        pos += 1
      }
      s.toString
    }
    def html = escape(value)
  }

  case class HTMLResult(value: String) extends ExecuteResult {
    def html = value
  }

  case object EmptyResult extends ExecuteResult {
    def html = ""
  }

  def executeCommands(template: Template, source: Option[Int]): List[String] => Array[Byte] = { row =>
    val input = allCatch opt row(source.get) getOrElse("")
    OuterProcess.execute(template.layout(row), input)
  }

  def process(f: List[String] => ExecuteResult, mediaDir: File): List[String] => String = { row =>
    f(row) match {
      case x: BinaryResult => x.save(mediaDir); x.html
      case x => x.html
    }
  }

  def typed(f: List[String] => Array[Byte], mimeType: String): List[String] => ExecuteResult = { row =>
    f(row) match {
      case x if x.isEmpty => EmptyResult
      case x =>
        mimeType match {
          case "jpg" | "jpeg" => ImageResult(x, "jpg")
          case "png" => ImageResult(x, "png")
          case "tif" | "tiff" => ImageResult(x, "tif")
          case "gif" => ImageResult(x, "gif")
          case "svg" => ImageResult(x, "svg")

          case "wav" => AudioResult(x, "wav")
          case "mp3" => AudioResult(x, "mp3")
          case "ogg" => AudioResult(x, "ogg")
          case "flac" => AudioResult(x, "flac")
          case "mp4" => AudioResult(x, "mp4")
          case "swf" => AudioResult(x, "swf")
          case "mov" => AudioResult(x, "mov")
          case "mpg"| "mpeg" => AudioResult(x, "mpeg")
          case "mkv" => AudioResult(x, "mkv")
          case "m4a" => AudioResult(x, "m4a")

          case "html" | "htm" => HTMLResult(new String(x, StandardCharsets.UTF_8))
          case "text" | "txt" => PlainTextResult(new String(x, StandardCharsets.UTF_8))
        }
    }
  }

  implicit class ArrayByteMessageDigest(self: Array[Byte]) {
    def sha1 = {
      val md = MessageDigest.getInstance("SHA-1")
      md.update(self)
      md.digest.map { "%02x".format(_) } mkString
    }
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

