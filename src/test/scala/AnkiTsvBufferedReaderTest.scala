
package com.github.rubyu.adupdate

import org.specs2.mutable._
import org.specs2.specification.Scope
import java.io._
import java.nio.charset.StandardCharsets


class AnkiTsvBufferedReaderTest extends SpecificationWithJUnit {

  def ankiReader(str: String) = {
    val bytes = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8))
    new BufferedReader(new AnkiTsvBufferedReader(new InputStreamReader(bytes, StandardCharsets.UTF_8)))
  }

  "AnkiTsvBufferedReader.read" should {
    "filter a line startswith # when it is at the first line" in {
      val reader = ankiReader("#\na")
      reader.readLine() mustEqual "a"
      reader.readLine() mustEqual null
    }
    "filter a line startswith # when it is at the last line" in {
      val reader = ankiReader("a\n#\n")
      reader.readLine() mustEqual "a"
      reader.readLine() mustEqual null
    }
    "filter a line startswith # when it is at the middle of the lines" in {
      val reader = ankiReader("a\n#\nb")
      reader.readLine() mustEqual "a"
      reader.readLine() mustEqual "b"
      reader.readLine() mustEqual null
    }

    "filter a line of tags when it is at the first line" in {
      val reader = ankiReader("tags:")
      reader.readLine() mustEqual null
    }
    "not to filter a line of tags when it is at the middle of the lines" in {
      val reader = ankiReader("a\ntags:\nb")
      reader.readLine() mustEqual "a"
      reader.readLine() mustEqual "tags:"
      reader.readLine() mustEqual "b"
      reader.readLine() mustEqual null
    }
    "not to filter a line of tags when it is at the tast line" in {
      val reader = ankiReader("a\ntags:")
      reader.readLine() mustEqual "a"
      reader.readLine() mustEqual "tags:"
      reader.readLine() mustEqual null
    }

    "filter a line of tags when it is at the first line, after filter comments" in {
      val reader = ankiReader("#\ntags:")
      reader.readLine() mustEqual null
    }

    "return same size as input in large source" in {
      val reader = ankiReader("-" * 100000)
      reader.readLine() mustEqual "-" * 100000
    }
  }
}
