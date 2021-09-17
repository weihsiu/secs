package secs.examples.asteroids

import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.canvas.GraphicsContext
import secs.{*, given}
import secs.examples.ui.*

object Asteroids extends JFXApp3:
  System.setProperty(
    "quantum.multithreaded",
    "false"
  ) // to slow down the psychotic frame rate in ubuntu

  def setup(width: Int, height: Int): (Scene, GraphicsContext) =
    val canvas = new Canvas(width, height)
    val scene1 = new Scene(width, height):
      content = canvas
    stage = new PrimaryStage:
      title = "Asteroids"
      scene = scene1
    (scene1, canvas.graphicsContext2D)

  def start(): Unit =
    val (scene, context) = setup(800, 600)
    val keyboard = scalafxKeyboard(scene)
    val renderer = scalafxRenderer(context)
    val secs = AsteroidsSecs(keyboard, renderer)
    val ticker = Secs.start(secs)
    renderer.animateFrame(ticker)
