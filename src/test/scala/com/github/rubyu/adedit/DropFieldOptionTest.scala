
package com.github.rubyu.adedit

import org.specs2.mutable._
import org.specs2.specification.Scope
import org.kohsuke.args4j.CmdLineException


class DropFieldOptionTest extends SpecificationWithJUnit {

  trait scope extends Scope {
    val option = new DropFieldOption
  }

  "DropFieldOption.field" should {
    "throw an CmdLineException when field is -1" in new scope {
      option.parseArgument(List[String]("-1")) must throwA[CmdLineException]
    }

    "throw a ManagedFailure when list is empty" in new scope {
      option.parseArgument(List[String]()).field must throwA(new ManagedFailure("'field' missing"))
    }

    "return Int" in new scope {
      option.parseArgument(List[String]("0")).field mustEqual 0
      option.parseArgument(List[String]("1")).field mustEqual 1
    }
  }
}
