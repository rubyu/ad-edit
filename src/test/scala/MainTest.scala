
package com.github.rubyu.adupdate

import org.specs2.mutable._
import org.specs2.specification.Scope
import java.io._


class TsvUpdaterTest extends SpecificationWithJUnit {

  trait scope extends Scope {
    val updater = new TsvUpdater
    val output = new ByteArrayOutputStream

    def input(str: String) = new ByteArrayInputStream(str.getBytes)
    def outputStr = output.toString("utf-8")
  }

  "TsvUpdater.update" should {
    "ingore comment lines" in new scope {
      updater.update(input("#"), output) { arr => arr }
      outputStr mustEqual("#")
    }

    "supports unicode entry" in new scope {
      updater.update(input("あ"), output) { arr => arr }
      outputStr mustEqual("あ")
    }

  }


}
