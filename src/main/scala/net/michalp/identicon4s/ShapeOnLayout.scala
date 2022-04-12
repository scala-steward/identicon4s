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

import Shapes.Shape

final case class RelativePosition(xPercentage: Double, yPercentage: Double) {
  assert(xPercentage >= 0 && xPercentage <= 1, "xPercentage must be between 0.0 and 1.0")
  assert(yPercentage >= 0 && yPercentage <= 1, "yPercentage must be between 0.0 and 1.0")
}

final case class ShapeOnLayout(shape: Shape, position: RelativePosition)
