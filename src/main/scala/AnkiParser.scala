
package com.github.rubyu.adupdate

import util.parsing.combinator.RegexParsers


class AnkiParser extends RegexParsers {
  override def skipWhitespace = false

  var firstLine = true

  def line = firstLine match {
    case true       => tags | comment | row | EOL
    case false      => comment | row | EOL
  }
  def tags            = "tags:" ~ rep( not( EOL ) ~> any ) ^^ { case a ~ b => a + b.mkString }
  def comment         = "#" ~ rep( not( EOL ) ~> any ) ^^ { case a ~ b => a + b.mkString }
  def row             = repsep( field, DELIMITER )
  def field           = space ~> quoted_field <~ space | raw_value
  def quoted_field    = rep1( QUOTE ~> quoted_value <~ QUOTE ) ^^ { _.mkString }
  def space           = rep( SPACER ) ^^^ ""
  def SPACER          = ' '
  def QUOTE           = '"'
  def escaped_quote   = QUOTE ~ QUOTE ^^^ QUOTE
  def quoted_value    = rep1( escaped_quote | not( QUOTE ) ~> any | EOL ) ^^ { _.mkString }
  def raw_value       = rep( not( DELIMITER ) ~> any ) ^^ { _.mkString }
  def any             = """.""".r
  def DELIMITER       = '\t'
  def EOL             = rep1( '\r'.? ~ '\n' ) ^^ { _ map {
      case Some(_) ~ _ => "\r\n"
      case _ => "\n"
    } mkString
  }
}