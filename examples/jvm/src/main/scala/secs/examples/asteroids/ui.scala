package secs.examples.asteroids

import scalafx.Includes.*
import scalafx.animation.AnimationTimer
import scalafx.scene.Scene
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.input.{KeyCode as FxKeyCode}
import scalafx.scene.paint.Paint

enum KeyCode(val value: FxKeyCode):
  case Space extends KeyCode(FxKeyCode.Space)
  case Left extends KeyCode(FxKeyCode.Left)
  case Up extends KeyCode(FxKeyCode.Up)
  case Right extends KeyCode(FxKeyCode.Right)
  case Down extends KeyCode(FxKeyCode.Down)

def scalafxKeyboard(scene: Scene): Keyboard = new:
  private var downKeys = Set.empty[FxKeyCode]
  scene.onKeyPressed = e => downKeys += e.getCode
  scene.onKeyReleased = e => downKeys -= e.getCode

  def keyDown(keyCode: KeyCode): Boolean = downKeys(keyCode.value)

def scalafxRenderer(context: GraphicsContext): Renderer = new:
  val width = context.getCanvas.getWidth

  val height = context.getCanvas.getHeight

  def strokeRect(color: String, x: Double, y: Double, width: Double, height: Double) =
    context.stroke = Paint.valueOf(color)
    context.strokeRect(x, y, width, height)

  def fillRect(color: String, x: Double, y: Double, width: Double, height: Double) =
    context.fill = Paint.valueOf(color)
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
    context.translate(x, y)
    context.scale(scale, scale)
    context.rotate(angle)
    context.beginPath()
    segments.foreach((x1, y1, x2, y2) =>
      context.moveTo(x1, y1)
      context.lineTo(x2, y2)
    )
    context.lineWidth /= scale
    context.closePath()
    context.stroke = color;
    context.strokePath()
    context.restore()

  def animateFrame(frame: Double => Unit) =
    val timer = AnimationTimer(now => frame(now / 1000000.0))
    timer.start()
