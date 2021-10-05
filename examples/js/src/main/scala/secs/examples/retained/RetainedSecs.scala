package secs.examples.retained

import org.scalajs.dom.*
import secs.{*, given}
import typings.three.*

class RetainedSecs(canvasElem: html.Canvas)
    extends Secs[(Spawned.type, Alive.type, Despawned.type)]:
  val scene = scenes.Scene()
  val light = lights.DirectionalLight(0xffffff, 1)
  light.position.set(-1, 2, 4)
  scene.add(light)
  val camera = cameras.PerspectiveCamera(
    75,
    canvasElem.width / canvasElem.height,
    0.1,
    1000
  )
  camera.position.z = 5
  val renderer = renderers.WebGLRenderer(new { canvas = canvasElem })
  renderer.setSize(canvasElem.width, canvasElem.height)

def init() = ()
def tick(time: Double) = ()
def beforeRender() = ()
def renderEntity(
    entity: Entity,
    status: EntityStatus,
    components: Components,
    previousComponents: => Components
) = ???
def afterRender() = ???
