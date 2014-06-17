package com.github.rubyu.wok


object Template {
  class Media(val dir: String)
}

class Template(script: String, args: List[String], mediaDir: String) {

  val compiler = new Compiler
  val wok = compiler.getInstance(script, args, mediaDir)

  def layout(row: List[String]): String = {
    wok.process(row)
  }
}



