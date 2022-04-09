package net.michalp.identicon4s

import cats.Applicative

trait Hashing[F[_]] {
  def hash(input: String): F[Long]
}

object Hashing {
  def apply[F[_]](implicit ev: Hashing[F]): Hashing[F] = ev

  def instance[F[_]: Applicative] = new Hashing[F] {
    def hash(input: String): F[Long] =
      Applicative[F].pure(input.hashCode())
  }

}
