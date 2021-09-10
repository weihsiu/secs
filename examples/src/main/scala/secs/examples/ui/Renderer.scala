package secs.examples.ui

import org.scalajs.dom

trait Renderer:
  def width: Double
  def height: Double
  def strokeRect(color: String, x: Double, y: Double, width: Double, height: Double): Unit
  def fillRect(color: String, x: Double, y: Double, width: Double, height: Double): Unit
  def strokePolygon(
      angle: Double,
      color: String,
      x: Double,
      y: Double,
      segments: List[(Double, Double, Double, Double)]
  ): Unit
  def animateFrame(frame: Double => Unit): Unit

object Renderer:
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
        angle: Double,
        color: String,
        x: Double,
        y: Double,
        segments: List[(Double, Double, Double, Double)]
    ) =
      context.save()
      context.strokeStyle = color
      context.translate(x, y)
      context.rotate(math.toRadians(angle))
      context.beginPath()
      segments.foreach((x1, y1, x2, y2) =>
        context.moveTo(x1, y1)
        context.lineTo(x2, y2)
      )
      context.stroke()
      context.restore()

    def animateFrame(frame: Double => Unit) =
      dom.window.requestAnimationFrame(time =>
        frame(time)
        animateFrame(frame)
      )
