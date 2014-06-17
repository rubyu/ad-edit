
package com.github.rubyu.wok

import java.io._
import scala.sys.process._
import java.nio.charset.StandardCharsets
import annotation.tailrec
import concurrent.SyncVar


object Proc {
  def apply(commandStrings: String*) = new Proc(commandStrings.toList)
  def apply(commandStrings: List[String]) = new Proc(commandStrings)
}

class Proc(commandStrings: List[String]) {

  val commands = parse(commandStrings)

  /**
   * List[String]をパイプ文字(|)で分割して、List[List[String]]に変換したものを返す。
   */
  private def parse(commandStrings: List[String]) = {
    @tailrec
    def split(list: List[List[String]], strings: List[String]): List[List[String]] = {
      strings.indexOf("|") match {
        case -1 if strings.isEmpty => throw new IllegalArgumentException("'command' must not be empty")
        case -1 => list :+ strings
        case 0 => throw new IllegalArgumentException("command must not start with '|'")
        case n => split(list :+ strings.take(n), strings.drop(n+1))
      }
    }
    split(List[List[String]](), commandStrings)
  }

  def exec() = executeCommands()
  def execWithInput(bytes: Array[Byte]) = executeCommands(bytes)
  def execWithInput(in: InputStream) = executeCommands(streamToBytes(in))
  def execWithInput(input: String) = executeCommands(input.getBytes(StandardCharsets.UTF_8))

  private def streamToBytes(in: InputStream) = Stream.continually(in.read()).takeWhile(-1 !=).map(_.toByte).toArray

  private def executeCommands(input: Array[Byte] = Array[Byte]()): String = {
    def execute(input: Array[Byte], command: List[String]): Array[Byte] = {
      val in = new SyncVar[Unit]
      val out = new SyncVar[Array[Byte]]
      val err = new SyncVar[Array[Byte]]

      val p = command.run(new ProcessIO(
        pin => {
          try {
            in put pin.write(input)
          } finally {
            pin.close()
          }
        }, pout => {
          try {
            out put streamToBytes(pout)
          } finally {
            pout.close()
          }
        }, perr => {
          try {
            err put streamToBytes(perr)
          } finally {
            perr.close()
          }
        }
      ))
      try {
        in.get
        err.get match {
          case bytes if bytes.size > 0 => System.err.println(new String(bytes, StandardCharsets.UTF_8))
          case _ =>
        }
        p.exitValue() match {
          case 0 =>
          case exitCode => throw new RuntimeException(s"Process returns non zero exit code(${exitCode})")
        }
        out.get
      } finally {
        p.destroy()
      }
    }

    @tailrec
    def executeSeq(input: Array[Byte], commands: List[List[String]]): Array[Byte] = {
      commands match {
        case Nil => throw new IllegalArgumentException("command must not be empty")
        case head :: Nil => execute(input, head)
        case head :: tail => executeSeq(execute(input, head), tail)
      }
    }
    new String(executeSeq(input, commands), StandardCharsets.UTF_8)
  }
}


