package secs.examples.asteroids

import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.raw.CanvasRenderingContext2D
import scala.scalajs.js
import secs.{*, given}

object Asteroids:
  def setup(width: Int, height: Int): CanvasRenderingContext2D =
    val canvas = dom.document.createElement("canvas").asInstanceOf[html.Canvas]
    dom.document.body.appendChild(canvas)
    canvas.width = width
    canvas.height = height
    canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

  def renderLoop(ticker: () => Unit): Unit =
    dom.window.requestAnimationFrame(_ =>
      ticker()
      renderLoop(ticker)
    )

  def main(args: Array[String]): Unit =
    val context = setup(800, 600)
    val secs = AsteroidsSecs(context)
    val ticker = Secs.start(secs)
    renderLoop(ticker)
