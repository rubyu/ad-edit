
package com.github.rubyu.adupdate

import java.io._
import scala.util.control.Exception._
import scala.collection.JavaConversions._

import scala.sys.process._
import java.nio.charset.Charset
import org.fusesource.scalate.{DefaultRenderContext, Binding, TemplateEngine}
import com.sun.org.apache.bcel.internal.generic.ClassObserver

object Main {
  def main(args: Array[String]) {

    /*
  --exec (optional)  command ...

	{{ field-n }}			//headerがあれば楽だが、リストとレシピは分離するべきなのでわかりづらい
	{{ input-media-dir }}
	{{ output-media-dir }}

	--exec-source      n (optional)
	--input            deck (required)
	--output           deck (required)
	--field            n (required)
	--field-type	     { html, image, audio } (default is html)
  --row		          n (optional)
	--overwrite       (optional)
	--update          (optional)
	--test	          (optional)		//--exec句がない場合は--exec-sourceの内容をそのまま標準出力に書く
     */

    val option = new CliOption
    option.parseArgument(args toList)

    if (option.help) {
      //printUsage(System.out, parser)
      System.exit(0)
    }

    //required, must exist
    val input = allCatch opt new File(option.input)
    val inputMediaDir = allCatch opt new File(input.get.getAbsolutePath + ".media")

    //required, must not already exist
    val output = allCatch opt new File(option.output)
    val outputMediaDir = allCatch opt new File(output.get.getAbsolutePath + ".media")

    //optional
    val execSource = allCatch opt option.execSource.toInt

    //required
    val field = allCatch opt option.field.toInt

    //default is html
    //todo specify the type by case classes
    val fieldType = allCatch opt Option(option.fieldType) getOrElse("html")

    //optional
    val row = allCatch opt option.execSource.toInt


    //check required values
    //todo check arguments

    if (input.isEmpty) {
      //--input is required
      System.exit(1)
    }

    if (!input.get.exists) {
      //input file path is invalid
      System.exit(1)
    }

    if (!option.test) {
      if (output.isEmpty) {
        //--output is required
        System.exit(1)
      }

      if (!option.overwrite && output.get.exists) {
        //output file already exists
        System.exit(1)
      }

      if (field.isEmpty) {
        //--field is required
        System.exit(1)
      }
    }

    //prepare
    if (option.overwrite && outputMediaDir.nonEmpty && outputMediaDir.get.exists) {
      //delete output-media-dir
    }

    //make input-media-dir
    if (inputMediaDir.nonEmpty && !inputMediaDir.get.exists) {

    }

    //make output-media-dir
    if (outputMediaDir.nonEmpty && !outputMediaDir.get.exists) {

    }

    //do job

    System.exit(0)
  }
}


class Foo(val value: String)

object OuterProcess {

  /**
   * commandToProcess
   * connect
   * execute
   *
   * テンプレートのために用意する変数。
   * field(n)
   * input_file
   * input_media_dir
   * output_file
   * output_media_dir
   *
   */
  def applyTemplate = {


    val scalate = new TemplateEngine
    scalate.bindings = List(Binding("name", "com.github.rubyu.adupdate.Foo", true))
    val result = new StringWriter
    val context = new DefaultRenderContext("", scalate, new PrintWriter(result))
    context.attributes("name") = new Foo("James")
    val source = "hello ${ name.value }"
    val template = scalate.compileSsp(source)
    template.render(context)
    result.toString
    /*
    val source = "hello ${name.value}"
    val template = scalate.compileSsp(source)
    scalate.layout(template.source, Map("name" -> new Foo("James")))
    */
  }

  def connect(processes: List[ProcessBuilder]): ProcessBuilder = {
    processes.size match {
      case x if x > 1 => processes.head #| connect(processes.tail)
      case x if x == 1 => processes.head
    }
  }

  def execute(commands: List[List[String]], input: String = ""): Array[Byte] = {
    if (commands.isEmpty) throw new IllegalArgumentException
    //todo applyTemplates
    val inputStream = new ByteArrayInputStream(input.getBytes(Charset.defaultCharset))
    val outputStream = new ByteArrayOutputStream()
    var plist = commands map { stringSeqToProcess(_) }
    if (input.nonEmpty) {
      plist = plist.head #< inputStream +: plist.tail
    }
    (connect(plist) #> outputStream !)
    outputStream.toByteArray
  }
}


