package com.zmcccoy

import cats.effect._
import com.zmccoy.Models.AppConfig
import doobie._
import doobie.implicits._
import doobie.hikari._
import doobie.util.ExecutionContexts

object DatabaseOps {
  //TODO: Pass in configuration, write up explanation comments
  def createTransactor[F[_] : Async : ContextShift](config: AppConfig): Resource[F, HikariTransactor[F]] = {
    for {
      connectEC  <- ExecutionContexts.fixedThreadPool[F](config.doobie.connectECSize)
      transactEC <- ExecutionContexts.cachedThreadPool[F]
      transactor <- HikariTransactor.newHikariTransactor[F](
        config.hikari.driverClassName,
        config.hikari.url,
        config.hikari.user,
        config.hikari.pass,
        connectEC,
        Blocker.liftExecutionContext(transactEC))
    } yield transactor
  }
}
