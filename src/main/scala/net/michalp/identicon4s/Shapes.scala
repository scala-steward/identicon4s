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

import cats.Monad
import cats.effect.std.Random
import cats.implicits._

private[identicon4s] trait Shapes[F[_]] {
  def randomShape: F[Shapes.Shape]
  def randomShapes: LazyList[F[Shapes.Shape]]
}

private[identicon4s] object Shapes {

  def instance[F[_]: Random: Monad] = new Shapes[F] {

    override def randomShapes: LazyList[F[Shape]] = LazyList.continually(randomShape)

    override def randomShape: F[Shape] =
      for {
        randInt <- Random[F].nextInt.map(_.abs.toInt)
        size    <- nextSize
      } yield randInt % 3 match {
        case 0 => Shape.Square(size)
        case 1 => Shape.Circle(size)
        case 2 => Shape.Triangle(size)
      }

    private def nextSize: F[Double] = Random[F].betweenDouble(minSize, maxSize)
  }

  private val minSize = 0.05
  private val maxSize = 0.15

  sealed trait Shape extends Product with Serializable

  object Shape {
    final case class Square(edgeLenghtRatio: Double) extends Shape
    final case class Circle(radiusRatio: Double) extends Shape
    final case class Triangle(edgeLenghtRatio: Double) extends Shape
  }

}
