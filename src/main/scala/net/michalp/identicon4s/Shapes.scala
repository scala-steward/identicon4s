package net.michalp.identicon4s

import cats.implicits._
import scala.util.Random

trait Shapes {
  def randomShape: Shapes.Shape
}

object Shapes {

  def instance(random: Random) = new Shapes {

    override def randomShape: Shape =
      random.nextInt().abs.toInt % 3 match {
        case 0 => Shape.Square(nextSize())
        case 1 => Shape.Circle(nextSize())
        case 2 => Shape.Triangle(nextSize())
      }

    private def nextSize() = random.between(minSize, maxSize)
  }

  private val minSize = 20
  private val maxSize = 90

  sealed trait Shape extends Product with Serializable

  object Shape {
    case class Square(edgeLenght: Int) extends Shape
    case class Circle(radius: Int) extends Shape
    case class Triangle(edgeLenght: Int) extends Shape
  }

}
