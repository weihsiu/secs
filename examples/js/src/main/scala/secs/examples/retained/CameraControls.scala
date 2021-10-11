package secs.examples.retained

import org.scalajs.dom
import typings.three.*

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", JSImport.Namespace)
object THREE extends js.Object

@js.native
@JSImport("camera-controls", JSImport.Default)
object CameraControls extends js.Object:
  def install(three: js.Object): Unit = js.native

@js.native
@JSImport("camera-controls", JSImport.Default)
class CameraControls(camera: cameras.Camera, element: dom.raw.HTMLElement) extends js.Object:
  def update(delta: Double): Boolean = js.native
