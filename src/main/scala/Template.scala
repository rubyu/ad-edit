
package com.github.rubyu.adupdate

import org.fusesource.scalate.{Binding, TemplateEngine}


object Template {

  class Field(private val fieldValues: Array[String]) {
    def apply(index: Int) = if (index < fieldValues.size) fieldValues(index) else ""
  }

  class MediaDir(private val mediaDir: String) {
    def dir = mediaDir
  }

  def layout(commands: List[String], fieldValues: Array[String], mediaDir: String): List[String] = {
    val engine = new TemplateEngine
    engine.escapeMarkup = false //avoid escape for markup characters
    val context = Map("field" -> new Field(fieldValues), "media" -> new MediaDir(mediaDir))
    val bindings = List(Binding("field", "com.github.rubyu.adupdate.Template.Field"),
      Binding("media", "com.github.rubyu.adupdate.Template.MediaDir"))
    commands map { command =>
      val template = engine.compileSsp(command, bindings)
      engine.layout(template.source, context)
    }
  }
}



