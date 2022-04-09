package net.michalp.identicon4s

import Shapes.Shape
import cats.Applicative
import scala.util.Random
import cats.implicits._
import cats.kernel.Monoid

private[identicon4s] trait Layouts {
  def randomLayout: Layouts.Layout
}

private[identicon4s] object Layouts {

  def instance(shapes: Shapes, random: Random): Layouts = new Layouts {

    override def randomLayout: Layout =
      List.fill(random.between(1, 4))(singleRandomLayout).combineAll
    
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
        ShapeOnLayout(a, 0.5, 0.1),
        ShapeOnLayout(b, 0.1, 0.5),
        ShapeOnLayout(c, 0.6, 0.5),
        ShapeOnLayout(d, 0.5, 0.6)
      )

    }

    /** Square layout
      * A      B
      *
      * C      D
      */
    final case class Square(a: Shape, b: Shape, c: Shape, d: Shape) extends Layout {

      override val shapesOnLayout: Seq[ShapeOnLayout] = Seq(
        ShapeOnLayout(a, 0.1, 0.1),
        ShapeOnLayout(b, 0.1, 0.6),
        ShapeOnLayout(c, 0.6, 0.1),
        ShapeOnLayout(d, 0.6, 0.6)
      )

    }

    /** Triangle layout
      *     A
      *
      * B       C
      */
    final case class Triangle(a: Shape, b: Shape, c: Shape) extends Layout {

      override val shapesOnLayout: Seq[ShapeOnLayout] = Seq(
        ShapeOnLayout(a, 0.5, 0.1),
        ShapeOnLayout(b, 0.1, 0.6),
        ShapeOnLayout(c, 0.7, 0.6)
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
        ShapeOnLayout(a, 0.1, 0.1),
        ShapeOnLayout(b, 0.1, 0.6),
        ShapeOnLayout(c, 0.6, 0.1),
        ShapeOnLayout(d, 0.6, 0.6),
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
        ShapeOnLayout(a, 0.1, 0.1),
        ShapeOnLayout(b, 0.4, 0.4),
        ShapeOnLayout(c, 0.6, 0.6)
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
