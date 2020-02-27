package com.zmccoy

import cats.effect._
import cats.implicits._
import com.zmcccoy.DatabaseOps
import org.http4s.HttpRoutes
import org.http4s.syntax._
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.server.blaze._
import fs2.Stream
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.client.blaze.BlazeClientBuilder

import scala.concurrent.ExecutionContext.global



object Server {

  def serve[F[_]: ConcurrentEffect : ContextShift : Timer]: Stream[F, ExitCode] = {
    for {
      logger     <- Stream.eval(Slf4jLogger.create)
      _          <- Stream.eval(logger.info("Starting the application"))
      transactor <- Stream.resource(DatabaseOps.createTransactor())
      client     <- Stream.resource(BlazeClientBuilder[F](global).resource)
      exitCode   <- createPingRoute[F](routes)
    } yield {
      exitCode
    }

  }

  /*
  TODO:  Add trait that uses DB, add new routes, add health server , add config, add logger, add prometheus, add natchez, add dump of threads on end
  */

  //TODO: Move out these route
  def routes[F[_]: Sync]() = {
    val dsl = Http4sDsl[F]
    import dsl._

    HttpRoutes.of[F] {
      case GET -> Root / "ping" => Ok("pong")
    }
  }


  def createPingRoute[F[_]: ConcurrentEffect : Timer](routes: HttpRoutes[F]): Stream[F, ExitCode] = {
    BlazeServerBuilder[F]
      .bindHttp(8080, "localhost")
      .withHttpApp(routes.orNotFound)
      .serve
  }
}