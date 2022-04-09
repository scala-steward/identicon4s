/*
 * Copyright 2022 majk-p
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.michalp.identicon4s

import cats.Applicative
import cats.Functor
import cats.implicits._

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
        renderer.render(layout)
      }

  }

}
