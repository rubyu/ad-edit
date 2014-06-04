
package com.github.rubyu.adupdate

import org.specs2.mutable._
import org.specs2.specification.Scope
import org.kohsuke.args4j.CmdLineException


class CliOptionTest extends SpecificationWithJUnit {

  trait scope extends Scope {
    val option = new CliOption
  }

  "CliOption" should {
    "have default values when a empty array given" in new scope {
      option.parseArgument(Nil)
      option.exec mustEqual(List.empty[List[String]])
      option.execSource mustEqual null
      option.input mustEqual null
      option.output mustEqual null
      option.field mustEqual null
      option.fieldType mustEqual null
      option.row mustEqual null
      option.overwrite mustEqual false
      option.update mustEqual false
      option.test mustEqual false
    }
  }

  "CliOption.exec" should {
    "be null when given no values" in new scope {
      option.parseArgument(Array("--exec") toList)
      option.exec mustEqual(List.empty[List[String]])
    }
    "be null when given no values" in new scope {
      option.parseArgument(Array("a", "--exec") toList) must throwAn[CmdLineException]
      option.exec mustEqual(List.empty[List[String]])
    }
    "be null when given no values" in new scope {
      option.parseArgument(Array("a", "b", "--exec") toList) must throwAn[CmdLineException]
      option.exec mustEqual(List.empty[List[String]])
    }


    "have given values in a command" in new scope {
      option.parseArgument(Array("--exec", "a") toList)
      option.exec mustEqual(List( List("a") ))
    }
    "have given values in a command" in new scope {
      option.parseArgument(Array("--exec", "a", "b") toList)
      option.exec mustEqual(List( List("a", "b") ))
    }
    "have given values in a command" in new scope {
      option.parseArgument(Array("--exec", "--exec-source") toList)
      option.exec mustEqual(List( List("--exec-source") ))
    }
    "have given values in a command" in new scope {
      option.parseArgument(Array("--exec", "--exec-source", "a") toList)
      option.exec mustEqual(List( List("--exec-source", "a") ))
    }
    "have given values in a command" in new scope {
      option.parseArgument(Array("--exec", "a", "--exec-source") toList)
      option.exec mustEqual(List( List("a", "--exec-source") ))
    }
    "have given values in a command" in new scope {
      option.parseArgument(Array("--exec", "--exec-source", "--input") toList)
      option.exec mustEqual(List( List("--exec-source", "--input") ))
    }

    "throw IllegalArgumentException when pipe command on the first position" in new scope {
      option.parseArgument(Array("--exec", "|", "a") toList) must throwA(new IllegalArgumentException)
    }

    "throw IllegalArgumentException when pipe command on the last position" in new scope {
      option.parseArgument(Array("--exec", "a", "|") toList) must throwA(new IllegalArgumentException)
    }

    "throw IllegalArgumentException when succesive pipe commands" in new scope {
      option.parseArgument(Array("--exec", "a", "|", "|", "b") toList) must throwA(new IllegalArgumentException)
    }

    "have multiple commands" in new scope {
      option.parseArgument(Array("--exec", "a", "|", "b") toList)
      option.exec mustEqual(List( List("a"), List("b") ))
    }

    "have multiple commands" in new scope {
      option.parseArgument(Array("--exec", "a", "|", "b" , "|", "c") toList)
      option.exec mustEqual(List( List("a"), List("b"), List("c") ))
    }

  }
}
