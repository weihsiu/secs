package secs.examples.retained

import org.scalajs.dom
import org.scalajs.dom.*
import secs.Entity
import typings.three.*
import typings.three.cameras.Camera
import typings.three.materials.Material
import typings.three.math.Vector3

import scala.language.implicitConversions
import scala.scalajs.js

given Conversion[Vect3, math.Vector3] = v3 => math.Vector3(v3._1, v3._2, v3._3)

trait Renderer:
  def width: Double
  def height: Double
  def addCube(entity: Entity, cube: Cube, transform: Transform): Unit
  def updateCube(
      entity: Entity,
      cube: Cube,
      transform: Transform
  ): Unit
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
  val cameraControls = CameraControls(camera, canvasElem)
  val clock = core.Clock()

  def addCube(entity: Entity, cube: Cube, transform: Transform) =
    val geometry =
      geometries.BoxGeometry(
        cube.width,
        cube.width,
        cube.width,
        js.undefined,
        js.undefined,
        js.undefined
      )
    val material = materials.MeshPhongMaterial(new { color = 0xffffff })
    val mesh = objects.Mesh(geometry, material)
    mesh.scale.set.tupled((transform.scale, transform.scale, transform.scale))
    mesh.rotation.setFromVector3(transform.rotation, js.undefined)
    mesh.position.set.tupled(transform.position)
    scene.add(mesh)
    entities += entity -> mesh

  def updateCube(entity: Entity, cube: Cube, transform: Transform) =
    entities
      .get(entity)
      .foreach(mesh =>
        mesh.scale.set.tupled((transform.scale, transform.scale, transform.scale))
        mesh.rotation.setFromVector3(transform.rotation, js.undefined)
        mesh.position.set.tupled(transform.position)
      )

  def removeCube(entity: Entity) =
    entities
      .get(entity)
      .foreach(mesh =>
        scene.remove(mesh)
        mesh.geometry.dispose()
        mesh.material.asInstanceOf[Material].dispose()
      )

  def animateFrame(frame: Double => Unit) =
    dom.window.requestAnimationFrame(time =>
      frame(time)
      cameraControls.update(clock.getDelta())
      webglRenderer.render(scene, camera)
      animateFrame(frame)
    )
