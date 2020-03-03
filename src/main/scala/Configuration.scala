package com.zmccoy

import cats.effect.Sync
import com.typesafe.config.ConfigFactory
import pureconfig._
import pureconfig.generic.auto._

object Configuration {
  def loadConfig[F[_]: Sync]: F[AppConfig] = Sync[F].delay {
    val config = ConfigFactory.load()
    pureconfig.ConfigSource.fromConfig(config).at("com.zmccoy").loadOrThrow[AppConfig]
  }
}
