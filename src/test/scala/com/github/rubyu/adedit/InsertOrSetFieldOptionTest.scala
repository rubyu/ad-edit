
package com.github.rubyu.adedit

import org.specs2.mutable._
import org.specs2.specification.Scope
import org.kohsuke.args4j.CmdLineException


class InsertOrSetFieldOptionTest extends SpecificationWithJUnit {

  trait scope extends Scope {
    val option = new InsertOrSetFieldOption
  }

  "InsertOrSetFieldOption.field" should {
    "throw CmdLineException when field is -1" in new scope {
      option.parseArgument(List[String]("-1")) must throwA[CmdLineException]
    }

    "throw ManagedFailure when list is empty" in new scope {
      option.parseArgument(List[String]()).field must throwA(new ManagedFailure("'field' missing"))
    }

    "return Int" in new scope {
      option.parseArgument(List[String]("0")).field mustEqual 0
      option.parseArgument(List[String]("1")).field mustEqual 1
    }
  }

  "InsertOrSetFieldOption.source" should {
    "return None" in new scope {
      option.parseArgument(List[String]()).source mustEqual None
      option.parseArgument(List[String]("--source", "-1")).source mustEqual None
    }

    "return Some(Int)" in new scope {
      option.parseArgument(List[String]("--source", "0")).source mustEqual Some(0)
      option.parseArgument(List[String]("--source", "1")).source mustEqual Some(1)
    }
  }

  "InsertOrSetFieldOption.format" should {
    "throw ManagedFailure when no --format" in new scope {
      option.parseArgument(List[String]()).format must throwA(new ManagedFailure("'--format' missing"))
    }

    "throw ManagedFailure when --format foo given" in new scope {
      option.parseArgument(List[String]("--format", "foo")).format must throwA(new ManagedFailure("'foo' is not a supported format"))
    }

    "return valid format" in new scope {
      option.parseArgument(List[String]("--format", "jpg")).format mustEqual "jpg"
      option.parseArgument(List[String]("--format", "jpeg")).format mustEqual "jpeg"
      option.parseArgument(List[String]("--format", "png")).format mustEqual "png"
      option.parseArgument(List[String]("--format", "tif")).format mustEqual "tif"
      option.parseArgument(List[String]("--format", "tiff")).format mustEqual "tiff"
      option.parseArgument(List[String]("--format", "gif")).format mustEqual "gif"
      option.parseArgument(List[String]("--format", "svg")).format mustEqual "svg"
      option.parseArgument(List[String]("--format", "wav")).format mustEqual "wav"
      option.parseArgument(List[String]("--format", "mp3")).format mustEqual "mp3"
      option.parseArgument(List[String]("--format", "ogg")).format mustEqual "ogg"
      option.parseArgument(List[String]("--format", "flac")).format mustEqual "flac"
      option.parseArgument(List[String]("--format", "mp4")).format mustEqual "mp4"
      option.parseArgument(List[String]("--format", "swf")).format mustEqual "swf"
      option.parseArgument(List[String]("--format", "mov")).format mustEqual "mov"
      option.parseArgument(List[String]("--format", "mpg")).format mustEqual "mpg"
      option.parseArgument(List[String]("--format", "mpeg")).format mustEqual "mpeg"
      option.parseArgument(List[String]("--format", "mkv")).format mustEqual "mkv"
      option.parseArgument(List[String]("--format", "m4a")).format mustEqual "m4a"
      option.parseArgument(List[String]("--format", "html")).format mustEqual "html"
      option.parseArgument(List[String]("--format", "htm")).format mustEqual "htm"
      option.parseArgument(List[String]("--format", "text")).format mustEqual "text"
      option.parseArgument(List[String]("--format", "txt")).format mustEqual "txt"
    }
  }

  "InsertOrSetFieldOption.commands" should {
    "throw an exception when no commands given" in new scope {
      option.parseArgument(List[String]()).commands must throwA(new ManagedFailure("'command' missing in '--exec'"))
    }

    "throw an exception when empty command given" in new scope {
      option.parseArgument(List[String]("--exec")).commands must throwA(new ManagedFailure("'command' missing in '--exec'"))
      option.parseArgument(List[String]("a", "--exec")).commands must throwA(new ManagedFailure("'command' missing in '--exec'"))
      option.parseArgument(List[String]("--exec", "|")).commands must throwA(new ManagedFailure("'command' in '--exec' must not start with '|'"))
      option.parseArgument(List[String]("--exec", "a", "|")).commands must throwA(new ManagedFailure("'command' missing in '--exec'"))
      option.parseArgument(List[String]("--exec", "a", "|", "|", "b")).commands must throwA(new ManagedFailure("'command' in '--exec' must not start with '|'"))
    }

    "return List[List[String]]" in new scope {
      option.parseArgument(List[String]("--exec", "a")).commands mustEqual List(List[String]("a"))
      option.parseArgument(List[String]("--exec", "a", "b")).commands mustEqual List(List[String]("a", "b"))
      option.parseArgument(List[String]("--exec", "a", "|", "b")).commands mustEqual List(List[String]("a"), List[String]("b"))
    }
  }
}
