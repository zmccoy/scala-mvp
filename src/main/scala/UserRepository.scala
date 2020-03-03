package com.zmccoy

import cats.effect.Sync
import doobie.implicits._
import doobie.util.transactor.Transactor
import cats.implicits._

trait UserRepository[F[_]] {
  def readUser(userId: String): F[Option[User]]
  def writeUser(userId: String, name: String): F[Unit]
}

object UserRepository {
  def build[F[_]: Sync](transactor: Transactor[F]): UserRepository[F] =
    new UserRepository[F] {
      override def readUser(userId: String): F[Option[User]] =
        sql"""SELECT userId, age FROM users WHERE users.user_id = $userId""".query[User].option.transact(transactor)

      override def writeUser(userId: String, name: String): F[Unit] =
        sql"""INSERT INTO users values (user_id, name) values ($userId, $name)""".update.run.transact(transactor).void

    }
}
