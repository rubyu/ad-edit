
package com.github.rubyu.wok

import org.specs2.mutable._
import org.fusesource.scalate.CompilerException


class TemplateTest extends SpecificationWithJUnit {

  sequential

  "Template.layout" should {
    "row can be used" in {
      val template = new Template("${row(0)}", List[String](), "")
      template.layout(List[String]("a")) mustEqual "a"
    }

    "media can be used" in {
      val template = new Template("${media.dir}", List[String](), "a")
      template.layout(List[String]()) mustEqual "a"
    }

    "arg can be used" in {
      val template = new Template("${arg(0)}", List[String]("a"), "")
      template.layout(List[String]()) mustEqual "a"
    }

    "throw CompilerException" in {
      new Template("${ var_not_defined }", List[String]("a"), "") must throwA[CompilerException]
    }

    "escape $ by \\" in {
      val template = new Template("\\${field(0)}", List[String](), "")
      template.layout(List[String]("a")) mustEqual "${field(0)}"
    }

    "no to escape normal characters by \\" in {
      val template = new Template("\\a", List[String](), "")
      template.layout(List[String]("a")) mustEqual "\\a"
    }

    "not to escape markup characters by $$" in {
      val template = new Template("<>, ${row(0)}, ${media.dir}", List[String](), "<>")
      template.layout(List[String]("<>")) mustEqual "<>, <>, <>"
    }

    "import com.github.rubyu.adedit.Proc" in {
      val template = new Template("${ Proc(\"gnupack_basic-11.00\\\\app\\\\cygwin\\\\cygwin\\\\bin\\\\echo\",\"-n\", \"a\").exec() }", List[String](), "")
      template.layout(List[String]()) mustEqual "a"
    }
  }
}
