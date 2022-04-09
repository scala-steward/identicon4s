package net.michalp.identicon4s

import Layouts.Layout
import Shapes.Shape
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import cats.implicits._
import cats.Applicative
import net.michalp.identicon4s.Layouts.Layout.Diagonal
import net.michalp.identicon4s.Layouts.Layout.Diamond
import net.michalp.identicon4s.Layouts.Layout.Square
import net.michalp.identicon4s.Layouts.Layout.Triangle
import net.michalp.identicon4s.Layouts.Layout.ShapeX
import java.awt.Color
import java.awt.Polygon

trait Renderer[F[_]] {
  def render(layout: Layout): BufferedImage
}

object Renderer {
  def apply[F[_]](implicit ev: Renderer[F]): Renderer[F] = ev

  def instance[F[_]: Applicative] = new Renderer[F] {

    private val width = 512
    private val height = 512
    private val squareRootOf3 = math.sqrt(3)

    override def render(layout: Layout): BufferedImage = {
      val buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
      val g2d = buffImg.createGraphics()
      // Set white background (.setBackground didn't seem to work)
      g2d.setColor(Color.white)
      g2d.fillRect(0, 0, buffImg.getWidth(), buffImg.getHeight())
      // Set default drawing color
      g2d.setColor(Color.black)

      layout.shapesOnLayout.map { shapeOnLayout =>
        renderShape(g2d)(
          shapeOnLayout.shape,
          (shapeOnLayout.xPercentage * width).toInt,
          (shapeOnLayout.yPercentage * height).toInt
        )
      }
      buffImg
    }

    def renderShape(g2d: Graphics2D)(shape: Shape, x: Int, y: Int): Unit =
      shape match {
        case Shape.Square(edgeSize) =>
          g2d.fillRect(x, y, edgeSize, edgeSize)
        case Shape.Circle(radius)   =>
          g2d.fillOval(x, y, radius * 2, radius * 2)
        case Shape.Triangle(edge)   =>
          val h = (edge * squareRootOf3 / 2).toInt
          val w = edge
          val triangle = new Polygon()
          triangle.addPoint(x + w / 2, y)
          triangle.addPoint(x, y + h)
          triangle.addPoint(x + w, y + h)
          g2d.fillPolygon(triangle)
      }

  }

}
