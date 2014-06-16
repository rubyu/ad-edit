
package com.github.rubyu.wok

import org.specs2.mutable._


/**
 * UTF-8でのテストのみ行う。
 */
class ProcTest extends SpecificationWithJUnit {

  sequential

  "Proc.execute" should {

    "call windows programs" in {
      Proc("cmd", "/c", "echo", "a")
        .exec() mustEqual("a" + "\r\n")
    }

    "call cygwin programs" in {
      Proc("gnupack_basic-11.00\\app\\cygwin\\cygwin\\bin\\echo", "a")
        .exec() mustEqual("a" + "\n")
    }

    "call programs with arguments" in {
      Proc("gnupack_basic-11.00\\app\\cygwin\\cygwin\\bin\\cat",
        "./src/test/scala/resources/outer_process_test.txt")
        .exec() mustEqual("あ\r\nああ\r\nangel\r\néindʒəl\r\n")
    }

    "call programs with unicode encoded arguments" in {
      Proc("gnupack_basic-11.00\\app\\cygwin\\cygwin\\bin\\cat",
        "./src/test/scala/resources/outer_process_test.txt",
        "|",
        "gnupack_basic-11.00\\app\\cygwin\\cygwin\\bin\\grep",
        "éindʒəl")
        .exec() mustEqual("éindʒəl\n")
    }

    "call programs with standard input" in {
      Proc("gnupack_basic-11.00\\app\\cygwin\\cygwin\\bin\\cat",
        "./src/test/scala/resources/outer_process_test.txt",
        "-")
        .execWithInput("stdin-data") mustEqual("あ\r\nああ\r\nangel\r\néindʒəl\r\nstdin-data")
    }

    "call programs with unicode encoded standard input" in {
      Proc("gnupack_basic-11.00\\app\\cygwin\\cygwin\\bin\\cat",
        "./src/test/scala/resources/outer_process_test.txt",
        "-")
        .execWithInput("éindʒəl") mustEqual("あ\r\nああ\r\nangel\r\néindʒəl\r\néindʒəl")
    }

    "connect programs" in {
      Proc("gnupack_basic-11.00\\app\\cygwin\\cygwin\\bin\\cat",
        "./src/test/scala/resources/outer_process_test.txt",
        "|",
        "gnupack_basic-11.00\\app\\cygwin\\cygwin\\bin\\grep",
        "an")
        .exec() mustEqual("angel\n")
    }
  }
}
