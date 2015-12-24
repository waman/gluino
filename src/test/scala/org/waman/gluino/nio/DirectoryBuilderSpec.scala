package org.waman.gluino.nio

import java.nio.file.attribute.AclEntryPermission._
import java.nio.file.attribute.AclEntryType.ALLOW
import java.nio.file.attribute.PosixFilePermission._
import java.nio.file.attribute.{AclFileAttributeView, PosixFilePermission}
import java.nio.file.{FileSystems, Files}
import java.{util => jcf}

import org.scalatest.LoneElement._
import org.waman.gluino.io.GluinoIOCustomSpec
import org.waman.scalatest_util.{WindowsAdministrated, WindowsSpecific, PosixFileSystemSpecific}

class DirectoryBuilderSpec extends GluinoIOCustomSpec with GluinoPath{

  "DirectoryBuilder should" - {

    "(for Directories)" - {

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

    "(for Files)" - {

      "create files under any place of directory structure" in {
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

      "create file with content by using withWriter method (enhanced by GluinoPath)" in {
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

    "(for Files with Permission)" - {

      "create file with posix permission" taggedAs PosixFileSystemSpecific in
        new PosixFileSystemRequirement{
          __Exercise__
          val projectHome = new DirectoryBuilder {

            val baseDir = GluinoPath.createTempDirectory(prefix = "project-")

            file("build.sbt", posix("rwx------"))
          }.baseDir
          val sut = projectHome / "build.sbt"
          __Verify__
          val perms = Files.getAttribute(sut, "posix:permissions").asInstanceOf[jcf.Set[PosixFilePermission]]
          perms should contain theSameElementsAs Set(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE)
          __TearDown__
          projectHome.deleteDir()
        }

      val lookupService = FileSystems.getDefault.getUserPrincipalLookupService
      def getUser(name: String) = lookupService.lookupPrincipalByName(name)
      def getGroup(name: String) = lookupService.lookupPrincipalByGroupName(name)

      "create file with ACL permission (Guest on Windows)" taggedAs WindowsSpecific in
        new WindowsAclRequirement {
          __SetUp__
          val guest = getUser("Guest")
          __Exercise__
          val projectHome = new DirectoryBuilder {

            val baseDir = GluinoPath.createTempDirectory(prefix = "project-")

            file("build.sbt", acl(guest, Set(READ_DATA, WRITE_DATA), ALLOW))
          }.baseDir

          __Verify__
          val aclAttr = Files.getFileAttributeView(projectHome / "build.sbt", classOf[AclFileAttributeView])
          aclAttr.getAcl.loneElement.permissions() should contain allOf (READ_DATA, WRITE_DATA)
          __TearDown__
          projectHome.deleteDir()
        }

      "create file with ACL permission by acl(String) method" taggedAs WindowsSpecific in
        new WindowsAclRequirement {
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

    "(for Links and Symbolic Links)" - {

      "create a link to a file under the specified directory" in {
        __Exercise__
        val projectHome = new DirectoryBuilder {

          val baseDir = GluinoPath.createTempDirectory(prefix = "project-")

          dir("target"){
            file("target-file.txt") << "Some content."
          }

          dir("src") {
            link("test-link.lnk", baseDir / "target" / "target-file.txt")
          }
        }.baseDir
        __Verify__
        val sut = projectHome / "src" / "test-link.lnk"
        sut should exist
        text(sut) should equal ("Some content.")
        __TearDown__
        projectHome.deleteDir()
      }

      "create a symbolic link to a file under the specified directory" taggedAs WindowsAdministrated in
        new WindowsAdministratorRequirement {
          __Exercise__
          val projectHome = new DirectoryBuilder {

            val baseDir = GluinoPath.createTempDirectory(prefix = "project-")

            dir("target"){
              file("target-file.txt") << "Some content."
            }

            dir("src") {
              symbolicLink("test-symlink.symlink", baseDir / "target" / "target-file.txt")
            }
          }.baseDir
          __Verify__
          val sut = projectHome / "src" / "test-symlink.symlink"
          sut should exist
          Files.isSymbolicLink(sut) should equal (true)
          text(sut) should equal ("Some content.")
          __TearDown__
          projectHome.deleteDir()
        }
    }
  }
}
