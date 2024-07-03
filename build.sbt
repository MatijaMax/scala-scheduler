name := "scala-web-app"
lazy val root = (project in file(".")).enablePlugins(PlayScala, SwaggerPlugin)
scalaVersion := "2.13.10"

libraryDependencies ++= Seq(
  guice,
  // Database
  "com.typesafe.play" %% "play-slick" % "5.1.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.1.0",
  "org.postgresql" % "postgresql" % "42.5.0",
  // Security
  "com.github.t3hnar" %% "scala-bcrypt" % "4.3.0",
  // Swagger
  "org.webjars" % "swagger-ui" % "4.15.0",


)

// Swagger
swaggerDomainNameSpaces := Seq("models", "dtos")
