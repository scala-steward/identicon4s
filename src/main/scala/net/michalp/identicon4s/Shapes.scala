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

import scala.util.Random

private[identicon4s] trait Shapes {
  def randomShape: Shapes.Shape
}

private[identicon4s] object Shapes {

  def instance(random: Random) = new Shapes {

    override def randomShape: Shape =
      random.nextInt().abs.toInt % 3 match {
        case 0 => Shape.Square(nextSize())
        case 1 => Shape.Circle(nextSize())
        case 2 => Shape.Triangle(nextSize())
      }

    private def nextSize() = random.between(minSize, maxSize)
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
