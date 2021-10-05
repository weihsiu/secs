package secs.examples.retained

import org.scalajs.dom.*

trait Renderer:
  def canvas: html.Canvas
  def width: Double
  def height: Double
  def animateFrame(frame: Double => Unit): Unit

def threeRenderer(canvasElem: html.Canvas): Renderer = new Renderer:
  val canvas = canvasElem
  val width = canvas.width
  val height = canvas.height
  def animateFrame(frame: Double => Unit) =
    dom.window.requestAnimationFrame(time =>
      frame(time)
      animateFrame(frame)
    )
