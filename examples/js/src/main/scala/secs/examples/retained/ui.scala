package secs.examples.retained

import org.scalajs.dom
import org.scalajs.dom.*
import scala.language.implicitConversions
import scala.scalajs.js
import secs.Entity
import typings.three.*

given Conversion[Vect3, math.Vector3] = v3 => math.Vector3(v3._1, v3._2, v3._3)
given Conversion[Vect3, math.Euler] = v3 => math.Euler(v3._1, v3._2, v3._3, js.undefined)

trait Renderer:
  def width: Double
  def height: Double
  def addCube(entity: Entity, cube: Cube): Unit
  def updateCube(entity: Entity, cube: Cube): Unit
  def removeCube(entity: Entity): Unit
  def animateFrame(frame: Double => Unit): Unit

def threeRenderer(canvasElem: html.Canvas): Renderer = new Renderer:
  var entities = Map.empty[Entity, objects.Mesh[?, ?]]
  val width = canvasElem.width
  val height = canvasElem.height
  val scene = scenes.Scene()
  val light = lights.DirectionalLight(0xffffff, 1)
  light.position.set(-1, 2, 4)
  scene.add(light)
  val camera = cameras.PerspectiveCamera(
    75,
    width / height,
    0.1,
    1000
  )
  camera.position.z = 5
  val webglRenderer = renderers.WebGLRenderer(new { canvas = canvasElem })
  webglRenderer.setSize(width, height)

  def addCube(entity: Entity, cube: Cube) =
    val geometry = geometries.BoxGeometry()
    val material = materials.MeshPhongMaterial(new { color = 0xffffff })
    val cube = objects.Mesh(geometry, material)
    scene.add(cube)
    entities += entity -> cube

  def updateCube(entity: Entity, cube: Cube) =
    entities
      .get(entity)
      .foreach(cubeMesh =>
        cubeMesh.position.set.tupled(cube.position)
        cubeMesh.rotation.set(cube.rotation._1, cube.rotation._2, cube.rotation._3, js.undefined)
      )
  def removeCube(entity: Entity) =
    entities.get(entity).foreach(scene.remove(_))

  def animateFrame(frame: Double => Unit) =
    dom.window.requestAnimationFrame(time =>
      frame(time)
      webglRenderer.render(scene, camera)
      animateFrame(frame)
    )
