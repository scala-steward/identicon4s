# Identicon4s

Simple scala library for generating identicons - visual hashes for arbitrary string.

## How it works

- Take a string as an input
- Generate hash
- Use the hash as a random seed
- Randomly choose the layout
- Fill the layout with randomly selected shapes
- Generate 2D image

## Usage

```scala
import net.michalp.identicon4s.Identicon
val identicon = Identicon.defaultInstance[Id]

val image = identicon.generate("test")
val f = new File(s"test.png");
ImageIO.write(image, "png", f)
```

Resulting image

![test.png](./images/test.png)