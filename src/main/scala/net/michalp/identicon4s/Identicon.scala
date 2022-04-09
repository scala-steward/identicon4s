package net.michalp.identicon4s

import cats.Applicative
import cats.Monad
import cats.effect._
import cats.implicits._

import scala.util.Random
import java.awt.image.BufferedImage

trait Identicon[F[_]] {
  def toBuffImage(input: String): F[BufferedImage]
  def toBase64Png(input: String): F[String]
}

object Identicon {
  def apply[F[_]](implicit ev: Identicon[F]): Identicon[F] = ev

  def instance[F[_]: Applicative] = new Identicon[F] {

    private implicit val hashing: Hashing[F] = Hashing.instance

    override def toBuffImage(input: String): F[BufferedImage] =
      hashing.hash(input).map { seed =>
        val random = new Random(seed)
        val shapes: Shapes = Shapes.instance(random)
        val layouts: Layouts = Layouts.instance(shapes, random)
        val renderer: Renderer[F] = Renderer.instance
        val layout = layouts.randomLayout
        println(s"Input: $input, layout: $layout")
        renderer.render(layout)
      }

    override def toBase64Png(input: String): F[String] = ???

  }

}
