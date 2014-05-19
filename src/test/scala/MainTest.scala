
package com.github.rubyu.adupdate

import org.specs2.mutable._
import org.specs2.specification.Scope
import org.kohsuke.args4j.CmdLineException


class MainTest extends SpecificationWithJUnit {

}


class CliOptionTest extends SpecificationWithJUnit {

  trait Options extends Scope {
    val option = new CliOption
  }

  "CliOption" should {
    "have default values when a empty array given" in new Options {
      option.parseArgument(Nil)
      option.exec must haveTheSameElementsAs(List.empty[List[String]])
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
    "be null when given no values" in new Options {
      option.parseArgument(Array("--exec") toList)
      option.exec must haveTheSameElementsAs(List.empty[List[String]])
    }
    "be null when given no values" in new Options {
      option.parseArgument(Array("a", "--exec") toList) must throwAn[CmdLineException]
      option.exec must haveTheSameElementsAs(List.empty[List[String]])
    }
    "be null when given no values" in new Options {
      option.parseArgument(Array("a", "b", "--exec") toList) must throwAn[CmdLineException]
      option.exec must haveTheSameElementsAs(List.empty[List[String]])
    }


    "have given values in a command" in new Options {
      option.parseArgument(Array("--exec", "a") toList)
      option.exec must haveTheSameElementsAs(List( List("a") ))
    }
    "have given values in a command" in new Options {
      option.parseArgument(Array("--exec", "a", "b") toList)
      option.exec must haveTheSameElementsAs(List( List("a", "b") ))
    }
    "have given values in a command" in new Options {
      option.parseArgument(Array("--exec", "--exec-source") toList)
      option.exec must haveTheSameElementsAs(List( List("--exec-source") ))
    }
    "have given values in a command" in new Options {
      option.parseArgument(Array("--exec", "--exec-source", "a") toList)
      option.exec must haveTheSameElementsAs(List( List("--exec-source", "a") ))
    }
    "have given values in a command" in new Options {
      option.parseArgument(Array("--exec", "a", "--exec-source") toList)
      option.exec must haveTheSameElementsAs(List( List("a", "--exec-source") ))
    }
    "have given values in a command" in new Options {
      option.parseArgument(Array("--exec", "--exec-source", "--input") toList)
      option.exec must haveTheSameElementsAs(List( List("--exec-source", "--input") ))
    }

    "ignore pipe command on the first position" in new Options {
      option.parseArgument(Array("--exec", "|", "a") toList)
      option.exec must haveTheSameElementsAs(List( List("a") ))
    }
    "ignore pipe command on the first position" in new Options {
      option.parseArgument(Array("--exec", "|", "|", "a") toList)
      option.exec must haveTheSameElementsAs(List( List("a") ))
    }

    "ignore pipe command on the last position" in new Options {
      option.parseArgument(Array("--exec", "a", "|") toList)
      option.exec must haveTheSameElementsAs(List( List("a") ))
    }
    "ignore pipe command on the last position" in new Options {
      option.parseArgument(Array("--exec", "a", "|", "|") toList)
      option.exec must haveTheSameElementsAs(List( List("a") ))
    }

    "ignore succesive pipe commands" in new Options {
      option.parseArgument(Array("--exec", "|", "|") toList)
      option.exec must haveTheSameElementsAs(List.empty[List[String]])
    }
    "ignore succesive pipe commands" in new Options {
      option.parseArgument(Array("--exec", "a", "|", "|", "b") toList)
      option.exec must haveTheSameElementsAs(List( List("a"), List("b") ))
    }

    "have multiple commands" in new Options {
      option.parseArgument(Array("--exec", "a", "|", "b") toList)
      option.exec must haveTheSameElementsAs(List( List("a"), List("b") ))
    }

    "have multiple commands" in new Options {
      option.parseArgument(Array("--exec", "a", "|", "b" , "|", "c") toList)
      option.exec must haveTheSameElementsAs(List( List("a"), List("b"), List("c") ))
    }

  }
}


/*

package com.github.rubyu.ebquery

import org.specs2.mutable._
import scala.collection.JavaConversions._
import org.kohsuke.args4j.CmdLineParser


class CliOptionTest extends SpecificationWithJUnit {

  "CliOption" should {
    "have default values" in {
      val option = new CliOption
      val parser = new CmdLineParser(option)
      parser.parseArgument(Nil)
      option.words mustEqual null
      option.dir mustEqual null
      option.help mustEqual false
      option.format mustEqual null
      option.modules mustEqual null
    }

    "have given values" in {
      val args = Array(
        "-d", "dir",
        "--ebmap", "file",
        "-h", "word",
        "-f", "format",
        "-m", "modules")
      val option = new CliOption
      val parser = new CmdLineParser(option)
      parser.parseArgument(args.toList)
      option.words mustEqual Array("word")
      option.dir mustEqual "dir"
      option.ebMap mustEqual "file"
      option.help mustEqual true
      option.format mustEqual "format"
      option.modules mustEqual "modules"
    }

    "have given multiple words" in {
      val args: Array[String] = Array("a", "b")
      val option = new CliOption
      val parser = new CmdLineParser(option)
      parser.parseArgument(args.toList)
      option.words mustEqual Array("a", "b")
    }
  }
}
*/