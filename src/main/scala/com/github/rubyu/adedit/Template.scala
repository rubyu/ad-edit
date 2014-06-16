package com.github.rubyu.wok

import org.fusesource.scalate.{Binding, TemplateEngine}


object Template {
  class Media(val dir: String)
}

class Template(script: String, args: List[String], mediaDir: String) {
  private val engine = new TemplateEngine
  engine.escapeMarkup = false //avoid escape for markup characters

  private val media = new Template.Media(mediaDir)
  private val compiled = compile(script)

  private def compile(script: String) = {
    val bindings = List(
      Binding("row", "List[String]"),
      Binding("arg", "List[String]"),
      Binding("media", "com.github.rubyu.wok.Template.Media"))

    engine.compileSsp("<% import com.github.rubyu.wok.Proc %>" + script, bindings)
  }

  def layout(row: List[String]): String = {
    engine.layout(compiled.source, Map("row" -> row, "arg" -> args, "media" -> media))
  }
}



