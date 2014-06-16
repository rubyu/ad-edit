package com.github.rubyu.wok

import org.fusesource.scalate.{Binding, TemplateEngine}


object Template {

  //todo col(n) getOr ""

  class Field {
    var row: List[String] = _
    def apply(index: Int) = if (index < row.size) row(index) else ""
  }

  class Media {
    var dir: String = _
  }
}

class Template(script: String) {
  private val engine = new TemplateEngine
  engine.escapeMarkup = false //avoid escape for markup characters

  private val field = new Template.Field
  private val media = new Template.Media
  private val context = Map("field" -> field, "media" -> media)

  private val compiled = compile(script)

  private def compile(script: String) = {
    val bindings = List(
      Binding("field", "com.github.rubyu.adedit.Template.Field"),
      Binding("media", "com.github.rubyu.adedit.Template.Media"))

    engine.compileSsp("<% import com.github.rubyu.adedit.Proc %>" + script, bindings)
  }

  def layout(row: List[String], mediaDir: String): String = {
    field.row = row
    media.dir = mediaDir
    engine.layout(compiled.source, context)
  }
}



