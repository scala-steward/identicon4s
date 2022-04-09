package net.michalp.identicon4s

import Shapes.Shape
import cats.Applicative
import scala.util.Random
import cats.implicits._

trait Layouts {
  def randomLayout: Layouts.Layout
}

object Layouts {

  def instance(shapes: Shapes, random: Random): Layouts = new Layouts {

    override def randomLayout: Layout =
      random.nextInt().abs.toInt % 5 match {
        case 0 => Layout.Diamond(shapes.randomShape, shapes.randomShape, shapes.randomShape, shapes.randomShape)
        case 1 => Layout.Square(shapes.randomShape, shapes.randomShape, shapes.randomShape, shapes.randomShape)
        case 2 => Layout.Triangle(shapes.randomShape, shapes.randomShape, shapes.randomShape)
        case 3 =>
          Layout.ShapeX(shapes.randomShape, shapes.randomShape, shapes.randomShape, shapes.randomShape, shapes.randomShape)
        case 4 => Layout.Diagonal(shapes.randomShape, shapes.randomShape, shapes.randomShape)
      }

  }

  final case class ShapeOnLayout(shape: Shape, xPercentage: Double, yPercentage: Double) {
    assert(xPercentage >= 0 && xPercentage <= 1, "xPercentage must be between 0.0 and 1.0")
    assert(yPercentage >= 0 && yPercentage <= 1, "yPercentage must be between 0.0 and 1.0")
  }

  sealed trait Layout extends Product with Serializable {
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
    case class Diamond(a: Shape, b: Shape, c: Shape, d: Shape) extends Layout {

      override val shapesOnLayout: Seq[ShapeOnLayout] = Seq(
        ShapeOnLayout(a, 0.5, 0.2),
        ShapeOnLayout(b, 0.2, 0.5),
        ShapeOnLayout(c, 0.6, 0.5),
        ShapeOnLayout(d, 0.5, 0.6)
      )

    }

    /** Square layout
      * A      B
      *
      * 
      * C      D
      */
    case class Square(a: Shape, b: Shape, c: Shape, d: Shape) extends Layout {

      override val shapesOnLayout: Seq[ShapeOnLayout] = Seq(
        ShapeOnLayout(a, 0.2, 0.2),
        ShapeOnLayout(b, 0.2, 0.6),
        ShapeOnLayout(c, 0.6, 0.2),
        ShapeOnLayout(d, 0.6, 0.6)
      )

    }

    /** Triangle layout
      *     A
      *
      * 
      * B       C
      */
    case class Triangle(a: Shape, b: Shape, c: Shape) extends Layout {

      override val shapesOnLayout: Seq[ShapeOnLayout] = Seq(
        ShapeOnLayout(a, 0.5, 0.2),
        ShapeOnLayout(b, 0.2, 0.6),
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
    case class ShapeX(a: Shape, b: Shape, c: Shape, d: Shape, e: Shape) extends Layout {

      override val shapesOnLayout: Seq[ShapeOnLayout] = Seq(
        ShapeOnLayout(a, 0.2, 0.2),
        ShapeOnLayout(b, 0.2, 0.6),
        ShapeOnLayout(c, 0.6, 0.2),
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
    case class Diagonal(a: Shape, b: Shape, c: Shape) extends Layout {

      override val shapesOnLayout: Seq[ShapeOnLayout] = Seq(
        ShapeOnLayout(a, 0.2, 0.2),
        ShapeOnLayout(b, 0.4, 0.4),
        ShapeOnLayout(c, 0.6, 0.6)
      )

    }
  }

}
