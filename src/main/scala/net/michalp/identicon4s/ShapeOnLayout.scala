package net.michalp.identicon4s

import Shapes.Shape

final case class ShapeOnLayout(shape: Shape, xPercentage: Double, yPercentage: Double) {
  assert(xPercentage >= 0 && xPercentage <= 1, "xPercentage must be between 0.0 and 1.0")
  assert(yPercentage >= 0 && yPercentage <= 1, "yPercentage must be between 0.0 and 1.0")
}