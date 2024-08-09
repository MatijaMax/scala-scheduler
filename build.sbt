name := "scala-web-app"
lazy val root = (project in file(".")).enablePlugins(PlayScala, SwaggerPlugin)
scalaVersion := "2.12.9"

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
  // ZIO
  "dev.zio"                  %% "zio"    % "2.0.0-RC3",
  "dev.zio" %% "zio-interop-cats" % "3.3.0",
  "com.softwaremill.macwire" %% "macros" % "2.3.1" % Provided,
  compilerPlugin("com.github.ghik" % "silencer-plugin" % "1.4.3" cross CrossVersion.full),
  "com.github.ghik" % "silencer-lib" % "1.4.3" % Provided cross CrossVersion.full


)

// Swagger
swaggerDomainNameSpaces := Seq("models", "dtos")
