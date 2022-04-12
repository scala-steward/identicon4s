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
import cats.data.ReaderT
import cats.effect.std.Random
import cats.implicits._

import java.awt.Color
import java.awt.Graphics2D
import java.awt.Polygon
import java.awt.image.BufferedImage

import Shapes.Shape

private[identicon4s] trait Renderer[F[_]] {
  def render(shapesOnLayout: List[ShapeOnLayout]): F[BufferedImage]
}

private[identicon4s] object Renderer {

  def instance[F[_]: Random: Monad](config: Identicon.Config) = new Renderer[F] {

    override def render(shapesOnLayout: List[ShapeOnLayout]): F[BufferedImage] = {
      val buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
      (initialize :: renderShapes(shapesOnLayout))
        .sequence_
        .run(buffImg.createGraphics())
        .as(buffImg)
    }

    private def initialize: GraphicsOp[Unit] = liftPureOp { g2d =>
      // Set white background (.setBackground didn't seem to work)
      g2d.setColor(Color.white)
      g2d.fillRect(0, 0, width, height)
      // Set default drawing color
      g2d.setColor(Color.black)
    }

    private def renderShapes(shapesOnLayout: List[ShapeOnLayout]): List[GraphicsOp[Unit]] =
      shapesOnLayout.toList.map { shapeOnLayout =>
        renderShape(
          shapeOnLayout.shape,
          (shapeOnLayout.position.xPercentage * width).toInt,
          (shapeOnLayout.position.yPercentage * height).toInt
        )
      }

    private def renderShape(shape: Shape, x: Int, y: Int): GraphicsOp[Unit] = liftOp { g2d =>
      nextColor.map { color =>
        g2d.setColor(color)
        shape match {
          case Shape.Square(sizeRatio)   =>
            val shapeWidth = (sizeRatio * width).toInt
            val shapeHeight = (sizeRatio * height).toInt
            g2d.fillRect(x - (shapeWidth / 2), y - (shapeHeight / 2), shapeWidth, shapeHeight)
          case Shape.Circle(radiusRatio) =>
            val radius = (radiusRatio * width).toInt
            g2d.fillOval(x - radius, y - radius, radius * 2, radius * 2)
          case Shape.Triangle(sizeRatio) =>
            val triangle = trianglePolygon((sizeRatio * width).toInt, x, y)
            g2d.fillPolygon(triangle)
        }
      }

    }

    private def nextColor =
      if (config.renderMonochromatic) Color.black.pure[F]
      else
        Random[F]
          .shuffleList(
            List(
              Color.red,
              Color.green,
              Color.blue,
              Color.yellow,
              Color.black,
              Color.cyan
            )
          )
          .map(_.head)

    private val width = 256
    private val height = 256
    private val squareRootOf3 = math.sqrt(3)

    private type GraphicsOp[A] = ReaderT[F, Graphics2D, A]

    private def liftOp(f: Graphics2D => F[Unit]): GraphicsOp[Unit] =
      ReaderT(f)

    private def liftPureOp(f: Graphics2D => Unit): GraphicsOp[Unit] =
      ReaderT(f.andThen(_.pure[F]))

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
