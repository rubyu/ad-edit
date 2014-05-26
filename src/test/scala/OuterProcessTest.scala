
package com.github.rubyu.adupdate

import org.specs2.mutable._


/**
 * UTF-8でのテストのみ行う。
 */
class OuterProcessTest extends SpecificationWithJUnit {

  "OuterProcess.execute" should {
    "call windows programs" in {
      val result = OuterProcess.execute(List(
        List("cmd", "/c", "echo", "a")
      ))
      new String(result, "UTF-8") mustEqual("a" + "\r\n")
    }

    "call cygwin programs" in {
      val result = OuterProcess.execute(List(
        List("C:\\ad-tools\\gnupack_basic-11.00\\app\\cygwin\\cygwin\\bin\\echo", "a")
      ))
      new String(result, "UTF-8") mustEqual("a" + "\n")
    }

    "call programs with arguments" in {
      val result = OuterProcess.execute(List(
        List("C:\\ad-tools\\gnupack_basic-11.00\\app\\cygwin\\cygwin\\bin\\cat",
          "./src/test/scala/resources/outer_process_test.txt")
      ))
      new String(result, "UTF-8") mustEqual("あ\r\nああ\r\nangel\r\néindʒəl\r\n")
    }

    "call programs with unicode encoded arguments" in {
      val result = OuterProcess.execute(List(
        List("C:\\ad-tools\\gnupack_basic-11.00\\app\\cygwin\\cygwin\\bin\\cat",
          "./src/test/scala/resources/outer_process_test.txt"),
        List("C:\\ad-tools\\gnupack_basic-11.00\\app\\cygwin\\cygwin\\bin\\grep",
          "éindʒəl")
      ))
      new String(result, "UTF-8") mustEqual("éindʒəl\n")
    }

    "call programs with standard input" in {
      val result = OuterProcess.execute(List(
        List("C:\\ad-tools\\gnupack_basic-11.00\\app\\cygwin\\cygwin\\bin\\cat",
          "./src/test/scala/resources/outer_process_test.txt",
          "-")
      ), "stdin-data")
      new String(result, "UTF-8") mustEqual("あ\r\nああ\r\nangel\r\néindʒəl\r\nstdin-data")
    }

    "call programs with unicode encoded standard input" in {
      val result = OuterProcess.execute(List(
        List("C:\\ad-tools\\gnupack_basic-11.00\\app\\cygwin\\cygwin\\bin\\cat",
          "./src/test/scala/resources/outer_process_test.txt",
          "-")
      ), "éindʒəl")
      new String(result, "UTF-8") mustEqual("あ\r\nああ\r\nangel\r\néindʒəl\r\néindʒəl")
    }

    "connect programs" in {
      val result = OuterProcess.execute(List(
        List("C:\\ad-tools\\gnupack_basic-11.00\\app\\cygwin\\cygwin\\bin\\cat",
          "./src/test/scala/resources/outer_process_test.txt"),
        List("C:\\ad-tools\\gnupack_basic-11.00\\app\\cygwin\\cygwin\\bin\\grep",
        "an")
      ))
      new String(result, "UTF-8") mustEqual("angel\n")
    }
  }
}
