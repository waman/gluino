package org.waman.gluino.nio

import java.nio.file.{Files, Path}

import org.waman.gluino.io.GluinoIO.{lineSeparator => sep}
import org.waman.gluino.io.GluinoIOCustomSpec

class DirectoryBuilderSpec extends GluinoIOCustomSpec with GluinoPath{

  "DirectoryBuilder should" - {

    "be able to create nested directory structure" in {
      __Exercise__
      val projectHome = new DirectoryBuilder {
        val baseDir = GluinoPath.createTempDirectory(prefix = "project-")
        dir("src") {
          dir("main") {
            dir("java") {} // {} is necessary even if empty directory
            dir("scala") {}
            emptyDir("resources") // {} is not necessary
          }
          dir("test") {
            dir("scala") {}
            dir("resources") {}
          }
        }
        dir("project") {}
      }.baseDir
      __Verify__
      projectHome / "src" should exist
      projectHome / "src" / "main" should exist
      projectHome / "src" / "main" / "java" should exist
      projectHome / "src" / "main" / "scala" should exist
      projectHome / "src" / "main" / "resources" should exist
      projectHome / "src" / "test" / "scala" should exist
      projectHome / "src" / "test" / "resources" should exist
      projectHome / "project" should exist
      __TearDown__
      projectHome.deleteDir()
    }

    "be able to create files under any place of directory structure" in {
      __Exercise__
      val projectHome = new DirectoryBuilder {
        val baseDir = GluinoPath.createTempDirectory(prefix = "project-")
        file("build.sbt")
        file("README.md")
        file(".gitignore")
        dir("project") {
          file("build.properties")
        }
        dir("src") {
          dir("main") {
            dir("scala") {
              file("MyFirstApp.scala")
            }
          }
          dir("test") {
            dir("scala") {
              file("MyFirstAppSpec.scala")
            }
          }
        }
      }.baseDir
      __Verify__
      projectHome / "build.sbt" should exist
      projectHome / "README.md" should exist
      projectHome / ".gitignore" should exist
      projectHome / "project" / "build.properties" should exist
      projectHome / "src" / "main" / "scala" / "MyFirstApp.scala" should exist
      projectHome / "src" / "test" / "scala" / "MyFirstAppSpec.scala" should exist
      __TearDown__
      projectHome.deleteDir()
    }

    "be able to create file with content when 2 or more Strings are passed" in {
      __Exercise__
      val projectHome = new DirectoryBuilder {
        val baseDir = GluinoPath.createTempDirectory(prefix = "project-")
        file("build.sbt",
          s"""name := "${baseDir.getFileName}"""",
          "",
          """version := "0.1-SNAPSHOT"""",
          "",
          """libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test""""
        )
        file("README.md", "Read me!")
        file(".gitignore", "target/")
        dir("project") {
          file("build.properties", "sbt.version=0.13.8")
        }
        dir("src") {
          dir("main") {
            dir("scala") {
              file("MyFirstApp.scala")
            }
          }
          dir("test") {
            dir("scala") {
              file("MyFirstAppSpec.scala")
            }
          }
        }
      }.baseDir
      __Verify__
      text(projectHome / "README.md") should equal("Read me!" + sep)
      text(projectHome / ".gitignore") should equal("target/" + sep)
      text(projectHome / "project" / "build.properties") should equal("sbt.version=0.13.8" + sep)
      __TearDown__
      projectHome.deleteDir()
    }

    "be able to create file with content by using withWriter method (enhanced by GluinoPath)" in {
      __Exercise__
      val projectHome = new DirectoryBuilder {
        val baseDir = GluinoPath.createTempDirectory(prefix = "project-")
        file("build.sbt",
          s"""name := "${baseDir.getFileName}"""",
          "",
          """version := "0.1-SNAPSHOT"""",
          "",
          """libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test""""
        )
        file("README.md", "Read me!")
        file(".gitignore", "target/")
        dir("project") {
          file("build.properties", "sbt.version=0.13.8")
        }
        dir("src") {
          dir("main") {
            dir("scala") {
              file("MyFirstApp.scala").withWriter{ w =>
                w.write("class MyFirstApp extends App{" + sep)
                w.write("  println(\"Hello World!\")" + sep)
                w.write("}" + sep)
              }
            }
          }
          dir("test") {
            dir("scala") {
              file("MyFirstAppSpec.scala",
                """import org.scalatest.{FlatSpec, Matchers}
                  |
                  |class MyFirstAppSpec extends FlatSpec with Matchers{
                  |
                  |}""".stripMargin)
            }
          }
        }
      }.baseDir
      __Verify__
      (projectHome / "build.sbt").size should be > 0L
      (projectHome / "README.md").size should be > 0L
      (projectHome / ".gitignore").size should be > 0L
      (projectHome / "project" / "build.properties").size should be > 0L
      (projectHome / "src" / "main" / "scala" / "MyFirstApp.scala").size should be > 0L
      (projectHome / "src" / "test" / "scala" / "MyFirstAppSpec.scala").size should be > 0L
      __TearDown__
      projectHome.deleteDir()
    }
  }

  def text(path: Path): String = new String(Files.readAllBytes(path))
}
