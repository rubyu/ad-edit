
package com.github.rubyu.adupdate

import org.specs2.mutable._
import org.specs2.specification.Scope


class AnkiParserTest extends SpecificationWithJUnit {

  trait scope extends Scope {
    val parser = new AnkiParser
  }

  "AnkiParser.raw_value" should {
    "return empty string value when input is empty" in new scope {
      val result = parser.parse(parser.raw_value, "")
      result.get mustEqual ""
    }

    "parse a character" in new scope {
      val result = parser.parse(parser.raw_value, "a")
      result.get mustEqual "a"
    }

    "parse characters" in new scope {
      val result = parser.parse(parser.raw_value, "abc")
      result.get mustEqual "abc"
    }

    "return empty string value when input starts with \\n" in new scope {
      val result = parser.parse(parser.raw_value, "\n")
      result.get mustEqual ""
    }

    "return empty string value when input starts with \\r\\n" in new scope {
      val result = parser.parse(parser.raw_value, "\r\n")
      result.get mustEqual ""
    }

    "parse until \\n" in new scope {
      val result = parser.parse(parser.raw_value, "a\nb")
      result.get mustEqual "a"
    }

    "parse until \\r\\n" in new scope {
      val result = parser.parse(parser.raw_value, "a\r\nb")
      result.get mustEqual "a"
    }
  }

  "AnkiParser.quoted_value" should {
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

    "return empty result when input starts with \"" in new scope {
      val result = parser.parse(parser.quoted_value, "\"")
      result.isEmpty must beTrue
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


  "AnkiParser.quoted_field" should {
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

  "AnkiParser.field" should {
    "parse quoted text" in new scope {
      val result = parser.parse(parser.field, "\"a\"")
      result.get mustEqual "a"
    }

    "parse quoted text with padding before it" in new scope {
      val result = parser.parse(parser.field, " \"a\"")
      result.get mustEqual "a"
    }

    "parse quoted text with padding after it" in new scope {
      val result = parser.parse(parser.field, "\"a\" ")
      result.get mustEqual "a"
    }

    "parse quoted text with paddings both before and after" in new scope {
      val result = parser.parse(parser.field, " \"a\" ")
      result.get mustEqual "a"
    }

    "parse raw-text" in new scope {
      val result = parser.parse(parser.field, "a")
      result.get mustEqual "a"
    }

    "parse raw-text with padding before it as raw-text" in new scope {
      val result = parser.parse(parser.field, " a")
      result.get mustEqual " a"
    }

    "parse raw-text with padding after it as raw-text" in new scope {
      val result = parser.parse(parser.field, "a ")
      result.get mustEqual "a "
    }

    "parse raw-text with paddings both before and after it as raw-text" in new scope {
      val result = parser.parse(parser.field, " a ")
      result.get mustEqual " a "
    }

    "parse single quote as raw-text" in new scope {
      val result = parser.parse(parser.field, "\"")
      result.get mustEqual "\""
    }

    "parse text ends with single quote as raw-text" in new scope {
      val result = parser.parse(parser.field, "a\"")
      result.get mustEqual "a\""
    }

    "parse text starts with single quote as raw-text" in new scope {
      val result = parser.parse(parser.field, "\"a")
      result.get mustEqual "\"a"
    }

    "parse text contains single quote as raw-text" in new scope {
      val result = parser.parse(parser.field, "a\"b")
      result.get mustEqual "a\"b"
    }

    "return empty string when input starts with \\n" in new scope {
      val result = parser.parse(parser.field, "\na")
      result.get mustEqual ""
    }

    "return empty string when input starts with \\r\\n" in new scope {
      val result = parser.parse(parser.field, "\r\na")
      result.get mustEqual ""
    }

    "parse raw-text until \\n" in new scope {
      val result = parser.parse(parser.field, "a\nb")
      result.get mustEqual "a"
    }

    "parse raw-text until \\r\\n" in new scope {
      val result = parser.parse(parser.field, "a\r\nb")
      result.get mustEqual "a"
    }

    "parse quoted-text contains \n" in new scope {
      val result = parser.parse(parser.field, "\"a\nb\"")
      result.get mustEqual "a\nb"
    }

    "parse quoted-text contains \r\n" in new scope {
      val result = parser.parse(parser.field, "\"a\r\nb\"")
      result.get mustEqual "a\r\nb"
    }
  }

  "AnkiParser.row" should {
    "return a list contains a empty string when input is empty" in new scope {
      val result = parser.parse(parser.row, "")
      result.get mustEqual List("")
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

    "return a list size is 3 when tab separeted 3 quoted-text is given" in new scope {
      val result = parser.parse(parser.row, "\"a\"\t\"b\"\t\"c\"")
      result.get mustEqual List("a", "b", "c")
    }
  }

  "AnkiParser.comment" should {
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
      result.get mustEqual AnkiParser.result.Comment("#")
    }

    "parse a comment until the end" in new scope {
      val result = parser.parse(parser.comment_res, "#a")
      result.get mustEqual AnkiParser.result.Comment("#a")
    }

    "parse a comment until until \n" in new scope {
      val result = parser.parse(parser.comment_res, "#\n")
      result.get mustEqual AnkiParser.result.Comment("#")
    }

    "parse a comment until until \r\n" in new scope {
      val result = parser.parse(parser.comment_res, "#\r\n")
      result.get mustEqual AnkiParser.result.Comment("#")
    }
  }

  "AnkiParser.tags" should {
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
}
