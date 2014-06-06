
package com.github.rubyu.adedit

import org.fusesource.scalate.{Binding, TemplateEngine}


object Template {

  class Field(val row: List[String]) {
    def apply(index: Int) = if (index < row.size) row(index) else ""
  }

  class Media(val mediaDir: String) {
    def dir = mediaDir
  }
}

class Template(commands: List[List[String]], private val mediaDir: String) {

  private val engine = new TemplateEngine
  engine.escapeMarkup = false //avoid escape for markup characters

  private val compiledCommands = compileCommands(commands)

  private def compileCommands(commands: List[List[String]]) = {
    val bindings = List(
      Binding("field", "com.github.rubyu.adedit.Template.Field"),
      Binding("media", "com.github.rubyu.adedit.Template.Media"))

    commands map { _ map { engine.compileSsp(_, bindings) } }
  }

  def layout(row: List[String]): List[List[String]] = {
    val context = Map(
      "field" -> new Template.Field(row),
      "media" -> new Template.Media(mediaDir))
    compiledCommands map { _ map { t => engine.layout(t.source, context) } }
  }
}



