package com.zmccoy

object Models {
  final case class DoobieConfig(connectECSize: Int)
  final case class HConfig(url: String, driverClassName: String, user: String, pass: String)
  final case class AppConfig(doobie: DoobieConfig, hikari: HConfig)
}
