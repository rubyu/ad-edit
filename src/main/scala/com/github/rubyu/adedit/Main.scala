
package com.github.rubyu.adedit

import java.io._
import scala.util.control.Exception._
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import org.kohsuke.args4j.CmdLineException


class ManagedFailure(message: String) extends Exception(message)

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
  def setField(field: Int, f: List[String] => String): List[String] => List[String] = { row =>
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
    //todo この関数をすべて分けるべきでは
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

  def getMediaDir = {
    val dir = new File("collection.media")
    if (!dir.exists && !dir.mkdir()) {
      throw new ManagedFailure("Could not create 'collection.media' directory")
    }
    dir
  }

  def main(args: Array[String]) {
    try {
      allCatch opt args.head getOrElse("help") match {
        case "help" | "-h" | "-help" | "--help" =>
          printUsage(System.out)
        case "show-status" =>
          if (System.in.available() == 0) {
            throw new ManagedFailure("No input data; please input data from STDIN")
          }
        //統計情報をtsvで出力する
        case command =>
          if (System.in.available() == 0) {
            throw new ManagedFailure("No input data; please input data from STDIN")
          }
          val f = command match {
            case "insert-field" | "set-field" =>
              val option = new InsertOrSetFieldOption
              option.parseArgument(args.tail.toList)
              option.validate()
              val dir = getMediaDir
              val template = new Template(option.commands, dir.getAbsolutePath)
              val proc = process(typed(executeCommands(template, option.source), option.format), dir)
              command match {
                case "insert-field" => insertField(option.field, proc)
                case "set-field" => setField(option.field, proc)
              }
            case "drop-field" =>
              val option = new DropFieldOption
              option.parseArgument(args.tail.toList)
              option.validate()
              dropField(option.field)
          }
          new TsvUpdater().update(System.in, System.out)(f)
      }
    } catch {
      case e: CmdLineException =>
        System.err.println(s"Error: ${e.getMessage}")
      case e: ManagedFailure =>
        System.err.println(s"Error: ${e.getMessage}")
      case e: Throwable =>
        System.err.println(s"Unknown Error: ${e.toString}")
        e.printStackTrace(System.err)
    }
  }

  def printUsage(out: PrintStream) {
    out.println(List(
      "NAME",
      "",
      "ad-edit",
      "",
      "SYNOPSIS",
      "",
      "java -jar ad-edit.jar insert-field field [--source n] [--format ext] --exec command [| command ...]",
      "java -jar ad-edit.jar set-field field [--source n] [--format ext] --exec command [| command ...]",
      "java -jar ad-edit.jar drop-field field",
      "java -jar ad-edit.jar show-status",
      "java -jar ad-edit.jar help",
      "",
      "DESCRIPTION",
      "",
      "Edits a TSV file." +
        " The input data is read from STDIN and the result is output to STDOUT." +
        " The options are as follows:",
      "",
      "insert-field",
      "",
      "Generates new field and inserts it to before the specified field.",
      "",
      "field",
      "",
      "Target field number.",
      "",
      "--source n",
      "",
      "Source field number." +
        " If specified, the value of the field that corresponds to this value will be given to" +
        " the first command in --exec clause as STDIN input.",
      "",
      "--format ext",
      "",
      "The format of the data that the last command in --exec clause will be return." +
        " Allowed values are jpg, png, tif, gif, svg, wav, mp3, ogg, flac, mp4, swf, mov, mpg, mkv," +
        " m4a, html and txt.",
      "",
      "--exec command [| command ...]",
      "",
      "Definition of a generator of new field. command is a command expression" +
        " in a shell(e.g. echo -n hello!). --exec executes command and the result will be the data" +
        " of new field. In the avobe instance, the result will be hello!.",
      "",
      "command supports Template. The template expression is ${ }. Build-in values are" +
        " field and media. field provides access to the data of the fields." +
        " ... --exec echo -n \"${ field(0) }\" is equivalent to ... --source 0 --exec cat." +
        " media has a dir property that returns the absolute path to ./collection.media." +
        " To escape template expression, use $${ }. ... --exec echo -n \"$${ field(0) }\" will" +
        " be output $${ field(0) } as is.",
    "",
    "--exec supports Pipeline like a shell." +
      " The output of a command is connected to the input of the following command." +
      " And the output of last command will be treated as the result." +
      " The pipeline character is |, just the same as in a shell," +
      " but note that it in --exec clause must be escaped with shell's escape character for that reason.",
    "",
    "set-field",
    "",
    "Generates new field and overwrites the specified field.",
    "",
    "Options are the same as insert-field.",
    "",
    "drop-field",
    "",
    "Deletes the specified field.",
    "",
    "field",
    "",
    "Target field number.",
    "",
    "show-status",
    "",
    "Prints detail information.",
    "",
    "help",
    "",
    "Prints help.").mkString(System.lineSeparator))
    out.flush()
  }
}

