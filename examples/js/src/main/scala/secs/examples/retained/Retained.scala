package secs.examples.retained

import org.scalajs.dom
import org.scalajs.dom.*
import secs.{*, given}

object Retained:
  def setup(width: Int, height: Int): html.Canvas =
    val canvas = dom.document.createElement("canvas").asInstanceOf[html.Canvas]
    dom.document.body.appendChild(canvas)
    canvas.width = width
    canvas.height = height
    canvas

  def main(args: Array[String]): Unit =
    val canvas = setup(800, 600)
    val secs = RetainedSecs(canvas)
    val ticker = Secs.start(secs)
    lazy val animateFrame: () => Unit = () =>
      dom.window.requestAnimationFrame(time =>
        ticker(time)
        animateFrame()
      )
    animateFrame()
