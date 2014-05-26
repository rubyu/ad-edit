
package com.github.rubyu.adupdate

import org.specs2.mutable._


class TemplateTest extends SpecificationWithJUnit {
  "Template.layout" should {
    "layout commands with given arguments" in {
      Template.layout(
        List("${field(0)}, ${media.dir}"),
        Array[String]("a"),
        "media") mustEqual List("a, media")
    }

    "ignore accesses for out-of-index fields" in {
      Template.layout(
        List("${field(999)}"),
        Array[String]("a"),
        "") mustEqual List("")
    }

    "escape $ by \\" in {
      Template.layout(
        List("\\${field(0)}"),
        Array[String]("a"),
        "") mustEqual List("${field(0)}")
    }

    "no to escape normal characters by \\" in {
      Template.layout(
        List("\\a"),
        Array[String]("a"),
        "") mustEqual List("\\a")
    }

    "not to escape markup characters by $$" in {
      Template.layout(
        List("<>, ${field(0)}, ${media.dir}"),
        Array[String]("<>"),
        "<>") mustEqual List("<>, <>, <>")
    }
  }
}
