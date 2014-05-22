
package com.github.rubyu.adupdate

import org.specs2.mutable._
import org.specs2.specification.Scope
import java.io._


class TsvUpdaterTest extends SpecificationWithJUnit {

  trait scope extends Scope {
    val updater = new TsvUpdater
    val output = new ByteArrayOutputStream

    def input(str: String) = new ByteArrayInputStream(str.getBytes)
    def outputStr = output.toString("utf-8")
  }

  "TsvUpdater.update" should {
    "support unicode" in new scope {
      updater.update(input(Array(
        "あ").mkString(System.lineSeparator)), output) { arr => arr }
      outputStr mustEqual(Array(
        "あ",
        "") mkString(System.lineSeparator))
    }

    "do nothing when empty line" in new scope {
      updater.update(input(Array(
        "a",
        "",
        "b").mkString(System.lineSeparator)), output) { arr => arr }
      outputStr mustEqual(Array(
        "a",
        "",
        "b",
        "").mkString(System.lineSeparator))
    }

    "add no line separator at last line when empty" in new scope {
      updater.update(input(Array(
        "").mkString(System.lineSeparator)), output) { arr => arr }
      outputStr mustEqual(Array(
        "").mkString(System.lineSeparator))
    }

    "add line separator at last line when content exits" in new scope {
      updater.update(input(Array(
        "a").mkString(System.lineSeparator)), output) { arr => arr }
      outputStr mustEqual(Array(
        "a",
        "").mkString(System.lineSeparator))
    }

    "update row by given function when empty content" in new scope {
      updater.update(input(Array(
        "\t").mkString(System.lineSeparator)), output) { _ map { c => "1" } }
      outputStr mustEqual(Array(
        "1\t1",
        "").mkString(System.lineSeparator))
    }

    "update row by given function when single line" in new scope {
      updater.update(input(Array(
        "a\tb").mkString(System.lineSeparator)), output) { _ map { c => "1" } }
      outputStr mustEqual(Array(
        "1\t1",
        "").mkString(System.lineSeparator))
    }

    "update row by given function when multiple line" in new scope {
      updater.update(input(Array(
        "a\tb",
        "c\td").mkString(System.lineSeparator)), output) { _ map { c => "1" } }
      outputStr mustEqual(Array(
        "1\t1",
        "1\t1",
        "").mkString(System.lineSeparator))
    }

    "escape quotechar when content has lineSeparator" in new scope {
      updater.update(input(Array(
        "\"a",
        "",
        "b\"").mkString(System.lineSeparator)), output) { _ map { c => "1" } }
      outputStr mustEqual(Array(
        "1",
        "") mkString(System.lineSeparator))
    }

    "escape quotechar when content has tab" in new scope {
      updater.update(input(Array(
        "\"a",
        "\tb\"").mkString(System.lineSeparator)), output) { _ map { c => "1" } }
      outputStr mustEqual(Array(
        "1",
        "") mkString(System.lineSeparator))
    }

    "escape quotechar when content has tab and lineSeparator" in new scope {
      updater.update(input(Array(
        "\"a",
        "",
        "\t",
        "",
        "b\"").mkString(System.lineSeparator)), output) { _ map { c => "1" } }
      outputStr mustEqual(Array(
        "1",
        "") mkString(System.lineSeparator))
    }

    "escape quotechar" in new scope {
      updater.update(input(Array(
        "\"a\"\"b\"\tc").mkString(System.lineSeparator)), output) { _ map { c => c.replace("\"", "|") } }
      outputStr mustEqual(Array(
        "a|b\tc",
        "") mkString(System.lineSeparator))
    }

    "support variable columns" in new scope {
      updater.update(input(Array(
        "a\tb\tc",
        "d\te",
        "f\tg\th\ti").mkString(System.lineSeparator)), output) { _ map { c => "1" } }
      outputStr mustEqual(Array(
        "1\t1\t1",
        "1\t1",
        "1\t1\t1\t1",
        "") mkString(System.lineSeparator))
    }

    "ignore comment" in new scope {
      updater.update(input(Array(
        "#").mkString(System.lineSeparator)), output) { arr => arr }
      outputStr mustEqual(Array(
        "").mkString(System.lineSeparator))
    }

    "ignore succesive comments" in new scope {
      updater.update(input(Array(
        "#",
        "#").mkString(System.lineSeparator)), output) { arr => arr }
      outputStr mustEqual(Array(
        "").mkString(System.lineSeparator))
    }

    "ignore not succesive comments" in new scope {
      updater.update(input(Array(
        "#",
        "",
        "#").mkString(System.lineSeparator)), output) { arr => arr }
      outputStr mustEqual(Array(
        "",
        "").mkString(System.lineSeparator))
    }

    "bypass tags expression with no TSV elements" in new scope {
      updater.update(input(Array(
        "tags: a b").mkString(System.lineSeparator)), output) { arr => arr }
      outputStr mustEqual(Array(
        "tags: a b",
        "").mkString(System.lineSeparator))
    }

    "bypass tags expression after comment" in new scope {
      updater.update(input(Array(
        "#",
        "tags: a b").mkString(System.lineSeparator)), output) { arr => arr }
      outputStr mustEqual(Array(
        "tags: a b",
        "").mkString(System.lineSeparator))
    }

    "bypass tags expression after comment" in new scope {
      updater.update(input(Array(
        "tags: a b",
        "a").mkString(System.lineSeparator)), output) { arr => arr }
      outputStr mustEqual(Array(
        "tags: a b",
        "a",
        "").mkString(System.lineSeparator))
    }
  }
}
