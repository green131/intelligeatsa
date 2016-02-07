resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.4")

// Typesafe snapshots
resolvers += "Typesafe Snapshots" at "https://repo.typesafe.com/typesafe/snapshots/"

// Jongo dependencies
libraryDependencies += "uk.co.panaxiom" %% "play-jongo" % "1.0.0-jongo1.2"