package com.zmcccoy

import cats.effect._
import doobie.hikari._
import doobie.util.ExecutionContexts

object DatabaseOps {
  //TODO: Pass in configuration, write up explanation comments
  def createTransactor[F[_] : Async : ContextShift](): Resource[F, HikariTransactor[F]] = {
    for {
      connectEC  <- ExecutionContexts.fixedThreadPool[F](10)
      transactEC <- ExecutionContexts.cachedThreadPool[F]
      transactor <- HikariTransactor.newHikariTransactor[F](
        "org.postgresql.Driver",
        "url",
        "username",
        "pass",
        connectEC,
        Blocker.liftExecutionContext(transactEC))
    } yield transactor
  }
}
