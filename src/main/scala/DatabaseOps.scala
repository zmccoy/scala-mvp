package com.zmccoy

import cats.effect._
import doobie.hikari._
import doobie.util.ExecutionContexts
import org.flywaydb.core.Flyway
import cats.implicits._

object DatabaseOps {
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

  def runMigrations[F[_] : Sync](config: AppConfig): F[Unit] = {
    Sync[F].delay {
      val flyway = Flyway.configure
        .locations("src/main/resources/migration")
        .validateOnMigrate(true)
        .dataSource(config.hikari.url, config.hikari.user, config.hikari.pass)
        .load

      flyway.baseline()
      flyway.migrate()
    }.void
  }
}
