package secs.examples.asteroids

import org.scalajs.dom
import secs.{*, given}

import scala.scalajs.js
import org.scalajs.dom.ext.KeyCode

class AsteroidsSecs(context: dom.CanvasRenderingContext2D) extends Secs:
  def polarAdd(p1: (Double, Double), p2: (Double, Double)): (Double, Double) =
    import math.*
    (
      sqrt(p1._1 * p1._1 + p2._1 * p2._1 + 2 * p1._1 * p2._1 * cos(p2._2 - p1._2)),
      p1._2 + atan2(p2._1 * sin(p2._2 - p1._2), p1._1 + p2._1 * cos(p2._2 - p1._2))
    )

  case class EndOfLife(time: Double) extends Component derives ComponentMeta
  case class Direction(direction: Double) extends Component derives ComponentMeta
  case class Movement(x: Double, y: Double, heading: Double, speed: Double) extends Component
      derives ComponentMeta

  val width = context.canvas.width
  val height = context.canvas.height

  inline def setup(using C: Command): Unit =
    C.spawnEntity()
      .insertComponent(Label["spaceship"](0))
      .insertComponent(Movement(width / 2, height / 2, -90, 0))
      .insertComponent(Direction(-90))
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

  inline def updateSpaceship(time: Double)(using
      C: Command,
      Q: Query1[(EntityC, Label["spaceship"], Direction, Movement)]
  ): Unit =
    Q.result.foreach((e, l, d, m) =>
      if Keyboard.keyDown(KeyCode.Left) || Keyboard.keyDown(KeyCode.Right) then
        C.entity(e.entity)
          .updateComponent[Direction](d =>
            Direction(if Keyboard.keyDown(KeyCode.Left) then d.direction - 2 else d.direction + 2)
          )
      if Keyboard.keyDown(KeyCode.Up) then
        C.entity(e.entity)
          .updateComponent[Movement](m =>
            val (r, a) =
              polarAdd(
                (m.speed, math.toRadians(m.heading)),
                (0.03, math.toRadians(d.direction))
              )
            m.copy(speed = r, heading = math.toDegrees(a))
          )
      if Keyboard.keyDown(KeyCode.Space) then
        C.spawnEntity()
          .insertComponent(Label["torpedo"](0))
          .insertComponent(Movement(m.x, m.y, d.direction, 3))
          .insertComponent(EndOfLife(time + 2000))
    )

  inline def removeEndOfLifes(
      time: Double
  )(using C: Command, Q: Query1[(EntityC, EndOfLife)]): Unit =
    Q.result.foreach((e, l) => if l.time < time then C.despawnEntity(e.entity))

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

  def tick(time: Double) =
    updateSpaceship(time)
    removeEndOfLifes(time)
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
        context.moveTo(10, 0)
        context.lineTo(-10, 5)
        context.moveTo(10, 0)
        context.lineTo(-10, -5)
        context.moveTo(-6, 3)
        context.lineTo(-6, -3)
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
