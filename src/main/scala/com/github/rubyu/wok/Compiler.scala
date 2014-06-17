
package com.github.rubyu.wok

import scala.tools.nsc.{Global, Settings}
import scala.tools.nsc.interpreter.AbstractFileClassLoader
import scala.tools.nsc.io.VirtualDirectory
import scala.tools.nsc.reporters.ConsoleReporter
import scala.reflect.internal.util.BatchSourceFile


trait AbstractWok {
  val arg: List[String]
  val media: Template.Media
  def process(row: List[String]): String
}


class Compiler {
  private val virtualDirectory: VirtualDirectory = new VirtualDirectory("[memory]", None)
  private val settings: Settings = new Settings
  settings.deprecation.value = true
  settings.unchecked.value = true
  settings.outputDirs.setSingleOutput(virtualDirectory)
  settings.bootclasspath.value = System.getProperty("java.class.path")
  settings.classpath.value = settings.bootclasspath.value

  private val global = new Global(settings, new ConsoleReporter(settings))
  private val classLoader: AbstractFileClassLoader = new AbstractFileClassLoader(virtualDirectory, getClass.getClassLoader)

  def compileClass(source: String) {
    val compiler = new global.Run
    compiler.compileSources(List(new BatchSourceFile("Wok.scala", source)))
  }

  def getInstance(source: String, arg: List[String], mediaDir: String): AbstractWok = try {
    compileClass(wrapScript(source))
    classLoader.findClass("com.github.rubyu.wok.Wok")
      .getConstructor(classOf[List[String]], classOf[Template.Media])
      .newInstance(arg, new Template.Media(mediaDir))
      .asInstanceOf[AbstractWok]
  } finally {
    virtualDirectory.clear
  }

  def compile(source: String): Array[Byte] = try {
    compileClass(wrapScript(source))
    classLoader.classBytes("com.github.rubyu.wok.Wok")
  } finally {
    virtualDirectory.clear
  }

  private def wrapScript(code: String): String = {
    """
      |package com.github.rubyu.wok
      |
      |class Wok(val arg: List[String], val media: Template.Media) extends AbstractWok {
      |
      |  def process(row: List[String]) = {
      |    %s
      |  }
      |}
      |""".stripMargin.format(code)
  }
}