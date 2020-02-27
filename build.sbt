

val Http4sVersion = "0.21.1"
val CirceVersion = "0.13.0"
val ScalaTest = ""
val DoobieVersion = "0.8.8"
val Log4CatsVersion = "1.0.1"
val PureConfigVersion = "0.12.2"


lazy val root = (project in file("."))
  .settings(
    name := "server",
    scalaVersion := "2.13.1",
    organization := "com.zmccoy",
    libraryDependencies ++= Seq(
      "org.http4s"            %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"            %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s"            %% "http4s-circe"        % Http4sVersion,
      "org.http4s"            %% "http4s-dsl"          % Http4sVersion,
      "io.circe"              %% "circe-generic"       % CirceVersion,
      "org.tpolecat"          %% "doobie-core"         % DoobieVersion,
      "org.tpolecat"          %% "doobie-hikari"       % DoobieVersion,
      "io.chrisdavenport"     %% "log4cats-slf4j"      % Log4CatsVersion,
      "com.github.pureconfig" %% "pureconfig"          % PureConfigVersion,
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.0")
  )