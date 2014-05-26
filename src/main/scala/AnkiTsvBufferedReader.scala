
package com.github.rubyu.adupdate

import java.io._
import collection.mutable.ListBuffer


class AnkiTsvBufferedReader(in: Reader) extends BufferedReader(in) {

  private var buffer = List[Int]()
  private var firstL = true
  private var firstC = true

  override def read(cbuf: Array[Char], off: Int, len: Int): Int = {
    for(i <- 0 until len) {
      read() match {
        case -1 if i == 0 => return -1
        case -1 => return i
        case x => cbuf(off+i) = x.toChar
      }
    }
    return len
  }

  override def read(): Int = {
    if (buffer.nonEmpty) {
      val (head, tail) = (buffer.head, buffer.tail)
      buffer = tail
      return head
    }
    in.read() match {
      case -1 => -1
      case '#' if firstC =>
        readAhead()
        read()
      case 't' if firstL && firstC =>
        readAhead() match {
          case x if x.startsWith(List('a', 'g', 's', ':')) =>
          case x => buffer = x
        }
        read()
      case '\n' =>
        firstC = true
        firstL = false
        '\n'
      case x =>
        firstC = false
        x
    }
  }

  def readAhead(): List[Int] = {
    val buffer = ListBuffer[Int]()
    while (true) {
      in.read() match {
        case -1 => return buffer.toList
        case x =>
          buffer += x
          if (x == '\n') return buffer.toList
      }
    }
    buffer.toList //dummy
  }
}