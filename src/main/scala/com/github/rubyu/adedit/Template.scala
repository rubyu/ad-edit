
package com.github.rubyu.adedit

import org.fusesource.scalate.{Binding, TemplateEngine}


object Template {

  class Field {
    var row: List[String] = _
    def apply(index: Int) = if (index < row.size) row(index) else ""
  }

  class Media {
    var dir: String = _
  }
}

class Template(commands: List[List[String]]) {

  private val engine = new TemplateEngine
  engine.escapeMarkup = false //avoid escape for markup characters

  private val field = new Template.Field
  private val media = new Template.Media
  private val context = Map("field" -> field, "media" -> media)

  private val compiledCommands = compileCommands(commands)

  private def compileCommands(commands: List[List[String]]) = {
    val bindings = List(
      Binding("field", "com.github.rubyu.adedit.Template.Field"),
      Binding("media", "com.github.rubyu.adedit.Template.Media"))

    commands map { _ map { engine.compileSsp(_, bindings) } }
  }

  def layout(row: List[String], mediaDir: String): List[List[String]] = {
    field.row = row
    media.dir = mediaDir
    compiledCommands map { _ map { t => engine.layout(t.source, context) } }
  }
}



