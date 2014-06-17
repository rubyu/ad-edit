
package com.github.rubyu.wok

import org.specs2.mutable._
import org.specs2.specification.Scope
import org.kohsuke.args4j.CmdLineException


class DropFieldOptionTest extends SpecificationWithJUnit {

  trait scope extends Scope {
    val option = new DropFieldOption
  }

  "DropFieldOption.column" should {
    "throw an CmdLineException when column is -1" in new scope {
      option.parseArgument(List[String]("-1")) must throwA[CmdLineException]
    }

    "throw a ManagedFailure when list is empty" in new scope {
      option.parseArgument(List[String]()).column must throwA(new ManagedFailure("'column' missing"))
    }

    "return Int" in new scope {
      option.parseArgument(List[String]("0")).column mustEqual 0
      option.parseArgument(List[String]("1")).column mustEqual 1
    }
  }
}
