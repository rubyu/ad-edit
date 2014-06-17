
package com.github.rubyu.wok

import org.specs2.mutable._
import org.specs2.specification.Scope
import java.io.{FileOutputStream, OutputStreamWriter, OutputStream, File}


class CompilerTest extends SpecificationWithJUnit {

  "Compiler" should {

    "getInstance" in {
      val compiler = new Compiler
      compiler.getInstance("\"hello\"", List(), "").process(List()) mustEqual "hello"
    }

    "compile" in {
      val compiler = new Compiler
      val bytes = compiler.compile("\"hello\"")
      val cl = new ClassLoader(){ def get = defineClass("com.github.rubyu.wok.Wok", bytes, 0, bytes.size) }
      cl.get
        .getConstructor(classOf[List[String]], classOf[Template.Media])
        .newInstance(List(), new Template.Media(""))
        .asInstanceOf[AbstractWok]
        .process(List()) mustEqual "hello"
    }
  }
}
