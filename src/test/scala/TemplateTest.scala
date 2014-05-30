
package com.github.rubyu.adupdate

import org.specs2.mutable._


class TemplateTest extends SpecificationWithJUnit {

  sequential

  "Template.layout" should {
    "layout command with given arguments" in {
      val template = new Template(List(List("${field(0)}, ${media.dir}")))
      template.layout(List[String]("a"), "media") mustEqual List(List("a, media"))
    }

    "ignore accesses for out-of-index fields" in {
      val template = new Template(List(List("${field(999)}")))
      template.layout(List[String]("a"), "") mustEqual List(List(""))
    }

    "escape $ by \\" in {
      val template = new Template(List(List("\\${field(0)}")))
      template.layout(List[String]("a"), "") mustEqual List(List("${field(0)}"))
    }

    "no to escape normal characters by \\" in {
      val template = new Template(List(List("\\a")))
      template.layout(List[String]("a"), "") mustEqual List(List("\\a"))
    }

    "not to escape markup characters by $$" in {
      val template = new Template(List(List("<>, ${field(0)}, ${media.dir}")))
      template.layout(List[String]("<>"), "<>") mustEqual List(List("<>, <>, <>"))
    }
  }
}
