package org.waman.gluino.nio

import java.nio.file.attribute.{PosixFilePermission, AclEntryType, AclEntry, AclFileAttributeView}
import java.nio.file.{FileSystems, Files}
import java.{util => jcf}

import org.scalatest.LoneElement._
import org.waman.gluino.{PosixFileSystemSpecific, WindowsSpecific}

import org.waman.gluino.io.GluinoIOCustomSpec
import java.nio.file.attribute.AclEntryPermission._

class DirectoryBuilderSpec extends GluinoIOCustomSpec with GluinoPath{

  "DirectoryBuilder should" - {

    "(Directories)" - {

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
    }

    "(Files)" - {

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

          file("build.sbt") <<
            s"""name := "${baseDir.getFileName}"
                |
                |version := "0.1-SNAPSHOT"
                |
                |libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"
                |""".stripMargin

          file("README.md") << "Read me!"
          file(".gitignore") << "target/"
          dir("project") {
            file("build.properties") << "sbt.version=0.13.8"
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
        text(projectHome / "README.md") should equal("Read me!")
        text(projectHome / ".gitignore") should equal("target/")
        text(projectHome / "project" / "build.properties") should equal("sbt.version=0.13.8")
        __TearDown__
        projectHome.deleteDir()
      }

      "be able to create file with content by using withWriter method (enhanced by GluinoPath)" in {
        __Exercise__
        val projectHome = new DirectoryBuilder {

          val baseDir = GluinoPath.createTempDirectory(prefix = "project-")

          file("build.sbt") <<
            s"""name := "${baseDir.getFileName}"
                |
                |version := "0.1-SNAPSHOT"
                |
                |libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"
                |""".stripMargin

          file("README.md") << "Read me!"
          file(".gitignore") << "target/"
          dir("project") {
            file("build.properties") << "sbt.version=0.13.8"
          }
          dir("src") {
            dir("main") {
              dir("scala") {
                file("MyFirstApp.scala") <<
                  """class MyFirstApp extends App{
                    |  println("Hello World!")
                    |}""".stripMargin
              }
            }
            dir("test") {
              dir("scala") {
                file("MyFirstAppSpec.scala") <<
                  """import org.scalatest.{FlatSpec, Matchers}
                    |
                    |class MyFirstAppSpec extends FlatSpec with Matchers{
                    |
                    |}""".stripMargin
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

    "(Files with Permission)" - {

      "be able to create file with posix permission" taggedAs PosixFileSystemSpecific in
        new PosixFileSystemAssumption{
          __Exercise__
          val projectHome = new DirectoryBuilder {

            val baseDir = GluinoPath.createTempDirectory(prefix = "project-")

            file("build.sbt", posix("rwx------"))
          }.baseDir
          val sut = projectHome / "build.sbt"
          __Verify__
          import java.nio.file.attribute.PosixFilePermission._
          val perms = Files.getAttribute(sut, "posix:permissions").asInstanceOf[jcf.Set[PosixFilePermission]]
          perms should contain theSameElementsAs Set(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE)
          __TearDown__
          projectHome.deleteDir()
        }

      val lookupService = FileSystems.getDefault.getUserPrincipalLookupService
      def getUser(name: String) = lookupService.lookupPrincipalByName(name)
      def getGroup(name: String) = lookupService.lookupPrincipalByGroupName(name)

      "be able to create file with ACL permission (Guest on Windows)" taggedAs WindowsSpecific in
        new WindowsAssumption {
          __SetUp__
          val guest = getUser("Guest")
          val aclEntry = AclEntry.newBuilder()
            .setPrincipal(guest)
            .setPermissions(READ_DATA, WRITE_DATA)
            .setType(AclEntryType.ALLOW)
            .build()
          val fileAttr = new java.nio.file.attribute.FileAttribute[jcf.List[AclEntry]]{
            override def name(): String = "acl:acl"
            override def value(): jcf.List[AclEntry] = jcf.Collections.singletonList(aclEntry)
          }
          __Exercise__
          val projectHome = new DirectoryBuilder {

            val baseDir = GluinoPath.createTempDirectory(prefix = "project-")

            file("build.sbt", fileAttr)
          }.baseDir

          __Verify__
          val aclAttr = Files.getFileAttributeView(projectHome / "build.sbt", classOf[AclFileAttributeView])
          aclAttr.getAcl.loneElement.permissions() should contain allOf (READ_DATA, WRITE_DATA)
          __TearDown__
          projectHome.deleteDir()
        }

      "be able to create file with ACL permission by acl(String) method" taggedAs WindowsSpecific in
        new WindowsAssumption {
          __Exercise__
          val projectHome = new DirectoryBuilder {

            val baseDir = GluinoPath.createTempDirectory(prefix = "project-")

            file("build.sbt", acl("u:Guest:r--"))
          }.baseDir
          __Verify__
          val aclAttr = Files.getFileAttributeView(projectHome / "build.sbt", classOf[AclFileAttributeView])
          aclAttr.getAcl.loneElement.permissions() should contain (READ_DATA)
          __TearDown__
          projectHome.deleteDir()
        }
    }
  }
}
