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
import cats.kernel.Monoid

private[identicon4s] trait Layouts[F[_]] {
  def randomLayout: F[Layouts.Layout]
}

private[identicon4s] object Layouts {

  def instance[F[_]: Random: Monad](config: Identicon.Config): Layouts[F] = new Layouts[F] {

    override def randomLayout: F[Layout] =
      for {
        iterations <- Random[F].betweenInt(
                        config.minLayoutIterations,
                        config.maxLayoutIterations + 1
                      )
        layouts    <- List.fill(iterations)(singleRandomLayout).traverse(identity)
        combined = layouts.combineAll
      } yield combined

    private def singleRandomLayout: F[Layout] =
      Random[F].nextInt.map {
        _.abs.toInt % 5 match {
          case 0 => Layout.Diamond
          case 1 => Layout.Square
          case 2 => Layout.Triangle
          case 3 => Layout.ShapeX
          case 4 => Layout.Diagonal
        }
      }

  }

  sealed trait Layout {
    val objectPositions: Seq[RelativePosition]
  }

  object Layout {

    /** Diamond layout
      *     A
      *
      * B       C
      *
      *     D
      */
    final case object Diamond extends Layout {

      override val objectPositions: Seq[RelativePosition] = Seq(
        RelativePosition(0.5, 0.2),
        RelativePosition(0.2, 0.5),
        RelativePosition(0.8, 0.5),
        RelativePosition(0.5, 0.8)
      )

    }

    /** Square layout
      * A      B
      *
      * C      D
      */
    final case object Square extends Layout {

      override val objectPositions: Seq[RelativePosition] = Seq(
        RelativePosition(0.2, 0.2),
        RelativePosition(0.2, 0.8),
        RelativePosition(0.8, 0.2),
        RelativePosition(0.8, 0.8)
      )

    }

    /** Triangle layout
      *     A
      *
      * B       C
      */
    final case object Triangle extends Layout {

      override val objectPositions: Seq[RelativePosition] = Seq(
        RelativePosition(0.5, 0.2),
        RelativePosition(0.1, 0.8),
        RelativePosition(0.9, 0.8)
      )

    }

    /** X layout
      * A       B
      *
      *     E
      *
      * C       D
      */
    final case object ShapeX extends Layout {

      override val objectPositions: Seq[RelativePosition] = Seq(
        RelativePosition(0.2, 0.2),
        RelativePosition(0.2, 0.8),
        RelativePosition(0.8, 0.2),
        RelativePosition(0.8, 0.8),
        RelativePosition(0.4, 0.4)
      )

    }

    /** Diagonal layout
      * A
      *
      *     B
      *
      *         C
      */
    final case object Diagonal extends Layout {

      override val objectPositions: Seq[RelativePosition] = Seq(
        RelativePosition(0.2, 0.2),
        RelativePosition(0.5, 0.5),
        RelativePosition(0.8, 0.8)
      )

    }

    /** Empty layout to provide Monoid
      */
    case object Empty extends Layout {

      override val objectPositions: Seq[RelativePosition] = Seq.empty

    }

    implicit val monoid: Monoid[Layout] = new Monoid[Layout] {

      override def combine(x: Layout, y: Layout): Layout = new Layout {

        override val objectPositions: Seq[RelativePosition] =
          x.objectPositions ++ y.objectPositions

      }

      override def empty: Layout = Empty

    }

  }

}
