package secs.examples.asteroids

import org.scalajs.dom
import secs.*

import scala.scalajs.js

class AsteroidsSecs(context: dom.CanvasRenderingContext2D) extends Secs:
  case class Label(label: String) extends Component derives ComponentMeta
  case class Movement(x: Double, y: Double, heading: Double, speed: Double) extends Component
      derives ComponentMeta

  inline def setup(using C: Command): Unit =
    val t1 =
      C.spawnEntity().insertComponent(Label("torpedo")).insertComponent(Movement(0, 0, 30, 3))

  inline def updateMovements(using C: Command, Q: Query1[(EntityC, Movement)]): Unit =
    Q.result.foreach((e, m) =>
      C.entity(e.entity)
        .updateComponent[Movement](
          _.copy(
            x = m.x + math.cos(math.toRadians(m.heading)) * m.speed,
            y = m.y + math.sin(math.toRadians(m.heading)) * m.speed
          )
        )
    )

  def init() =
    setup

  def tick() =
    updateMovements

  def beforeRender() =
    context.fillStyle = "black"
    context.fillRect(0, 0, context.canvas.width, context.canvas.height)

  def renderEntity(entity: Entity, components: Map[ComponentMeta[Component], Component]) =
    components
      .getC[Movement]
      .foreach(m =>
        context.fillStyle = "white"
        context.fillRect(m.x, m.y, 2, 2)
      )

  def afterRender() = ()
