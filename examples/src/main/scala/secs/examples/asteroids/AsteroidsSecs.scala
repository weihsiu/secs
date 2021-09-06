package secs.examples.asteroids

import org.scalajs.dom
import secs.{*, given}

import scala.scalajs.js

class AsteroidsSecs(context: dom.CanvasRenderingContext2D) extends Secs:
  case class Movement(x: Double, y: Double, heading: Double, speed: Double) extends Component
      derives ComponentMeta

  inline def setup(using C: Command): Unit =
    C.spawnEntity().insertComponent(Label["torpedo"]()).insertComponent(Movement(0, 0, 30, 3))
    for i <- 0 to 4 do
      C.spawnEntity()
        .insertComponent(Label["asteroid"]())
        .insertComponent(Movement(100, 100 * i, 45, 2))

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
      .getCs[(Label["torpedo"], Movement)]
      .foreach((l, m) =>
        context.fillStyle = "white"
        context.fillRect(m.x, m.y, 2, 2)
      )
    components
      .getCs[(Label["asteroid"], Movement)]
      .foreach((l, m) =>
        context.strokeStyle = "white"
        context.beginPath()
        context.arc(m.x, m.y, 30, 0, math.Pi * 2)
        context.stroke()
      )

  def afterRender() = ()
