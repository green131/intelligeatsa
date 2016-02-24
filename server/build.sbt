name := "server"

version := "1.0"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

initialize ~= { _ =>
	System.setProperty("http.port", "8080")
}

libraryDependencies += "org.mongodb" % "mongodb-driver" % "3.2.1"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.3"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.6.3"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-annotations" % "2.6.3"
libraryDependencies += "junit" % "junit" % "4.11"



lazy val root = (project in file(".")).enablePlugins(PlayJava)
