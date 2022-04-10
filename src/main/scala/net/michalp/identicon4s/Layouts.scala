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

import cats.implicits._
import cats.kernel.Monoid

import scala.util.Random

import Shapes.Shape

private[identicon4s] trait Layouts {
  def randomLayout: Layouts.Layout
}

private[identicon4s] object Layouts {

  def instance(shapes: Shapes, random: Random, config: Identicon.Config): Layouts = new Layouts {

    override def randomLayout: Layout =
      List
        .fill(
          random.between(
            config.minLayoutIterations,
            config.maxLayoutIterations + 1
          )
        )(singleRandomLayout)
        .combineAll

    private def singleRandomLayout: Layout =
      random.nextInt().abs.toInt % 5 match {
        case 0 => Layout.Diamond(nextShape, nextShape, nextShape, nextShape)
        case 1 => Layout.Square(nextShape, nextShape, nextShape, nextShape)
        case 2 => Layout.Triangle(nextShape, nextShape, nextShape)
        case 3 => Layout.ShapeX(nextShape, nextShape, nextShape, nextShape, nextShape)
        case 4 => Layout.Diagonal(nextShape, nextShape, nextShape)
      }

    private def nextShape = shapes.randomShape

  }

  sealed trait Layout {
    val shapesOnLayout: Seq[ShapeOnLayout]
  }

  object Layout {

    /** Diamond layout
      *     A
      *
      * B       C
      *
      *     D
      */
    final case class Diamond(a: Shape, b: Shape, c: Shape, d: Shape) extends Layout {

      override val shapesOnLayout: Seq[ShapeOnLayout] = Seq(
        ShapeOnLayout(a, 0.5, 0.2),
        ShapeOnLayout(b, 0.2, 0.5),
        ShapeOnLayout(c, 0.8, 0.5),
        ShapeOnLayout(d, 0.5, 0.8)
      )

    }

    /** Square layout
      * A      B
      *
      * C      D
      */
    final case class Square(a: Shape, b: Shape, c: Shape, d: Shape) extends Layout {

      override val shapesOnLayout: Seq[ShapeOnLayout] = Seq(
        ShapeOnLayout(a, 0.2, 0.2),
        ShapeOnLayout(b, 0.2, 0.8),
        ShapeOnLayout(c, 0.8, 0.2),
        ShapeOnLayout(d, 0.8, 0.8)
      )

    }

    /** Triangle layout
      *     A
      *
      * B       C
      */
    final case class Triangle(a: Shape, b: Shape, c: Shape) extends Layout {

      override val shapesOnLayout: Seq[ShapeOnLayout] = Seq(
        ShapeOnLayout(a, 0.5, 0.2),
        ShapeOnLayout(b, 0.1, 0.8),
        ShapeOnLayout(c, 0.9, 0.8)
      )

    }

    /** X layout
      * A       B
      *
      *     E
      *
      * C       D
      */
    final case class ShapeX(a: Shape, b: Shape, c: Shape, d: Shape, e: Shape) extends Layout {

      override val shapesOnLayout: Seq[ShapeOnLayout] = Seq(
        ShapeOnLayout(a, 0.2, 0.2),
        ShapeOnLayout(b, 0.2, 0.8),
        ShapeOnLayout(c, 0.8, 0.2),
        ShapeOnLayout(d, 0.8, 0.8),
        ShapeOnLayout(e, 0.4, 0.4)
      )

    }

    /** Diagonal layout
      * A
      *
      *     B
      *
      *         C
      */
    final case class Diagonal(a: Shape, b: Shape, c: Shape) extends Layout {

      override val shapesOnLayout: Seq[ShapeOnLayout] = Seq(
        ShapeOnLayout(a, 0.2, 0.2),
        ShapeOnLayout(b, 0.5, 0.5),
        ShapeOnLayout(c, 0.8, 0.8)
      )

    }

    /** Empty layout to provide Monoid
      */
    case object Empty extends Layout {

      override val shapesOnLayout: Seq[ShapeOnLayout] = Seq.empty

    }

    implicit val monoid: Monoid[Layout] = new Monoid[Layout] {

      override def combine(x: Layout, y: Layout): Layout = new Layout {

        override val shapesOnLayout: Seq[ShapeOnLayout] =
          x.shapesOnLayout ++ y.shapesOnLayout

      }

      override def empty: Layout = Empty

    }

  }

}
