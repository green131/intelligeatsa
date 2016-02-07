name := "server"

version := "1.0"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

initialize ~= { _ =>
	System.setProperty("http.port", "8080")
}

lazy val root = (project in file(".")).enablePlugins(PlayJava)