
package com.github.rubyu.adupdate

import java.io._
import collection.mutable.ListBuffer
import AnkiTsvParser.result._
import collection.mutable
import annotation.tailrec


class AnkiTsvReader(in: Reader) extends Iterator[Element] {

  private val parser = new AnkiTsvParser

  private var firstLine = true

  @tailrec
  private def readLine(buffer: ListBuffer[Char] = new ListBuffer[Char]): String = {
    in.read() match {
      case -1 => buffer.mkString
      case n => n.toChar match {
        case '\n' => buffer += '\n'; buffer.mkString
        case c => buffer += c; readLine(buffer)
      }
    }
  }

  private var queue = mutable.Queue[Element]()

  private def parseNext(): Option[Element] = {
    if (queue.nonEmpty) {
      Some(queue.dequeue())
    } else {
      @tailrec
      def _parseNext(prev: String = ""): Option[Element] = {
        readLine() match {
          case line if line.isEmpty && prev.nonEmpty => Some(InvalidString(prev))
          case line if line.isEmpty => None
          case line =>
            val text = prev + line
            val p = if (firstLine) parser.first_line else parser.line
            parser.parse(p, text) match {
              case x if x.successful =>
                text.drop(x.next.offset) match {
                  case rest if rest.size == 2 && rest.endsWith("\r\n") => queue += EOL("\r\n")
                  case rest if rest.size == 1 && rest.endsWith("\n") => queue += EOL("\n")
                  case rest if rest.size == 0 =>
                  case rest if rest.endsWith("\r\n") => queue += InvalidString(rest.dropRight(2)); queue += EOL("\r\n")
                  case rest if rest.endsWith("\n") => queue += InvalidString(rest.dropRight(1)); queue += EOL("\n")
                  case rest => queue += InvalidString(rest)
                }
                val elem = x.get
                if (!elem.isInstanceOf[Comment] && !elem.isInstanceOf[EOL]) {
                  firstLine = false
                }
                Some(elem)
              case x => _parseNext(text)
            }
        }
      }
      _parseNext()
    }
  }

  private var _next: Option[Element] = None

  def hasNext = {
    _next match {
      case Some(x) => true
      case None => _next = parseNext(); _next.isDefined
    }
  }

  def next() = {
    _next match {
      case Some(x) => _next = None; x
      case None => parseNext().getOrElse(throw new NoSuchElementException)
    }
  }
}