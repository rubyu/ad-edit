
package com.github.rubyu.adedit

import java.io._
import scala.sys.process._
import java.nio.charset.StandardCharsets
import annotation.tailrec
import concurrent.SyncVar


object OuterProcess {

  def execute(commands: List[List[String]], input: String = ""): Array[Byte] = {
    def execute(input: Array[Byte], command: List[String]): Array[Byte] = {
      val in = new SyncVar[Unit]
      val out = new SyncVar[Array[Byte]]
      val err = new SyncVar[Array[Byte]]

      def streamToBytes(in: InputStream) = Stream.continually(in.read()).takeWhile(-1 !=).map(_.toByte).toArray

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
        p.exitValue() match {
          case 0 =>
          case _ => throw new RuntimeException("Process returns non zero return code")
        }
        err.get match {
          case bytes if bytes.size > 0 => System.err.println(new String(bytes, "utf-8"))
          case _ =>
        }
        out.get
      } finally {
        p.destroy()
      }
    }

    @tailrec
    def executeSeq(input: Array[Byte], commands: List[List[String]]): Array[Byte] = {
      commands match {
        case Nil => throw new IllegalArgumentException("Command must not be empty")
        case head :: Nil => execute(input, head)
        case head :: tail => executeSeq(execute(input, head), tail)
      }
    }
    executeSeq(input.getBytes(StandardCharsets.UTF_8), commands)
  }
}


