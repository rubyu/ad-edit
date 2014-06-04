
package com.github.rubyu.adupdate

import org.specs2.mutable._
import java.nio.charset.StandardCharsets
import java.nio.file.{Paths, Files}
import java.io.File


class MainTest extends SpecificationWithJUnit {

  "Main.dropField" should {
    "drop a field of given position" in {
      Main.dropField(0)(List("a")) mustEqual List()
      Main.dropField(0)(List("a", "b")) mustEqual List("b")
      Main.dropField(1)(List("a")) mustEqual List("a")
      Main.dropField(1)(List("a", "b")) mustEqual List("a")
      Main.dropField(1)(List("a", "b", "c")) mustEqual List("a", "c")
    }
  }

  "Main.insertField" should {
    "insert a field to given position" in {
      Main.insertField(0, { row => "ins" })(List("a")) mustEqual List("ins", "a")
      Main.insertField(0, { row => "ins" })(List("a", "b")) mustEqual List("ins", "a", "b")
      Main.insertField(1, { row => "ins" })(List("a")) mustEqual List("a", "ins")
      Main.insertField(2, { row => "ins" })(List("a")) mustEqual List("a", "", "ins")
      Main.insertField(1, { row => "ins" })(List("a", "b")) mustEqual List("a", "ins", "b")
    }
  }

  "Main.updateField" should {
    "update a field of given position" in {
      Main.updateField(0, { row => "upd" })(List("a")) mustEqual List("upd")
      Main.updateField(0, { row => "upd" })(List("a", "b")) mustEqual List("upd", "b")
      Main.updateField(1, { row => "upd" })(List("a")) mustEqual List("a", "upd")
      Main.updateField(1, { row => "upd" })(List("a", "b")) mustEqual List("a", "upd")
    }
  }

  "Main.executeCommands" should {
    "return Array[Byte]" in {
      val template = new Template(List(List(
        "C:\\ad-tools\\gnupack_basic-11.00\\app\\cygwin\\cygwin\\bin\\echo", "${field(0)}")), "")
      Main.executeCommands(template, None)(List("hoge")) mustEqual "hoge\n".getBytes(StandardCharsets.UTF_8)
    }
  }

  "Main.ImageResult" should {
    "return img tag" in {
      val result = Main.ImageResult(Array[Byte](), "jpg")
      result.html mustEqual "<img src=da39a3ee5e6b4b0d3255bfef95601890afd80709.jpg>"
    }

    "save value to given directory" in {
      val temp = Files.createTempDirectory(null)
      val result = Main.ImageResult(Array[Byte](), "jpg")
      result.save(temp.toFile)
      val file = new File(temp.toFile, result.name)
      file.exists() mustEqual true
      file.delete() mustEqual true
      temp.toFile.delete() mustEqual true
    }
  }

  "Main.AudioResult" should {
    "return sound tag of anki" in {
      val result = Main.AudioResult(Array[Byte](), "wav")
      result.html mustEqual "[sound:da39a3ee5e6b4b0d3255bfef95601890afd80709.wav]"
    }

    "save value to given directory" in {
      val temp = Files.createTempDirectory(null)
      val result = Main.AudioResult(Array[Byte](), "wav")
      result.save(temp.toFile)
      val file = new File(temp.toFile, result.name)
      file.exists() mustEqual true
      file.delete() mustEqual true
      temp.toFile.delete() mustEqual true
    }
  }

  "Main.PlainTextResult" should {
    "escape html tags" in {
      Main.PlainTextResult("<").html mustEqual "&lt;"
      Main.PlainTextResult(">").html mustEqual "&gt;"
      Main.PlainTextResult("&").html mustEqual "&amp;"
      Main.PlainTextResult("\"").html mustEqual "&quot;"
      Main.PlainTextResult("'").html mustEqual "&#39;"
      Main.PlainTextResult("\n").html mustEqual "\n"
      Main.PlainTextResult("\r").html mustEqual "\r"
      Main.PlainTextResult("\t").html mustEqual "\t"
    }
  }

  "Main.HTMLResult" should {
    "not to escape html tags" in {
      Main.HTMLResult("<").html mustEqual "<"
      Main.HTMLResult(">").html mustEqual ">"
      Main.HTMLResult("&").html mustEqual "&"
      Main.HTMLResult("\"").html mustEqual "\""
      Main.HTMLResult("'").html mustEqual "'"
      Main.HTMLResult("\n").html mustEqual "\n"
      Main.HTMLResult("\r").html mustEqual "\r"
      Main.HTMLResult("\t").html mustEqual "\t"
    }
  }

  "Main.process" should {

    def f(result: Main.ExecuteResult): List[String] => Main.ExecuteResult = { row => result }

    "save media file to media dir"  in {
      val temp = Files.createTempDirectory(null)
      val result = Main.AudioResult(Array[Byte](), "wav")
      Main.process(f(result), temp.toFile)(List())
      val file = new File(temp.toFile, result.name)
      file.exists() mustEqual true
      file.delete() mustEqual true
      temp.toFile.delete() mustEqual true
    }
  }

  "Main.typed" should {

    def f(value: Array[Byte]): List[String] => Array[Byte] = { row => value }

    "return EmptyResult" in {
      val value = Array[Byte]()
      Main.typed(f(value), "jpg")(List()) mustEqual Main.EmptyResult
    }

    "return ImageResult(ext=jpg) when ext jpg given" in {
      val value = "a".getBytes(StandardCharsets.UTF_8)
      Main.typed(f(value), "jpg")(List()) mustEqual Main.ImageResult(value, "jpg")
    }
    "return ImageResult(ext=jpg) when ext jpeg given" in {
      val value = "a".getBytes(StandardCharsets.UTF_8)
      Main.typed(f(value), "jpeg")(List()) mustEqual Main.ImageResult(value, "jpg")
    }

    "return ImageResult(ext=tif) when ext tif given" in {
      val value = "a".getBytes(StandardCharsets.UTF_8)
      Main.typed(f(value), "tif")(List()) mustEqual Main.ImageResult(value, "tif")
    }
    "return ImageResult(ext=tif) when ext tiff given" in {
      val value = "a".getBytes(StandardCharsets.UTF_8)
      Main.typed(f(value), "tiff")(List()) mustEqual Main.ImageResult(value, "tif")
    }

    "return ImageResult(ext=gif) when ext gif given" in {
      val value = "a".getBytes(StandardCharsets.UTF_8)
      Main.typed(f(value), "gif")(List()) mustEqual Main.ImageResult(value, "gif")
    }

    "return ImageResult(ext=svg) when ext svg given" in {
      val value = "a".getBytes(StandardCharsets.UTF_8)
      Main.typed(f(value), "svg")(List()) mustEqual Main.ImageResult(value, "svg")
    }

    "return AudioResult(ext=wav) when ext wav given" in {
      val value = "a".getBytes(StandardCharsets.UTF_8)
      Main.typed(f(value), "wav")(List()) mustEqual Main.AudioResult(value, "wav")
    }

    "return AudioResult(ext=mp3) when ext mp3 given" in {
      val value = "a".getBytes(StandardCharsets.UTF_8)
      Main.typed(f(value), "mp3")(List()) mustEqual Main.AudioResult(value, "mp3")
    }

    "return AudioResult(ext=ogg) when ext ogg given" in {
      val value = "a".getBytes(StandardCharsets.UTF_8)
      Main.typed(f(value), "ogg")(List()) mustEqual Main.AudioResult(value, "ogg")
    }

    "return AudioResult(ext=flac) when ext flac given" in {
      val value = "a".getBytes(StandardCharsets.UTF_8)
      Main.typed(f(value), "flac")(List()) mustEqual Main.AudioResult(value, "flac")
    }

    "return AudioResult(ext=mp4) when ext mp4 given" in {
      val value = "a".getBytes(StandardCharsets.UTF_8)
      Main.typed(f(value), "mp4")(List()) mustEqual Main.AudioResult(value, "mp4")
    }

    "return AudioResult(ext=swf) when ext swf given" in {
      val value = "a".getBytes(StandardCharsets.UTF_8)
      Main.typed(f(value), "swf")(List()) mustEqual Main.AudioResult(value, "swf")
    }

    "return AudioResult(ext=mov) when ext mov given" in {
      val value = "a".getBytes(StandardCharsets.UTF_8)
      Main.typed(f(value), "mov")(List()) mustEqual Main.AudioResult(value, "mov")
    }

    "return AudioResult(ext=mpeg) when ext mpeg given" in {
      val value = "a".getBytes(StandardCharsets.UTF_8)
      Main.typed(f(value), "mpeg")(List()) mustEqual Main.AudioResult(value, "mpeg")
    }
    "return AudioResult(ext=mpeg) when ext mpg given" in {
      val value = "a".getBytes(StandardCharsets.UTF_8)
      Main.typed(f(value), "mpg")(List()) mustEqual Main.AudioResult(value, "mpeg")
    }

    "return AudioResult(ext=mkv) when ext mkv given" in {
      val value = "a".getBytes(StandardCharsets.UTF_8)
      Main.typed(f(value), "mkv")(List()) mustEqual Main.AudioResult(value, "mkv")
    }

    "return AudioResult(ext=m4a) when ext m4a given" in {
      val value = "a".getBytes(StandardCharsets.UTF_8)
      Main.typed(f(value), "m4a")(List()) mustEqual Main.AudioResult(value, "m4a")
    }

    "return HTMLResult when ext html given" in {
      val value = "a".getBytes(StandardCharsets.UTF_8)
      Main.typed(f(value), "html")(List()) mustEqual Main.HTMLResult("a")
    }
    "return HTMLResult when ext htm given" in {
      val value = "a".getBytes(StandardCharsets.UTF_8)
      Main.typed(f(value), "htm")(List()) mustEqual Main.HTMLResult("a")
    }

    "return PlainTextResult when ext text given" in {
      val value = "a".getBytes(StandardCharsets.UTF_8)
      Main.typed(f(value), "text")(List()) mustEqual Main.PlainTextResult("a")
    }
    "return PlainTextResult when ext txt given" in {
      val value = "a".getBytes(StandardCharsets.UTF_8)
      Main.typed(f(value), "txt")(List()) mustEqual Main.PlainTextResult("a")
    }
  }
}
