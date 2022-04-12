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

import javax.imageio.ImageIO
import java.io.File
import cats.effect.IO
import cats.implicits._
import cats.effect.IOApp

object Demo extends IOApp.Simple {
  val identicon = Identicon.defaultInstance[IO]()

  def renderImage(identicon: Identicon[IO])(text: String) =
    identicon.generate(text).map { image =>
      val f = new File(s"./output/$text.png")
      ImageIO.write(image, "png", f)
    }

  def run =
    Seq("test", "lorem", "ipsum", "dolor", "99999").traverse_(renderImage(identicon))
}
