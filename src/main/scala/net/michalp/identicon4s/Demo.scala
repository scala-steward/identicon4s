package net.michalp.identicon4s

import javax.imageio.ImageIO
import java.io.File
import cats.Id

object Demo extends App {
  val identicon = Identicon.defaultInstance[Id]

  def renderImage(identicon: Identicon[Id])(text: String) = {
    val image = identicon.generate(text)
    val f = new File(s"./output/$text.png");
    ImageIO.write(image, "png", f)
  }

  Seq("test", "lorem", "ipsum", "dolor", "99999").foreach(renderImage(identicon))
}
