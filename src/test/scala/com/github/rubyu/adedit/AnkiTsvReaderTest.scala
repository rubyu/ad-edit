
package com.github.rubyu.adupdate

import org.specs2.mutable._
import java.io.StringReader
import AnkiTsvParser.result._


class AnkiTsvReaderTest extends SpecificationWithJUnit {

  "AnkiTsvReader" should {
    "parse raw-text" in {
      val result = new AnkiTsvReader(new StringReader("a"))
      result.toList mustEqual List(Row(List("a")))
    }

    "parse quoted-text" in {
      val result = new AnkiTsvReader(new StringReader("\"a\""))
      result.toList mustEqual List(Row(List("a")))
    }

    "parse \\n" in {
      val result = new AnkiTsvReader(new StringReader("\n"))
      result.toList mustEqual List()
    }

    "parse \\r\\n" in {
      val result = new AnkiTsvReader(new StringReader("\r\n"))
      result.toList mustEqual List()
    }

    "parse comment" in {
      val result = new AnkiTsvReader(new StringReader("#"))
      result.toList mustEqual List(Comment("#"))
    }

    "parse tags" in {
      val result = new AnkiTsvReader(new StringReader("tags:"))
      result.toList mustEqual List(Tags("tags:"))
    }

    "parse quoted-text contains \\n" in {
      val result = new AnkiTsvReader(new StringReader("\"a\nb\""))
      result.toList mustEqual List(Row(List("a\nb")))
    }

    "parse raw-text, DELIM, raw-text" in {
      val result = new AnkiTsvReader(new StringReader("a\tb"))
      result.toList mustEqual List(Row(List("a", "b")))
    }

    "parse raw-text, DELIM, raw-test, DELIM, raw-text" in {
      val result = new AnkiTsvReader(new StringReader("a\tb\tc"))
      result.toList mustEqual List(Row(List("a", "b", "c")))
    }

    "parse raw-text, \\n, raw-text" in {
      val result = new AnkiTsvReader(new StringReader("a\nb"))
      result.toList mustEqual List(Row(List("a")), Row(List("b")))
    }

    "parse raw-text, \n" in {
      val result = new AnkiTsvReader(new StringReader("a\n"))
      result.toList mustEqual List(Row(List("a")))
    }

    "parse raw-text, \r\n" in {
      val result = new AnkiTsvReader(new StringReader("a\r\n"))
      result.toList mustEqual List(Row(List("a")))
    }

    "parse un-closed quote-text as InvalidString" in {
      val result = new AnkiTsvReader(new StringReader("\"a"))
      result.toList mustEqual List(InvalidString("\"a"))
    }

    "parse a row that has un-closed quote-text as row, InvalidString" in {
      val result = new AnkiTsvReader(new StringReader("a\t\"b"))
      result.toList mustEqual List(InvalidString("a\t\"b"))
    }

    "return empty input when empty input is given" in {
      val result = new AnkiTsvReader(new StringReader(""))
      result.toList mustEqual List()
    }

  }
  "AnkiTsvReader.lastSuccess" should {
    "return None when input is empty" in {
      val result = new AnkiTsvReader(new StringReader(""))
      result.toList
      result.lastSuccess mustEqual None
    }

    "return Some(1) when input is line" in {
      val result = new AnkiTsvReader(new StringReader("a"))
      result.toList
      result.lastSuccess mustEqual Some(0)
    }

    "return Some(2) when input is line, \\n, line" in {
      val result = new AnkiTsvReader(new StringReader("a\nb"))
      result.toList
      result.lastSuccess mustEqual Some(1)
    }

    "return None when input is broken-line" in {
      val result = new AnkiTsvReader(new StringReader("\"b"))
      result.toList
      result.lastSuccess mustEqual None
    }

    "return Some(0) when input is line, broken-line" in {
      val result = new AnkiTsvReader(new StringReader("a\n\"b"))
      result.toList mustEqual List(Row(List("a")), InvalidString("\"b"))
      result.lastSuccess mustEqual Some(0)
    }
  }
}
