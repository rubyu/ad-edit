
package com.github.rubyu.adupdate

import org.specs2.mutable._
import org.specs2.specification.Scope
import org.kohsuke.args4j.CmdLineException


class DropFieldOptionTest extends SpecificationWithJUnit {

  trait scope extends Scope {
    val option = new DropFieldOption
  }

  "DropFieldOption.field" should {
    "throw an exception" in new scope {
      option.parseArgument(List[String]("-1")).field must throwA[CmdLineException]
    }

    "return Int" in new scope {
      option.parseArgument(List[String]("0")).field mustEqual 0
      option.parseArgument(List[String]("1")).field mustEqual 1
    }
  }
}
