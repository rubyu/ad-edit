
package com.github.rubyu.adedit

import org.specs2.mutable._
import org.specs2.specification.Scope


class AnkiTsvParserTest extends SpecificationWithJUnit {

  trait scope extends Scope {
    val parser = new AnkiTsvParser
  }


  "AnkiTsvParser.eol_res" should {
    "parse \\r" in new scope {
      val result = parser.parse(parser.eol_res, "\r")
      result.get mustEqual AnkiTsvParser.result.EOL("\r")
    }

    "parse \\n" in new scope {
      val result = parser.parse(parser.eol_res, "\n")
      result.get mustEqual AnkiTsvParser.result.EOL("\n")
    }

    "parse \\r\\n" in new scope {
      val result = parser.parse(parser.eol_res, "\r\n")
      result.get mustEqual AnkiTsvParser.result.EOL("\r\n")
    }
  }

  "AnkiTsvParser.raw_value_0" should {
    "return empty string value when input is empty" in new scope {
      val result = parser.parse(parser.raw_value_0, "")
      result.get mustEqual ""
    }

    "parse a character" in new scope {
      val result = parser.parse(parser.raw_value_0, "a")
      result.get mustEqual "a"
    }

    "parse characters" in new scope {
      val result = parser.parse(parser.raw_value_0, "abc")
      result.get mustEqual "abc"
    }

    "return empty string value when input is \\n" in new scope {
      val result = parser.parse(parser.raw_value_0, "\n")
      result.get mustEqual ""
    }

    "return empty string value when input is \\r" in new scope {
      val result = parser.parse(parser.raw_value_0, "\r")
      result.get mustEqual ""
    }

    "return empty string value when input is \\r\\n" in new scope {
      val result = parser.parse(parser.raw_value_0, "\r\n")
      result.get mustEqual ""
    }

    "parse until \\n" in new scope {
      val result = parser.parse(parser.raw_value_0, "a\nb")
      result.get mustEqual "a"
    }

    "parse until \\r" in new scope {
      val result = parser.parse(parser.raw_value_0, "a\rb")
      result.get mustEqual "a"
    }

    "parse until \\r\\n" in new scope {
      val result = parser.parse(parser.raw_value_0, "a\r\nb")
      result.get mustEqual "a"
    }
  }

  "AnkiTsvParser.raw_value_1" should {
    "return empty result value when input is empty" in new scope {
      val result = parser.parse(parser.raw_value_1, "")
      result.isEmpty must beTrue
    }

    "parse a character" in new scope {
      val result = parser.parse(parser.raw_value_1, "a")
      result.get mustEqual "a"
    }

    "parse characters" in new scope {
      val result = parser.parse(parser.raw_value_1, "abc")
      result.get mustEqual "abc"
    }

    "return empty result value when input starts with \\n" in new scope {
      val result = parser.parse(parser.raw_value_1, "\n")
      result.isEmpty must beTrue
    }

    "return empty result value when input starts with \\r\\n" in new scope {
      val result = parser.parse(parser.raw_value_1, "\r\n")
      result.isEmpty must beTrue
    }

    "parse until \\n" in new scope {
      val result = parser.parse(parser.raw_value_1, "a\nb")
      result.get mustEqual "a"
    }

    "parse until \\r\\n" in new scope {
      val result = parser.parse(parser.raw_value_1, "a\r\nb")
      result.get mustEqual "a"
    }
  }

  "AnkiTsvParser.quoted_value" should {
    "parse a character" in new scope {
      val result = parser.parse(parser.quoted_value, "a")
      result.get mustEqual "a"
    }

    "parse characters" in new scope {
      val result = parser.parse(parser.quoted_value, "abc")
      result.get mustEqual "abc"
    }

    "parse a double quote to a single quote" in new scope {
      val result = parser.parse(parser.quoted_value, "\"\"")
      result.get mustEqual "\""
    }

    "parse characters contains a double quote" in new scope {
      val result = parser.parse(parser.quoted_value, "a\"\"b")
      result.get mustEqual "a\"b"
    }

    "return empty string when input starts with \"" in new scope {
      val result = parser.parse(parser.quoted_value, "\"")
      result.get mustEqual ""
    }

    "allow \\n" in new scope {
      val result = parser.parse(parser.quoted_value, "\n")
      result.get mustEqual "\n"
    }

    "allow \\r\\n" in new scope {
      val result = parser.parse(parser.quoted_value, "\r\n")
      result.get mustEqual "\r\n"
    }

    "parse input contains \\n" in new scope {
      val result = parser.parse(parser.quoted_value, "a\nb")
      result.get mustEqual "a\nb"
    }

    "parse input contains \\r\\n" in new scope {
      val result = parser.parse(parser.quoted_value, "a\r\nb")
      result.get mustEqual "a\r\nb"
    }
  }


  "AnkiTsvParser.quoted_field" should {
    "return empty result when input does not starts with quote character" in new scope {
      val result = parser.parse(parser.quoted_field, "a")
      result.isEmpty must beTrue
    }

    "return empty result when input starts with quote character and breaks in the middle" in new scope {
      val result = parser.parse(parser.quoted_field, "\"a")
      result.isEmpty must beTrue
    }

    "parse input starts and ends with quote character" in new scope {
      val result = parser.parse(parser.quoted_field, "\"a\"")
      result.get mustEqual "a"
    }

    "parse input starts and ends with quote character until second quote character" in new scope {
      val result = parser.parse(parser.quoted_field, "\"a\"b")
      result.get mustEqual "a"
    }
  }

  "AnkiTsvParser.field_0" should {
    "parse quoted text" in new scope {
      val result = parser.parse(parser.field_0, "\"a\"")
      result.get mustEqual "a"
    }

    "parse quoted text with padding before it" in new scope {
      val result = parser.parse(parser.field_0, " \"a\"")
      result.get mustEqual "a"
    }

    "parse quoted text with padding after it" in new scope {
      val result = parser.parse(parser.field_0, "\"a\" ")
      result.get mustEqual "a"
    }

    "parse quoted text with paddings both before and after" in new scope {
      val result = parser.parse(parser.field_0, " \"a\" ")
      result.get mustEqual "a"
    }

    "parse raw-text" in new scope {
      val result = parser.parse(parser.field_0, "a")
      result.get mustEqual "a"
    }

    "parse raw-text with padding before it as raw-text" in new scope {
      val result = parser.parse(parser.field_0, " a")
      result.get mustEqual " a"
    }

    "parse raw-text with padding after it as raw-text" in new scope {
      val result = parser.parse(parser.field_0, "a ")
      result.get mustEqual "a "
    }

    "parse raw-text with paddings both before and after it as raw-text" in new scope {
      val result = parser.parse(parser.field_0, " a ")
      result.get mustEqual " a "
    }

    "return empty string when the first character is QUOTE" in new scope {
      val result = parser.parse(parser.field_0, "\"")
      result.get mustEqual ""
    }

    "parse text ends with single quote as raw-text" in new scope {
      val result = parser.parse(parser.field_0, "a\"")
      result.get mustEqual "a\""
    }

    "parse text contains single quote as raw-text" in new scope {
      val result = parser.parse(parser.field_0, "a\"b")
      result.get mustEqual "a\"b"
    }

    "return empty string when input starts with \\n" in new scope {
      val result = parser.parse(parser.field_0, "\na")
      result.get mustEqual ""
    }

    "return empty string when input starts with \\r\\n" in new scope {
      val result = parser.parse(parser.field_0, "\r\na")
      result.get mustEqual ""
    }

    "parse raw-text until \\n" in new scope {
      val result = parser.parse(parser.field_0, "a\nb")
      result.get mustEqual "a"
    }

    "parse raw-text until \\r\\n" in new scope {
      val result = parser.parse(parser.field_0, "a\r\nb")
      result.get mustEqual "a"
    }

    "parse quoted-text contains \n" in new scope {
      val result = parser.parse(parser.field_0, "\"a\nb\"")
      result.get mustEqual "a\nb"
    }

    "parse quoted-text contains \r\n" in new scope {
      val result = parser.parse(parser.field_0, "\"a\r\nb\"")
      result.get mustEqual "a\r\nb"
    }
  }


  "AnkiTsvParser.field_1" should {
    "parse quoted text" in new scope {
      val result = parser.parse(parser.field_1, "\"a\"")
      result.get mustEqual "a"
    }

    "parse quoted text with padding before it" in new scope {
      val result = parser.parse(parser.field_1, " \"a\"")
      result.get mustEqual "a"
    }

    "parse quoted text with padding after it" in new scope {
      val result = parser.parse(parser.field_1, "\"a\" ")
      result.get mustEqual "a"
    }

    "parse quoted text with paddings both before and after" in new scope {
      val result = parser.parse(parser.field_1, " \"a\" ")
      result.get mustEqual "a"
    }

    "parse raw-text" in new scope {
      val result = parser.parse(parser.field_1, "a")
      result.get mustEqual "a"
    }

    "parse raw-text with padding before it as raw-text" in new scope {
      val result = parser.parse(parser.field_1, " a")
      result.get mustEqual " a"
    }

    "parse raw-text with padding after it as raw-text" in new scope {
      val result = parser.parse(parser.field_1, "a ")
      result.get mustEqual "a "
    }

    "parse raw-text with paddings both before and after it as raw-text" in new scope {
      val result = parser.parse(parser.field_1, " a ")
      result.get mustEqual " a "
    }

    "return empty result when the first character is QUOTE" in new scope {
      val result = parser.parse(parser.field_1, "\"")
      result.isEmpty must beTrue
    }

    "parse text ends with single quote as raw-text" in new scope {
      val result = parser.parse(parser.field_1, "a\"")
      result.get mustEqual "a\""
    }

    "parse text contains single quote as raw-text" in new scope {
      val result = parser.parse(parser.field_1, "a\"b")
      result.get mustEqual "a\"b"
    }

    "return empty result when input starts with \\n" in new scope {
      val result = parser.parse(parser.field_1, "\na")
      result.isEmpty must beTrue
    }

    "return empty result when input starts with \\r\\n" in new scope {
      val result = parser.parse(parser.field_1, "\r\na")
      result.isEmpty must beTrue
    }

    "parse raw-text until \\n" in new scope {
      val result = parser.parse(parser.field_1, "a\nb")
      result.get mustEqual "a"
    }

    "parse raw-text until \\r\\n" in new scope {
      val result = parser.parse(parser.field_1, "a\r\nb")
      result.get mustEqual "a"
    }

    "parse quoted-text contains \\n" in new scope {
      val result = parser.parse(parser.field_1, "\"a\nb\"")
      result.get mustEqual "a\nb"
    }

    "parse quoted-text contains \\r\\n" in new scope {
      val result = parser.parse(parser.field_1, "\"a\r\nb\"")
      result.get mustEqual "a\r\nb"
    }
  }

  "AnkiTsvParser.row" should {
    "return empty result when input is empty" in new scope {
      val result = parser.parse(parser.row, "")
      result.isEmpty must beTrue
    }

    "return a list size is 1 when a raw-text is given" in new scope {
      val result = parser.parse(parser.row, "a")
      result.get mustEqual List("a")
    }

    "return a list size is 2 when tab separeted 2 raw-text are given" in new scope {
      val result = parser.parse(parser.row, "a\tb")
      result.get mustEqual List("a", "b")
    }

    "return a list size is 3 when tab separeted 3 raw-text are given" in new scope {
      val result = parser.parse(parser.row, "a\tb\tc")
      result.get mustEqual List("a", "b", "c")
    }

    "return a list size is 1 when a quoted-text is given" in new scope {
      val result = parser.parse(parser.row, "\"a\"")
      result.get mustEqual List("a")
    }

    "return a list size is 2 when tab separeted 2 quoted-text is given" in new scope {
      val result = parser.parse(parser.row, "\"a\"\t\"b\"")
      result.get mustEqual List("a", "b")
    }

    "return a list size is 2 when a tab is given" in new scope {
      val result = parser.parse(parser.row, "\t")
      result.get mustEqual List("", "")
    }

    "return a list size is 3 when 2 tabs are given" in new scope {
      val result = parser.parse(parser.row, "\t\t")
      result.get mustEqual List("", "", "")
    }

    "parse until the end of a row expression" in new scope {
      val result = parser.parse(parser.row, "\"a\"b")
      result.get mustEqual List("a")
      result.next.offset mustEqual 3
    }

  }

  "AnkiTsvParser.comment" should {
    "return empty result when input is empty" in new scope {
      val result = parser.parse(parser.comment_res, "")
      result.isEmpty must beTrue
    }

    "return empty result when input is raw-text" in new scope {
      val result = parser.parse(parser.comment_res, "a")
      result.isEmpty must beTrue
    }

    "parse a comment" in new scope {
      val result = parser.parse(parser.comment_res, "#")
      result.get mustEqual AnkiTsvParser.result.Comment("#")
    }

    "parse a comment until the end" in new scope {
      val result = parser.parse(parser.comment_res, "#a")
      result.get mustEqual AnkiTsvParser.result.Comment("#a")
    }

    "parse a comment until until \n" in new scope {
      val result = parser.parse(parser.comment_res, "#\n")
      result.get mustEqual AnkiTsvParser.result.Comment("#")
    }

    "parse a comment until until \r\n" in new scope {
      val result = parser.parse(parser.comment_res, "#\r\n")
      result.get mustEqual AnkiTsvParser.result.Comment("#")
    }
  }

  "AnkiTsvParser.tags" should {
    "return empty result when input is empty" in new scope {
      val result = parser.parse(parser.tags, "")
      result.isEmpty must beTrue
    }

    "return empty result when input is raw-text" in new scope {
      val result = parser.parse(parser.tags, "a")
      result.isEmpty must beTrue
    }

    "parse a tags" in new scope {
      val result = parser.parse(parser.tags, "tags:")
      result.get mustEqual "tags:"
    }

    "parse a tags until the end" in new scope {
      val result = parser.parse(parser.tags, "tags: ")
      result.get mustEqual "tags: "
    }

    "parse a tags until \n" in new scope {
      val result = parser.parse(parser.tags, "tags:\n")
      result.get mustEqual "tags:"
    }

    "parse a tags until \r\n" in new scope {
      val result = parser.parse(parser.tags, "tags:\r\n")
      result.get mustEqual "tags:"
    }
  }

  "AnkiTsvParser.first_line" should {
    "return Tags when tags expression is given" in new scope {
      val result = parser.parse(parser.first_line, "tags:")
      result.get mustEqual AnkiTsvParser.result.Tags("tags:")
    }

    "return Tags when tags expression is given and ends with \\n" in new scope {
      val result = parser.parse(parser.first_line, "tags:\n")
      result.get mustEqual AnkiTsvParser.result.Tags("tags:")
    }

    "return Tags when tags expression is given and ends with \\r\\n" in new scope {
      val result = parser.parse(parser.first_line, "tags:\r\n")
      result.get mustEqual AnkiTsvParser.result.Tags("tags:")
    }

    "return Comment when comment expression is given" in new scope {
      val result = parser.parse(parser.first_line, "#")
      result.get mustEqual AnkiTsvParser.result.Comment("#")
    }

    "return Comment when comment expression is given and ends with \\n" in new scope {
      val result = parser.parse(parser.first_line, "#\n")
      result.get mustEqual AnkiTsvParser.result.Comment("#")
    }

    "return Comment when comment expression is given and ends with \\r\\n" in new scope {
      val result = parser.parse(parser.first_line, "#\r\n")
      result.get mustEqual AnkiTsvParser.result.Comment("#")
    }

    "return Row when row expression is given" in new scope {
      val result = parser.parse(parser.first_line, "a")
      result.get mustEqual AnkiTsvParser.result.Row(List("a"))
    }

    "return Row when row expression is given and ends with \\n" in new scope {
      val result = parser.parse(parser.first_line, "a\n")
      result.get mustEqual AnkiTsvParser.result.Row(List("a"))
    }

    "return Row when row expression is given and ends with \\r\\n" in new scope {
      val result = parser.parse(parser.first_line, "a\r\n")
      result.get mustEqual AnkiTsvParser.result.Row(List("a"))
    }
  }

  "AnkiTsvParser.line" should {
    "return Row when tags expression is given" in new scope {
      val result = parser.parse(parser.line, "tags:")
      result.get mustEqual AnkiTsvParser.result.Row(List("tags:"))
    }

    "return Row when tags expression is given and ends with \\n" in new scope {
      val result = parser.parse(parser.line, "tags:\n")
      result.get mustEqual AnkiTsvParser.result.Row(List("tags:"))
    }

    "return Row when tags expression is given and ends with \\r\\n" in new scope {
      val result = parser.parse(parser.line, "tags:\r\n")
      result.get mustEqual AnkiTsvParser.result.Row(List("tags:"))
    }

    "return Comment when comment expression is given" in new scope {
      val result = parser.parse(parser.line, "#")
      result.get mustEqual AnkiTsvParser.result.Comment("#")
    }

    "return Comment when comment expression is given and ends with \\n" in new scope {
      val result = parser.parse(parser.line, "#\n")
      result.get mustEqual AnkiTsvParser.result.Comment("#")
    }

    "return Comment when comment expression is given and ends with \\r\\n" in new scope {
      val result = parser.parse(parser.line, "#\r\n")
      result.get mustEqual AnkiTsvParser.result.Comment("#")
    }

    "return Row when row expression is given" in new scope {
      val result = parser.parse(parser.line, "a")
      result.get mustEqual AnkiTsvParser.result.Row(List("a"))
    }

    "return Row when row expression is given and ends with \\n" in new scope {
      val result = parser.parse(parser.line, "a\n")
      result.get mustEqual AnkiTsvParser.result.Row(List("a"))
    }

    "return Row when row expression is given and ends with \\r\\n" in new scope {
      val result = parser.parse(parser.line, "a\r\n")
      result.get mustEqual AnkiTsvParser.result.Row(List("a"))
    }
  }


}
