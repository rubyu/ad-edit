
package com.github.rubyu.adupdate

import org.specs2.mutable._
import org.specs2.specification.Scope


class AnkiParserTest extends SpecificationWithJUnit {

  trait scope extends Scope {
    val parser = new AnkiParser
  }

  "AnkiParser.raw_value" should {
    "parse empty input" in new scope {
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

    "parse \\n" in new scope {
      val result = parser.parse(parser.raw_value, "\n")
      result.get mustEqual ""
    }

    "parse \\n" in new scope {
      val result = parser.parse(parser.raw_value, "a\nb")
      result.get mustEqual "a"
    }
  }

  "AnkiParser.quoted_value" should {
    "1" in new scope {
      val result = parser.parse(parser.quoted_value, "a")
      result.get mustEqual "a"
    }

    "2" in new scope {
      val result = parser.parse(parser.quoted_value, "\"\"")
      result.get mustEqual "\""
    }

    "parse characters" in new scope {
      val result = parser.parse(parser.quoted_value, "a\"\"b")
      result.get mustEqual "a\"b"
    }

    "error when " in new scope {
      val result = parser.parse(parser.quoted_value, "\"")
      result.isEmpty must beTrue
    }
  }


  "AnkiParser.quoted_field" should {
    "1" in new scope {
      val result = parser.parse(parser.quoted_field, "\"a\"")
      result.get mustEqual "a"
    }
  }

  "AnkiParser.field" should {
    "1" in new scope {
      val result = parser.parse(parser.field, "\"a\"")
      result.get mustEqual "a"
    }

    "2" in new scope {
      val result = parser.parse(parser.field, " \"a\"")
      result.get mustEqual "a"
    }

    "3" in new scope {
      val result = parser.parse(parser.field, "\"a\" ")
      result.get mustEqual "a"
    }

    "4" in new scope {
      val result = parser.parse(parser.field, " \"a\" ")
      result.get mustEqual "a"
    }

    "6" in new scope {
      val result = parser.parse(parser.field, " a")
      result.get mustEqual " a"
    }

    "7" in new scope {
      val result = parser.parse(parser.field, "a ")
      result.get mustEqual "a "
    }

    "8" in new scope {
      val result = parser.parse(parser.field, " a ")
      result.get mustEqual " a "
    }

    "9" in new scope {
      val result = parser.parse(parser.field, "\"")
      result.get mustEqual "\""
    }

    "10" in new scope {
      val result = parser.parse(parser.field, "\"a\nb\"")
      result.get mustEqual "a\nb"
    }

    "11" in new scope {
      val result = parser.parse(parser.field, "a\nb")
      result.get mustEqual "a"
    }
  }

  "AnkiParser.row" should {
    "1" in new scope {
      val result = parser.parse(parser.row, "a")
      result.get mustEqual List("a")
    }

    "2" in new scope {
      val result = parser.parse(parser.row, "a\tb")
      result.get mustEqual List("a", "b")
    }

    "3" in new scope {
      val result = parser.parse(parser.row, "a\tb\tc")
      result.get mustEqual List("a", "b", "c")
    }

    "1" in new scope {
      val result = parser.parse(parser.row, "\"a\"")
      result.get mustEqual List("a")
    }

    "2" in new scope {
      val result = parser.parse(parser.row, "\"a\"\t\"b\"")
      result.get mustEqual List("a", "b")
    }

    "3" in new scope {
      val result = parser.parse(parser.row, "\"a\"\t\"b\"\t\"c\"")
      result.get mustEqual List("a", "b", "c")
    }

  }

  "AnkiParser.coment" should {
    "1" in new scope {
      val result = parser.parse(parser.comment, "")
      result.isEmpty must beTrue
    }

    "2" in new scope {
      val result = parser.parse(parser.comment, "a")
      result.isEmpty must beTrue
    }

    "3" in new scope {
      val result = parser.parse(parser.comment, "#")
      result.get mustEqual "#"
    }

    "1" in new scope {
      val result = parser.parse(parser.comment, "# ")
      result.get mustEqual "# "
    }

    "2" in new scope {
      val result = parser.parse(parser.comment, "#\n")
      result.get mustEqual "#"
    }

    "3" in new scope {
      val result = parser.parse(parser.comment, "# \n")
      result.get mustEqual "# "
    }

  }

  "AnkiParser.tags" should {
    "1" in new scope {
      val result = parser.parse(parser.tags, "")
      result.isEmpty must beTrue
    }

    "2" in new scope {
      val result = parser.parse(parser.tags, "a")
      result.isEmpty must beTrue
    }

    "3" in new scope {
      val result = parser.parse(parser.tags, "tags:")
      result.get mustEqual "tags:"
    }

    "1" in new scope {
      val result = parser.parse(parser.tags, "tags: ")
      result.get mustEqual "tags: "
    }

    "2" in new scope {
      val result = parser.parse(parser.tags, "tags:\n")
      result.get mustEqual "tags:"
    }

    "3" in new scope {
      val result = parser.parse(parser.tags, "tags: \n")
      result.get mustEqual "tags: "
    }

  }


}
