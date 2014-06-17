
package com.github.rubyu.wok

import org.specs2.mutable._
import org.specs2.specification.Scope
import org.kohsuke.args4j.CmdLineException


class InsertOrSetFieldOptionTest extends SpecificationWithJUnit {

  trait scope extends Scope {
    val option = new InsertOrSetFieldOption
  }

  "InsertOrSetFieldOption.column" should {
    "throw CmdLineException when column is -1" in new scope {
      option.parseArgument(List[String]("-1")) must throwA[CmdLineException]
    }

    "throw ManagedFailure when list is empty" in new scope {
      option.parseArgument(List[String]()).column must throwA(new ManagedFailure("'column' missing"))
    }

    "return Int" in new scope {
      option.parseArgument(List[String]("0")).column mustEqual 0
      option.parseArgument(List[String]("1")).column mustEqual 1
    }
  }

  "InsertOrSetFieldOption.script" should {
    "return None" in new scope {
      option.parseArgument(List[String]()).script must throwA(new ManagedFailure("'script' missing"))
    }

    "return String" in new scope {
      option.parseArgument(List[String]("0", "script")).script mustEqual "script"
    }
  }

  "InsertOrSetFieldOption.args" should {
    "return empty List[String] when no argument given" in new scope {
      option.parseArgument(List[String]()).args mustEqual List[String]()
    }

    "return List[String]" in new scope {
      option.parseArgument(List[String]("0", "script", "a", "b")).args mustEqual List[String]("a", "b")
    }
  }
}
