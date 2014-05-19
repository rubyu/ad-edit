
package com.github.rubyu.adupdate

import scala.util.control.Exception._
import java.io.File


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
	                                //--output, --field などの値も使用しない
     */

    val option = new CliOption
    option.parseArgument(args toList)

    if (option.help) {
      //printUsage(System.out, parser)
      System.exit(0)
    }

    //required, must exist
    val input = allCatch opt new File(option.input)
    //required, must not already exist
    val output = allCatch opt new File(option.output)
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
        //output file path is invalid, or output file already exists
        System.exit(1)
      }

      if (field.isEmpty) {
        //--field is required
        System.exit(1)
      }
    }

    //prepare job
      //delete output-media-dir
      //make input-media-dir, output-media-dir

    //do job



    System.exit(0)
  }
}

class TsvReader {

}

class TsvWriter {

}

class ProcessCaller