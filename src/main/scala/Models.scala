package com.zmccoy

//Configuration models
final case class DoobieConfig(connectECSize: Int)
final case class HConfig(url: String, driverClassName: String, user: String, pass: String)
final case class AppConfig(doobie: DoobieConfig, hikari: HConfig)


//Database models
final case class User(userId: String, name: String)
