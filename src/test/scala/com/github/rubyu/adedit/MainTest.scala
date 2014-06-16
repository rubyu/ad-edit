
package com.github.rubyu.wok

import org.specs2.mutable._
import java.nio.charset.StandardCharsets
import java.nio.file.{Paths, Files}
import java.io._
import org.specs2.specification.Scope


class MainTest extends SpecificationWithJUnit {

  "Main.main" should {

    sequential

    class AttemptToExitException(val status: Int) extends RuntimeException

    class MockExitSecurityManager extends java.rmi.RMISecurityManager {
      override def checkExit(status: Int) { throw new AttemptToExitException(status) }
      override def checkPermission(perm: java.security.Permission) {}
    }

    trait scope extends Scope with After {
      val _sm = System.getSecurityManager
      val _stdout = System.out
      val _stderr = System.err
      val _stdin = System.in

      val stdout, stderr = new ByteArrayOutputStream

      System.setSecurityManager(new MockExitSecurityManager)
      System.setOut(new PrintStream(new BufferedOutputStream(stdout), true, "utf-8"))
      System.setErr(new PrintStream(new BufferedOutputStream(stderr), true, "utf-8"))

      def after {
        System.setSecurityManager(_sm)
        System.setOut(_stdout)
        System.setErr(_stderr)
        System.setIn(_stdin)
      }
    }

    "print error when invalid command" in new scope {
      System.setIn(new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)))
      Main.main(Array[String]("a")) must throwAn(new AttemptToExitException(1))
      System.err.flush()
      stderr.toString("utf-8") mustEqual List(
        "Error: 'a' is not a supported command",
        "").mkString(System.lineSeparator)
    }

    "do insert with constant" in new scope {
      System.setIn(new ByteArrayInputStream("a".getBytes(StandardCharsets.UTF_8)))
      Main.main(Array[String](
        "insert", "0", "b")
      ) must throwAn(new AttemptToExitException(0))
      System.err.flush()
      stderr.toString("utf-8") mustEqual ""
      System.out.flush()
      stdout.toString("utf-8") mustEqual "b\ta\r\n"
    }

    "do insert with media.dir" in new scope {
      System.setIn(new ByteArrayInputStream("a".getBytes(StandardCharsets.UTF_8)))
      Main.main(Array[String](
        "insert", "0", "${media.dir}")
      ) must throwAn(new AttemptToExitException(0))
      System.err.flush()
      stderr.toString("utf-8") mustEqual ""
      System.out.flush()
      stdout.toString("utf-8") mustEqual Main.mediaDir.getAbsolutePath + "\ta\r\n"
    }

    "do insert with argument" in new scope {
      System.setIn(new ByteArrayInputStream("a".getBytes(StandardCharsets.UTF_8)))
      Main.main(Array[String](
        "insert", "0", "${arg(0)}", "b")
      ) must throwAn(new AttemptToExitException(0))
      System.err.flush()
      stderr.toString("utf-8") mustEqual ""
      System.out.flush()
      stdout.toString("utf-8") mustEqual "b\ta\r\n"
    }

    "do insert with row" in new scope {
      System.setIn(new ByteArrayInputStream("a".getBytes(StandardCharsets.UTF_8)))
      Main.main(Array[String](
        "insert", "0", "${row(0)}")
      ) must throwAn(new AttemptToExitException(0))
      System.err.flush()
      stderr.toString("utf-8") mustEqual ""
      System.out.flush()
      stdout.toString("utf-8") mustEqual "a\ta\r\n"
    }

    "do set-field with contant" in new scope {
      System.setIn(new ByteArrayInputStream("a".getBytes(StandardCharsets.UTF_8)))
      Main.main(Array[String](
        "set", "0", "b")
      ) must throwAn(new AttemptToExitException(0))
      System.out.flush()
      stdout.toString("utf-8") mustEqual "b\r\n"
    }

    "do set-field with media.dir" in new scope {
      System.setIn(new ByteArrayInputStream("a".getBytes(StandardCharsets.UTF_8)))
      Main.main(Array[String](
        "set", "0", "${media.dir}")
      ) must throwAn(new AttemptToExitException(0))
      System.out.flush()
      stdout.toString("utf-8") mustEqual Main.mediaDir.getAbsolutePath + "\r\n"
    }

    "do set-field with argument" in new scope {
      System.setIn(new ByteArrayInputStream("a".getBytes(StandardCharsets.UTF_8)))
      Main.main(Array[String](
        "set", "0", "${arg(0)}", "b")
      ) must throwAn(new AttemptToExitException(0))
      System.out.flush()
      stdout.toString("utf-8") mustEqual "b\r\n"
    }

    "do set-field with row" in new scope {
      System.setIn(new ByteArrayInputStream("a".getBytes(StandardCharsets.UTF_8)))
      Main.main(Array[String](
        "set", "0", "${row(0)}")
      ) must throwAn(new AttemptToExitException(0))
      System.out.flush()
      stdout.toString("utf-8") mustEqual "a\r\n"
    }

    "do drop" in new scope {
      System.setIn(new ByteArrayInputStream("a\tb".getBytes(StandardCharsets.UTF_8)))
      Main.main(Array[String](
        "drop", "0")) must throwAn(new AttemptToExitException(0))
      System.out.flush()
      stdout.toString("utf-8") mustEqual "b\r\n"
    }

    "print help" in new scope {
      Main.main(Array[String](
        "help")) must throwAn(new AttemptToExitException(0))
      stdout.toString("utf-8") mustEqual "See https://github.com/rubyu/wok" + System.lineSeparator
    }
  }

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
      Main.setField(0, { row => "upd" })(List("a")) mustEqual List("upd")
      Main.setField(0, { row => "upd" })(List("a", "b")) mustEqual List("upd", "b")
      Main.setField(1, { row => "upd" })(List("a")) mustEqual List("a", "upd")
      Main.setField(1, { row => "upd" })(List("a", "b")) mustEqual List("a", "upd")
    }
  }
}
