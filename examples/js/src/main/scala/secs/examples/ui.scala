package secs.examples.ui

import org.scalajs.dom

enum KeyCode(val value: Int):
  case Space extends KeyCode(32)
  case Left extends KeyCode(37)
  case Up extends KeyCode(38)
  case Right extends KeyCode(39)

val htmlKeyboard = new Keyboard:
  private var downKeys = Set.empty[Int]
  dom.document.onkeydown = e => downKeys += e.keyCode
  dom.document.onkeyup = e => downKeys -= e.keyCode

  def keyDown(keyCode: KeyCode): Boolean = downKeys(keyCode.value)

def htmlCanvasRenderer(context: dom.CanvasRenderingContext2D): Renderer = new Renderer:
  val width = context.canvas.width

  val height = context.canvas.height

  def strokeRect(color: String, x: Double, y: Double, width: Double, height: Double) =
    context.strokeStyle = color
    context.strokeRect(x, y, width, height)

  def fillRect(color: String, x: Double, y: Double, width: Double, height: Double) =
    context.fillStyle = color
    context.fillRect(x, y, width, height)

  def strokePolygon(
      scale: Double,
      angle: Double,
      color: String,
      x: Double,
      y: Double,
      segments: List[(Double, Double, Double, Double)]
  ) =
    context.save()
    context.strokeStyle = color
    context.translate(x, y)
    context.scale(scale, scale)
    context.rotate(math.toRadians(angle))
    context.beginPath()
    segments.foreach((x1, y1, x2, y2) =>
      context.moveTo(x1, y1)
      context.lineTo(x2, y2)
    )
    context.lineWidth /= scale
    context.stroke()
    context.restore()

  def animateFrame(frame: Double => Unit) =
    dom.window.requestAnimationFrame(time =>
      frame(time)
      animateFrame(frame)
    )
