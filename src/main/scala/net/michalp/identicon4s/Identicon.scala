package net.michalp.identicon4s

import cats.Applicative
import cats.Functor
import cats.Monad
import cats.effect._
import cats.implicits._

import java.awt.image.BufferedImage
import java.awt.image.RenderedImage
import scala.util.Random

trait Identicon[F[_]] {
  def generate(input: String): F[RenderedImage]
}

object Identicon {
  def apply[F[_]](implicit ev: Identicon[F]): Identicon[F] = ev

  def defaultInstance[F[_]: Applicative] = {
    implicit val hashing: Hashing[F] = Hashing.instance
    instance[F]
  }

  def instance[F[_]: Hashing: Functor] = new Identicon[F] {

    override def generate(input: String): F[RenderedImage] =
      Hashing[F].hash(input).map { seed =>
        val random = new Random(seed)
        val shapes: Shapes = Shapes.instance(random)
        val layouts: Layouts = Layouts.instance(shapes, random)
        val renderer: Renderer = Renderer.instance
        val layout = layouts.randomLayout
        println(s"Input: $input, layout: $layout")
        renderer.render(layout)
      }

  }

}
