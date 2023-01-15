ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "html2excel",
    idePackagePrefix := Some("com.github.xuhaojun"),
    libraryDependencies ++= Seq(
      "net.ruippeixotog" %% "scala-scraper" % "3.0.0",
      "com.norbitltd" %% "spoiwo" % "2.2.1",
      "org.apache.poi" % "poi" % "5.2.3",
      "org.apache.poi" % "poi-ooxml" % "5.2.3"
    )
  )
