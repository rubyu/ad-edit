
package com.github.rubyu.adupdate

import java.io._
import scala.sys.process._
import java.nio.charset.Charset


object OuterProcess {

  def connect(processes: List[ProcessBuilder]): ProcessBuilder = {
    processes.size match {
      case x if x > 1 => processes.head #| connect(processes.tail)
      case x if x == 1 => processes.head
    }
  }

  def execute(commands: List[List[String]], input: String = ""): Array[Byte] = {
    if (commands.isEmpty) throw new IllegalArgumentException
    val inputStream = new ByteArrayInputStream(input.getBytes(Charset.defaultCharset))
    val outputStream = new ByteArrayOutputStream()
    var plist = commands map { stringSeqToProcess(_) }
    if (input.nonEmpty) {
      plist = plist.head #< inputStream +: plist.tail
    }
    connect(plist) #> outputStream ! ;
    outputStream.toByteArray
  }
}


