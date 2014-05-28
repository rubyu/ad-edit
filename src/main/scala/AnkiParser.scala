
package com.github.rubyu.adupdate

import util.parsing.combinator.RegexParsers


object AnkiParser {
  object result {
    trait Element
    case class Tags(value: String) extends Element
    case class Comment(value: String) extends Element
    case class Row(value: List[String]) extends Element
    case class EOL(value: String) extends Element
  }
}

class AnkiParser extends RegexParsers {
  override def skipWhitespace = false

  def first_line      = tags_res | comment_res | row_res | eol_res
  def line            =            comment_res | row_res | eol_res

  def tags_res        = tags    ^^ { AnkiParser.result.Tags(_) }
  def comment_res     = comment ^^ { AnkiParser.result.Comment(_) }
  def row_res         = row     ^^ { AnkiParser.result.Row(_) }
  def eol_res         = EOL     ^^ { AnkiParser.result.EOL(_) }

  def tags            = "tags:" ~ rep( not( EOL ) ~> any ) ^^ { case a ~ b => a + b.mkString }
  def comment         = "#"     ~ rep( not( EOL ) ~> any ) ^^ { case a ~ b => a + b.mkString }
  def row             = repsep( field, DELIMITER )

  def field           = padding ~> quoted_field <~ padding | raw_value
  def quoted_field    = QUOTE   ~> quoted_value <~ QUOTE
  def padding         = rep( SPACE )
  def escaped_quote   = QUOTE ~ QUOTE ^^^ QUOTE
  def quoted_value    = rep1( escaped_quote | not( QUOTE ) ~> any | EOL ) ^^ { _.mkString }
  def raw_value       = rep( not( DELIMITER ) ~> any ) ^^ { _.mkString }

  def any             = ".".r
  def SPACE           = ' '
  def QUOTE           = '"'
  def DELIMITER       = '\t'
  def EOL             = "\r\n" | "\n"
}