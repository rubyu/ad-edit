
package com.github.rubyu.adedit

import java.io._
import collection.mutable.ListBuffer
import AnkiTsvParser.result._
import collection.mutable
import annotation.tailrec


class AnkiTsvReader(in: Reader) extends Iterator[Element] {

  private val parser = new AnkiTsvParser

  private var firstLine = true

  private var lineNumber = -1
  private var _lastSuccess: Option[Int] = None

  /**
   * Elementが1回正常にパースされるまではNone,それ以降は最後にパースされた、0から始まる行番号を返す。
   */
  def lastSuccess = _lastSuccess

  @tailrec
  private def readLine(buffer: ListBuffer[Char] = new ListBuffer[Char]): String = {
    in.read() match {
      case -1 => if (buffer.nonEmpty) lineNumber += 1; buffer.mkString
      case n => n.toChar match {
        case '\n' => lineNumber += 1; buffer += '\n'; buffer.mkString
        case c => buffer += c; readLine(buffer)
      }
    }
  }

  private val queue = mutable.Queue[Element]()

  /**
   * Elementを継承するTags、Comment、Row、InvalidStringのいずれかを返す。EOLは返さない。
   * Commentを無視して、最初の行に対するパースにのみTagsが出現する可能性がある。
   * パーサがTags, Comment, Rowを返した場合はそのまま返す。
   * パースしきれない余りが残った場合はInvalidStringを返す。
   */
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
                x.get match {
                  case elem: EOL => _parseNext()
                  case elem =>
                    if (!elem.isInstanceOf[Comment]) firstLine = false
                    _lastSuccess = Some(lineNumber)
                    Some(elem)
                }
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