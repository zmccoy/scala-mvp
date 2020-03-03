package com.zmccoy

import cats.effect._
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.syntax._
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.server.blaze._
import fs2.Stream
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.middleware.Metrics
import org.http4s.server.middleware.{Metrics => ServerMetrics}
import org.http4s.metrics.MetricsOps
import org.http4s.metrics.prometheus.Prometheus

import scala.concurrent.ExecutionContext.global



object Server {

  def serve[F[_]: ConcurrentEffect : ContextShift : Timer]: Stream[F, ExitCode] = {
    for {
      config     <- Stream.eval(Configuration.loadConfig[F])
      logger     <- Stream.eval(Slf4jLogger.create)
      _          <- Stream.eval(logger.info("Starting the application"))
      transactor <- Stream.resource(DatabaseOps.createTransactor(config))
      metricOps  <- Stream.resource(prometheusMetricOps)
      client     <- Stream.resource(BlazeClientBuilder[F](global).resource)
      instClient = Metrics[F](metricOps)(client)
      instRoutes = ServerMetrics[F](metricOps)(pingRoute)
      exitCode   <- startServer[F](instRoutes)
    } yield {
      exitCode
    }
  }

  /*
  TODO: add new routes, add health server, add natchez
  */

  //TODO: Move out these route
  def pingRoute[F[_]: Sync]() = {
    val dsl = Http4sDsl[F]
    import dsl._

    HttpRoutes.of[F] {
      case GET -> Root / "ping" => Ok("pong")
    }
  }

  def prometheusMetricOps[F[_]: Sync]: Resource[F, MetricsOps[F]] = {
    for {
      cr <- Prometheus.collectorRegistry
      mo <- Prometheus.metricsOps(cr)
    } yield mo
  }

  def startServer[F[_]: ConcurrentEffect : Timer](routes: HttpRoutes[F]): Stream[F, ExitCode] = {
    BlazeServerBuilder[F]
      .bindHttp(8080, "localhost")
      .withHttpApp(routes.orNotFound)
      .serve
  }

}