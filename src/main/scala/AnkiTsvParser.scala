
package com.github.rubyu.adupdate

import util.parsing.combinator.RegexParsers


object AnkiTsvParser {
  object result {
    trait Element
    case class Tags(value: String) extends Element
    case class Comment(value: String) extends Element
    case class Row(value: List[String]) extends Element
    case class EOL(value: String) extends Element
    case class InvalidString(value: String) extends Element
  }
}

class AnkiTsvParser extends RegexParsers {
  override def skipWhitespace = false

  //入力にマッチし、余りがないこと。
  def first_line      = phrase( ( tags_res | comment_res | row_res ) <~ EOL.? | eol_res )
  def line            =            phrase( ( comment_res | row_res ) <~ EOL.? | eol_res )

  def tags_res        = tags    ^^ { AnkiTsvParser.result.Tags(_) }
  def comment_res     = comment ^^ { AnkiTsvParser.result.Comment(_) }
  def row_res         = row     ^^ { AnkiTsvParser.result.Row(_) }
  def eol_res         = EOL     ^^ { AnkiTsvParser.result.EOL(_) }

  def tags            = "tags:" ~ rep( CHAR ) ^^ { case a ~ b => a + b.mkString }
  def comment         = "#"     ~ rep( CHAR ) ^^ { case a ~ b => a + b.mkString }

  //サイズが1の要素が空でない列か、サイズが2以上の要素が空でもよい列。
  def row             = row_0 | row_1

  //サイズが2以上の列。フィールドが空になってもよい。
  def row_0           = field_0 ~ DELIM ~ rep1sep( field_0, DELIM ) ^^ { case head ~ _ ~ tail => head :: tail }
  //サイズが1の列。フィールドが空になってはいけない。
  def row_1           = field_1 ^^ { List(_) }

  //長さ0以上
  def field_0         = quoted_field | raw_value_0
  //長さ1以上
  def field_1         = quoted_field | raw_value_1

  //QUOTEに囲まれていること。前後にスペースによるパディングが存在してもよい。
  def quoted_field    = padding ~> QUOTE ~> quoted_value <~ QUOTE <~ padding
  //(QUOTE以外、ダブルクォート、改行)からなる長さ0以上の文字列
  def quoted_value    = rep( escaped_quote | not_quote | EOL ) ^^ { _.mkString }

  //QUOTE, DELIM以外から開始し、DELIM以外が後続する、長さ0以上の文字列。
  def raw_value_0     = (not_quote_and_delim ~ rep( not_delim )).? ^^ { case Some(head ~ tail) => head :: tail mkString; case None => "" }
  //QUOTE, DELIM以外から開始し、DELIM以外が後続する、長さ1以上の文字列。
  def raw_value_1     =  not_quote_and_delim ~ rep( not_delim ) ^^ { case head ~ tail => head :: tail mkString }

  def padding         = rep( SPACE )
  def escaped_quote   = QUOTE ~ QUOTE ^^^ QUOTE
  def not_quote       = not( QUOTE ) ~> CHAR
  def not_delim       = not( DELIM ) ~> CHAR
  def not_quote_and_delim  = not( guard(QUOTE) | DELIM ) ~> CHAR

  def CHAR            = ".".r
  def SPACE           = ' '
  def QUOTE           = '"'
  def DELIM           = '\t'
  def EOL             = "\r\n" | "\n"
}