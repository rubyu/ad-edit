
package com.github.rubyu.wok

import org.specs2.mutable._


class TemplateTest extends SpecificationWithJUnit {

  sequential

  "Template.layout" should {
    "layout command with given arguments" in {
      val template = new Template("${field(0)}, ${media.dir}")
      template.layout(List[String]("a"), "media") mustEqual "a, media"
    }

    "ignore accesses for out-of-index fields" in {
      val template = new Template("${field(999)}")
      template.layout(List[String]("a"), "") mustEqual ""
    }

    "escape $ by \\" in {
      val template = new Template("\\${field(0)}")
      template.layout(List[String]("a"), "") mustEqual "${field(0)}"
    }

    "no to escape normal characters by \\" in {
      val template = new Template("\\a")
      template.layout(List[String]("a"), "") mustEqual "\\a"
    }

    "not to escape markup characters by $$" in {
      val template = new Template("<>, ${field(0)}, ${media.dir}")
      template.layout(List[String]("<>"), "<>") mustEqual "<>, <>, <>"
    }

    "import com.github.rubyu.adedit.Proc" in {
      val template = new Template("${ Proc(\"gnupack_basic-11.00\\\\app\\\\cygwin\\\\cygwin\\\\bin\\\\echo\",\"-n\", \"a\").exec() }")
      template.layout(List[String](), "") mustEqual "a"
    }
  }
}
