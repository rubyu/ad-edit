
package com.github.rubyu.wok

import org.specs2.mutable._
import org.specs2.specification.Scope
import java.io._


class TsvUpdaterTest extends SpecificationWithJUnit {

  sequential

  trait scope extends Scope {
    val _stderr = System.err
    val stderr = new ByteArrayOutputStream
    System.setErr(new PrintStream(new BufferedOutputStream(stderr), true, "utf-8"))

    def after {
      System.setErr(_stderr)
    }

    val updater = new TsvUpdater
    val output = new ByteArrayOutputStream

    def input(str: String) = new ByteArrayInputStream(str.getBytes("utf-8"))
    def outputStr = output.toString("utf-8")
  }

  "TsvUpdater.update" should {
    "ignore invalid string" in new scope {
      updater.update(input(List(
        "\"").mkString("\r\n")), output) { arr => arr }
      stderr.toString("utf-8") mustEqual("")
    }

    "output a character as is" in new scope {
      updater.update(input(List(
        "あ").mkString("\r\n")), output) { arr => arr }
      outputStr mustEqual(List(
        "あ",
        "") mkString("\r\n"))
    }

    "ignore empty lines" in new scope {
      updater.update(input(List(
        "a",
        "",
        "b").mkString("\r\n")), output) { arr => arr }
      outputStr mustEqual(List(
        "a",
        "b",
        "").mkString("\r\n"))
    }

    "output nothing when the input is empty" in new scope {
      updater.update(input(List(
        "").mkString("\r\n")), output) { arr => arr }
      outputStr mustEqual(List(
        "").mkString("\r\n"))
    }

    "output a line separator at the end when content exits" in new scope {
      updater.update(input(List(
        "a").mkString("\r\n")), output) { arr => arr }
      outputStr mustEqual(List(
        "a",
        "").mkString("\r\n"))
    }

    "update row by given function when empty content" in new scope {
      updater.update(input(List(
        "\t").mkString("\r\n")), output) { _ map { c => "1" } }
      outputStr mustEqual(List(
        "1\t1",
        "").mkString("\r\n"))
    }

    "update row by given function when single line" in new scope {
      updater.update(input(List(
        "a\tb").mkString("\r\n")), output) { _ map { c => "1" } }
      outputStr mustEqual(List(
        "1\t1",
        "").mkString("\r\n"))
    }

    "update row by given function when multiple line" in new scope {
      updater.update(input(List(
        "a\tb",
        "c\td").mkString("\r\n")), output) { _ map { c => "1" } }
      outputStr mustEqual(List(
        "1\t1",
        "1\t1",
        "").mkString("\r\n"))
    }

    "escape quotechar when content has lineSeparator" in new scope {
      updater.update(input(List(
        "\"a",
        "",
        "b\"").mkString("\r\n")), output) { _ map { c => "1" } }
      outputStr mustEqual(List(
        "1",
        "") mkString("\r\n"))
    }

    "escape quotechar when content has tab" in new scope {
      updater.update(input(List(
        "\"a",
        "\tb\"").mkString("\r\n")), output) { _ map { c => "1" } }
      outputStr mustEqual(List(
        "1",
        "") mkString("\r\n"))
    }

    "escape quotechar when content has tab and lineSeparator" in new scope {
      updater.update(input(List(
        "\"a",
        "",
        "\t",
        "",
        "b\"").mkString("\r\n")), output) { _ map { c => "1" } }
      outputStr mustEqual(List(
        "1",
        "") mkString("\r\n"))
    }

    "escape quotechar" in new scope {
      updater.update(input(List(
        "\"a\"\"b\"\tc").mkString("\r\n")), output) { _ map { c => c.replace("\"", "|") } }
      outputStr mustEqual(List(
        "a|b\tc",
        "") mkString("\r\n"))
    }

    "support variable columns" in new scope {
      updater.update(input(List(
        "a\tb\tc",
        "d\te",
        "f\tg\th\ti").mkString("\r\n")), output) { _ map { c => "1" } }
      outputStr mustEqual(List(
        "1\t1\t1",
        "1\t1",
        "1\t1\t1\t1",
        "") mkString("\r\n"))
    }
  }
}
