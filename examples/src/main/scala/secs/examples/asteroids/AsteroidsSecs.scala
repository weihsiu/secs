package secs.examples.asteroids

import org.scalajs.dom
import secs.{*, given}

import scala.scalajs.js

class AsteroidsSecs(context: dom.CanvasRenderingContext2D) extends Secs:
  case class Direction(direction: Double) extends Component derives ComponentMeta
  case class Movement(x: Double, y: Double, heading: Double, speed: Double) extends Component
      derives ComponentMeta

  val width = context.canvas.width
  val height = context.canvas.height

  inline def setup(using C: Command): Unit =
    C.spawnEntity()
      .insertComponent(Label["spaceship"](0))
      .insertComponent(Movement(width / 2, height / 2, 0, 0))
      .insertComponent(Direction(45))
    C.spawnEntity().insertComponent(Label["torpedo"](0)).insertComponent(Movement(0, 0, 30, 3))
    for i <- 0 to 4 do
      C.spawnEntity()
        .insertComponent(Label["asteroid"](i))
        .insertComponent(
          Movement(
            math.random * context.canvas.width,
            math.random * context.canvas.height,
            math.random * 360,
            1
          )
        )

  inline def updateMovements(using C: Command, Q: Query1[(EntityC, Movement)]): Unit =
    Q.result.foreach((e, m) =>
      var newX = m.x + math.cos(math.toRadians(m.heading)) * m.speed
      var newY = m.y + math.sin(math.toRadians(m.heading)) * m.speed
      if newX < 0 then newX += width else if newX >= width then newX -= width
      if newY < 0 then newY += height else if newY >= height then newY -= height
      C.entity(e.entity).updateComponent[Movement](_.copy(x = newX, y = newY))
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
      .getCs[(Label["spaceship"], Movement, Direction)]
      .foreach((l, m, d) =>
        context.save()
        context.strokeStyle = "white"
        context.translate(m.x, m.y)
        context.rotate(math.toRadians(d.direction))
        context.beginPath()
        context.moveTo(0, -10)
        context.lineTo(-5, 10)
        context.moveTo(0, -10)
        context.lineTo(5, 10)
        context.moveTo(-3, 6)
        context.lineTo(3, 6)
        context.stroke()
        context.restore()
      )
    components
      .getCs[(Label["torpedo"], Movement)]
      .foreach((l, m) =>
        context.fillStyle = "white"
        context.fillRect(m.x - 1, m.y - 1, 3, 3)
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
