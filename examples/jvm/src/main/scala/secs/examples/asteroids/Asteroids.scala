package secs.examples.asteroids

import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.beans.property.DoubleProperty.sfxDoubleProperty2jfx
import scalafx.geometry.Insets
import scalafx.scene.Group
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.effect.DropShadow
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color.*
import scalafx.scene.paint.Stop.sfxStop2jfx
import scalafx.scene.paint.*
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text

import scala.language.implicitConversions

object Asteroids extends JFXApp3:
  def start(): Unit =
    val canvas = new Canvas(800, 600)
    val rootPane = new Group
    stage = new PrimaryStage:
      title = "Canvas Doodle Test"
      scene = new Scene(800, 600):
        content = canvas
    val gc = canvas.graphicsContext2D
    reset(Color.Blue)
    canvas.onMouseDragged = (e: MouseEvent) => gc.clearRect(e.x - 2, e.y - 2, 5, 5)
    canvas.onMouseClicked = (e: MouseEvent) => if e.clickCount > 1 then reset(Color.Blue)
    def reset(color: Color): Unit =
      gc.fill = color
      gc.fillRect(0, 0, canvas.width.get, canvas.height.get)
