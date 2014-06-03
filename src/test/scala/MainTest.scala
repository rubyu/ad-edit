
package com.github.rubyu.adupdate

import org.specs2.mutable._
import java.nio.charset.StandardCharsets
import java.nio.file.{Paths, Files}
import java.io.File


class MainTest extends SpecificationWithJUnit {

  "Main.dropField" should {
    "drop a field of given position" in {
      Main.dropField(0)(List("a")) mustEqual List()
      Main.dropField(0)(List("a", "b")) mustEqual List("b")
      Main.dropField(1)(List("a")) mustEqual List("a")
      Main.dropField(1)(List("a", "b")) mustEqual List("a")
      Main.dropField(1)(List("a", "b", "c")) mustEqual List("a", "c")
    }
  }

  "Main.insertField" should {
    "insert a field to given position" in {
      Main.insertField(0, { row => "ins" })(List("a")) mustEqual List("ins", "a")
      Main.insertField(0, { row => "ins" })(List("a", "b")) mustEqual List("ins", "a", "b")
      Main.insertField(1, { row => "ins" })(List("a")) mustEqual List("a", "ins")
      Main.insertField(2, { row => "ins" })(List("a")) mustEqual List("a", "", "ins")
      Main.insertField(1, { row => "ins" })(List("a", "b")) mustEqual List("a", "ins", "b")
    }
  }

  "Main.updateField" should {
    "update a field of given position" in {
      Main.updateField(0, { row => "upd" })(List("a")) mustEqual List("upd")
      Main.updateField(0, { row => "upd" })(List("a", "b")) mustEqual List("upd", "b")
      Main.updateField(1, { row => "upd" })(List("a")) mustEqual List("a", "upd")
      Main.updateField(1, { row => "upd" })(List("a", "b")) mustEqual List("a", "upd")
    }
  }

  "Main.executeCommands" should {
    "return Array[Byte]" in {
      val template = new Template(List(List(
        "C:\\ad-tools\\gnupack_basic-11.00\\app\\cygwin\\cygwin\\bin\\echo", "${field(0)}")), "")
      Main.executeCommands(template, None)(List("hoge")) mustEqual "hoge\n".getBytes(StandardCharsets.UTF_8)
    }
  }
}
