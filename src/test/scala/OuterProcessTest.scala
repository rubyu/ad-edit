
package com.github.rubyu.adupdate

import org.specs2.mutable._
import org.specs2.specification.Scope
import java.io._
import java.nio.charset.Charset


/**
 * UTF-8でのテストのみ行う。
 */
class OuterProcessTest extends SpecificationWithJUnit {

  //todo resourcesにバイナリを追加して、かつ、ここではパスを通してテストを行う
  //todo Cygwinが暗黙にMS-DOS形式のパスを使用するように
  //http://takuya-1st.hatenablog.jp/entry/20110423/1303586388

  "OuterProcess.call" should {
    "call a program" in {
      OuterProcess.call(List(
        List("C:\\Program Files (x86)\\Gow\\bin\\uname")
      )).size > 0 must beTrue
    }

    "call a program with a argument" in {
      OuterProcess.call(List(
        List("C:\\Program Files (x86)\\Gow\\bin\\cat", "./src/test/scala/resources/outer_process_test.txt")
      )).startsWith("あ") must beTrue
    }


    "call programs connected a pipe" in {
      OuterProcess.call(List(
        List("C:\\Program Files (x86)\\Gow\\bin\\cat", "./src/test/scala/resources/outer_process_test.txt"),
        List("C:\\Program Files (x86)\\Gow\\bin\\grep", "a")
      )) mustEqual("")
    }

    "call programs connected a pipe" in {
      OuterProcess.call(List(
        List("C:\\Program Files (x86)\\Gow\\bin\\cat", "./src/test/scala/resources/outer_process_test.txt"),
        List("C:\\Program Files (x86)\\Gow\\bin\\grep", "in")
      )) mustEqual("")
    }

    "call programs connected a pipe" in {
      OuterProcess.call(List(
        List("C:\\Program Files (x86)\\Gow\\bin\\cat", "./src/test/scala/resources/outer_process_test.txt"),
        List("cmd", "/c", "chcp", "65001", "&&", "C:\\Program Files (x86)\\Gow\\bin\\grep", "in")
      )) mustEqual("")
    }

    "call programs connected a pipe" in {
      OuterProcess.call(List(
        List("C:\\Program Files (x86)\\Gow\\bin\\cat", "./src/test/scala/resources/outer_process_test.txt"),
        List("cmd", "/c", "chcp", "65001", "&&", "C:\\Program Files (x86)\\Gow\\bin\\grep", "あ")
      )) mustEqual("")
    }

    "call programs connected a pipe" in {
      OuterProcess.call(List(
        List("C:\\ad-tools\\gnupack_basic-11.00\\app\\cygwin\\cygwin\\bin\\cat", "./src/test/scala/resources/outer_process_test.txt"),
        List("C:\\ad-tools\\gnupack_basic-11.00\\app\\cygwin\\cygwin\\bin\\grep", "あ")
      )) mustEqual("")
    }

    "call programs connected a pipe" in {
      OuterProcess.call(List(
        List("cmd", "/c", "chcp", "65001", "&&", "cmd", "/c", "echo", "éindʒəl"),
        List("C:\\ad-tools\\gnupack_basic-11.00\\app\\cygwin\\cygwin\\bin\\grep", "éindʒəl")
      )) mustEqual("")
    }

    "call programs connected a pipe" in {
      OuterProcess.call(List(
        List("C:\\ad-tools\\gnupack_basic-11.00\\app\\cygwin\\cygwin\\bin\\grep", "-Fn", "éindʒəl", "/cygdrive/c/ad-update/src/test/scala/resources/outer_process_test.txt")
      )) mustEqual("")
    }

    "call a program with unicode eivironment" in {
      OuterProcess.call(List(
        List("cmd", "/c", "chcp", "65001", "&&", "cmd", "/c",  "echo", "あ")
      )) mustEqual("")
    }

    "call a program with unicode eivironment" in {
      OuterProcess.call(List(
        List("cmd", "/c", "chcp", "65001", "&&", "cmd", "/c", "echo", "éindʒəl")
      )) mustEqual("")
    }

    "call a program with unicode eivironment" in {
      OuterProcess.call(List(
        List("python", "-c",  "import sys; print sys.getdefaultencoding()")
      )) mustEqual("")
    }


    "call a program with unicode eivironment" in {
      OuterProcess.call(List(
        List("python", "-c",  "import sys; reload(sys); sys.setdefaultencoding('utf-8'); sys.stdout.write(sys.argv[1].decode(sys.getfilesystemencoding() or sys.getdefaultencoding()))", "éindʒəl")
      )) mustEqual("")
    }

    "call a program with unicode eivironment" in {
      OuterProcess.call(List(
        List("java", "-jar", "-Dfile.encoding=utf-8",
          "C:\\ebquery\\target\\ebquery-0.3.0.jar",
          "-d", "Z:\\BTSync\\rubyu\\dictionary\\KENE7J5",
          "--ebmap", "C:\\ebquery\\src\\test\\resources\\KENE7J5.MAP",
          "angel")
      )) mustEqual("")
    }


  }
}
