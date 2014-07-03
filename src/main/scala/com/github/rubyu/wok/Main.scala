
package com.github.rubyu.wok

import java.io._
import scala.util.control.Exception._
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
    row.padTo(field+1, "").updated(field, f(row))
  }

  def executeScript(template: Template): List[String] => String = { row =>
    template.layout(row)
  }

  def mediaDir = {
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
        case command @ ("insert" | "set" | "drop") =>
          val f = command match {
            case "insert" | "set" =>
              val option = new InsertOrSetFieldOption
              option.parseArgument(args.tail.toList)
              option.validate()
              val template = new Template(option.script, option.args, mediaDir.getAbsolutePath)
              command match {
                case "insert" => insertField(option.column, executeScript(template))
                case "set" => setField(option.column, executeScript(template))
              }
            case "drop" =>
              val option = new DropFieldOption
              option.parseArgument(args.tail.toList)
              option.validate()
              dropField(option.column)
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
    out.println("See https://github.com/rubyu/wok")
    out.flush()
  }
}

