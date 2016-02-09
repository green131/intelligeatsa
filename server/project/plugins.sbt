resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.4")

// Typesafe snapshots
resolvers += "Typesafe Snapshots" at "https://repo.typesafe.com/typesafe/snapshots/"

// MongoDB Java (Async) dependencies
libraryDependencies += "org.mongodb" % "mongodb-driver-core" % "3.2.1"
libraryDependencies += "org.mongodb" % "mongodb-driver-async" % "3.2.1"