
package com.github.rubyu.adupdate

import java.io._
import scala.sys.process._
import java.nio.charset.StandardCharsets
import annotation.tailrec


object OuterProcess {

  def connect(list: List[ProcessBuilder]): ProcessBuilder = {
    @tailrec
    def connect(p: ProcessBuilder, list: List[ProcessBuilder]): ProcessBuilder = {
      if (list.isEmpty) p
      else connect(p #| list.head, list.tail)
    }
    if (list.isEmpty) throw new IllegalArgumentException
    connect(list.head, list.tail)
  }

  def execute(commands: List[List[String]], input: String = ""): Array[Byte] = {
    if (commands.isEmpty) throw new IllegalArgumentException
    val outputStream = new ByteArrayOutputStream()
    val plist = if (input.nonEmpty) {
      val inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))
      commands.head #< inputStream +: commands.tail.map { stringSeqToProcess(_) }
    } else {
      commands.map { stringSeqToProcess(_) }
    }
    connect(plist) #> outputStream !;
    outputStream.toByteArray
  }
}


