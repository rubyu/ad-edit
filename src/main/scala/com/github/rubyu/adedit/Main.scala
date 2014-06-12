
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

  def executeCommands(template: Template, source: Option[Int], mediaDir: String): List[String] => Array[Byte] = { row =>
    val input = allCatch opt row(source.get) getOrElse("")
    OuterProcess.execute(template.layout(row, mediaDir), input)
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
        case command @ ("insert-field" | "set-field" | "drop-field") =>
          val f = command match {
            case "insert-field" | "set-field" =>
              val option = new InsertOrSetFieldOption
              option.parseArgument(args.tail.toList)
              option.validate()
              val dir = getMediaDir
              val template = new Template(option.commands)
              val proc = process(typed(executeCommands(template, option.source, dir.getAbsolutePath), option.format), dir)
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
        case command @ _ => throw new ManagedFailure(s"'${command}' is not a supported command")
      }
    } catch {
      case e: CmdLineException =>
        System.err.println(s"Error: ${e.getMessage}")
        System.exit(1)
      case e: ManagedFailure =>
        System.err.println(s"Error: ${e.getMessage}")
        System.exit(1)
      case e: Throwable =>
        System.err.println(s"Unknown Error: ${e.toString}")
        e.printStackTrace(System.err)
        System.exit(1)
    }
    System.exit(0)
  }

  def printUsage(out: PrintStream) {
    out.println("See https://github.com/rubyu/ad-edit")
    out.flush()
  }
}

