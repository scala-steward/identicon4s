package net.michalp.identicon4s

import cats.Applicative
import cats.data.Reader
import cats.implicits._
import net.michalp.identicon4s.Layouts.Layout.Diagonal
import net.michalp.identicon4s.Layouts.Layout.Diamond
import net.michalp.identicon4s.Layouts.Layout.ShapeX
import net.michalp.identicon4s.Layouts.Layout.Square
import net.michalp.identicon4s.Layouts.Layout.Triangle

import java.awt.Color
import java.awt.Graphics2D
import java.awt.Polygon
import java.awt.image.BufferedImage

import Layouts.Layout
import Shapes.Shape

private[identicon4s] trait Renderer {
  def render(layout: Layout): BufferedImage
}

private[identicon4s] object Renderer {
  def instance = new Renderer {

    override def render(layout: Layout): BufferedImage = {
      val buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
      (initialize :: renderShapes(layout))
        .sequence_
        .run(buffImg.createGraphics())
        .as(buffImg)
    }

    private def initialize: GraphicsOp[Unit] = liftOp { g2d =>
      // Set white background (.setBackground didn't seem to work)
      g2d.setColor(Color.white)
      g2d.fillRect(0, 0, width, height)
      // Set default drawing color
      g2d.setColor(Color.black)
    }

    private def renderShapes(layout: Layout): List[GraphicsOp[Unit]] =
      layout.shapesOnLayout.toList.map { shapeOnLayout =>
        renderShape(
          shapeOnLayout.shape,
          (shapeOnLayout.xPercentage * width).toInt,
          (shapeOnLayout.yPercentage * height).toInt
        )
      }

    private def renderShape(shape: Shape, x: Int, y: Int): GraphicsOp[Unit] = liftOp { g2d =>
      shape match {
        case Shape.Square(edgeSize) =>
          g2d.fillRect(x, y, (edgeSize*width).toInt, (edgeSize*height).toInt)
        case Shape.Circle(radius)   =>
          g2d.fillOval(x, y, (radius*width*2).toInt, (radius*height*2).toInt)
        case Shape.Triangle(edge)   =>
          val triangle = trianglePolygon((edge*width).toInt, x, y)
          g2d.fillPolygon(triangle)
      }
    }

    private val width = 256
    private val height = 256
    private val squareRootOf3 = math.sqrt(3)

    private type GraphicsOp[A] = Reader[Graphics2D, A]
    private def liftOp(f: Graphics2D => Unit): GraphicsOp[Unit] =
      Reader(f)

    private def trianglePolygon(edgeLenght: Int, x: Int, y: Int) = {
      val w = edgeLenght
      val h = (w * squareRootOf3 / 2).toInt
      val triangle = new Polygon()
      triangle.addPoint(x + w / 2, y)
      triangle.addPoint(x, y + h)
      triangle.addPoint(x + w, y + h)
      triangle
    }

  }

}
