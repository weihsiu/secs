package secs.examples.asteroids

import org.scalajs.dom
import secs.{*, given}
import secs.BoolOps.*
import secs.examples.ui.*

import scala.scalajs.js
import org.scalajs.dom.ext.KeyCode

class AsteroidsSecs(keyboard: Keyboard, renderer: Renderer) extends Secs:
  val spaceshipSegments = List(
    (10.0, 0.0, -10.0, 5.0),
    (10.0, 0.0, -10.0, -5.0),
    (-6.0, 3.0, -6.0, -3.0)
  )
  val flameSegments = List(
    (-6.0, 3.0, -14.0, 0.0),
    (-6.0, -3.0, -14.0, 0.0)
  )
  val asteroidSegments = List(
    List(
      (0.0, 8.0, 6.0, 3.0),
      (6.0, 3.0, 3.0, 0.0),
      (3.0, 0.0, 7.0, -3.0),
      (7.0, -3.0, 1.0, -7.0),
      (1.0, -7.0, -5.0, -5.0),
      (-5.0, -5.0, -7.0, 3.0),
      (-7.0, 3.0, 0.0, 8.0)
    ),
    List(
      (0.0, 8.0, 3.0, 5.0),
      (3.0, 5.0, 5.0, 6.0),
      (5.0, 6.0, 7.0, 1.0),
      (7.0, 1.0, 3.0, -1.0),
      (3.0, -1.0, 4.0, -3.0),
      (4.0, -3.0, -3.0, -7.0),
      (-3.0, -7.0, -6.0, -2.0),
      (-6.0, -2.0, -3.0, 1.0),
      (-3.0, 1.0, -5.0, 5.0),
      (-5.0, 5.0, 0.0, 8.0)
    ),
    List(
      (0.0, 8.0, 4.0, 4.0),
      (4.0, 4.0, 2.0, 2.0),
      (2.0, 2.0, 6.0, -3.0),
      (6.0, -3.0, 0.0, -7.0),
      (0.0, -7.0, -2.0, -3.0),
      (-2.0, -3.0, -6.0, -5.0),
      (-6.0, -5.0, -7.0, 0.0),
      (-7.0, 0.0, -2.0, 2.0),
      (-2.0, 2.0, -5.0, 5.0),
      (-5.0, 5.0, 0.0, 8.0)
    ),
    List(
      (0.0, 8.0, 6.0, 3.0),
      (6.0, 3.0, 5.0, -3.0),
      (5.0, -3.0, 0.0, -7.0),
      (0.0, -7.0, -6.0, -1.0),
      (-6.0, -1.0, -2.0, 2.0),
      (-2.0, 2.0, -5.0, 6.0),
      (-5.0, 6.0, 0.0, 8.0)
    )
  )

  def boundingRect(
      x: Double,
      y: Double,
      scale: Double,
      segments: List[(Double, Double, Double, Double)]
  ): (Double, Double, Double, Double) =
    val ss =
      segments.map(s => (s._1 * scale + x, s._2 * scale + y, s._3 * scale + x, s._4 * scale + y))
    (ss.map(_._1).min, ss.map(_._2).min, ss.map(_._3).max, ss.map(_._4).max)

  def withinRect(x: Double, y: Double, rect: (Double, Double, Double, Double)): Boolean =
    x >= rect._1 && x < rect._3 && y >= rect._2 && y < rect._4

  def polarAdd(p1: (Double, Double), p2: (Double, Double)): (Double, Double) =
    import math.*
    (
      sqrt(p1._1 * p1._1 + p2._1 * p2._1 + 2 * p1._1 * p2._1 * cos(p2._2 - p1._2)),
      p1._2 + atan2(p2._1 * sin(p2._2 - p1._2), p1._1 + p2._1 * cos(p2._2 - p1._2))
    )

  case class TorpedoPos(x: Double, y: Double) derives EventSenderCM, EventReceiverCM

  case class FlameOn() extends Component derives ComponentMeta
  case class CoolOff(time: Double) extends Component derives ComponentMeta
  case class EndOfLife(time: Double) extends Component derives ComponentMeta
  case class Direction(direction: Double) extends Component derives ComponentMeta
  case class Scale(scale: Double) extends Component derives ComponentMeta
  case class Movement(x: Double, y: Double, heading: Double, speed: Double) extends Component
      derives ComponentMeta

  val width = renderer.width
  val height = renderer.height

  def spawnAsteroids(command: Command, scale: Double, pos: Option[(Double, Double)]): Unit =
    for i <- 0 to 3 do
      val p = pos.getOrElse((math.random * width, math.random * height))
      command
        .spawnEntity()
        .insertComponent(Label["asteroid"](i))
        .insertComponent(
          Movement(
            p._1,
            p._2,
            math.random * 360,
            1.5
          )
        )
        .insertComponent(Scale(scale))
        .insertComponent(EventReceiver[TorpedoPos]())

  def spawnDebris(command: Command, time: Double, x: Double, y: Double): Unit =
    for i <- 0 to 4 do
      command
        .spawnEntity()
        .insertComponent(Label["debris"](0))
        .insertComponent(Movement(x, y, math.random * 360, math.random * 2))
        .insertComponent(EndOfLife(time + 500))

  inline def setup(using C: Command): Unit =
    C.spawnEntity()
      .insertComponent(Label["spaceship"](0))
      .insertComponent(Movement(width / 2, height / 2, -90, 0))
      .insertComponent(Direction(-90))
    spawnAsteroids(C, 4, None)

  inline def updateSpaceship(time: Double)(using
      C: Command,
      Q: Query1[(EntityC, Label["spaceship"], Direction, Movement, Option[CoolOff])]
  ): Unit =
    Q.result.foreach((e, _, d, m, cO) =>
      // turn left / right
      if keyboard.keyDown(KeyCode.Left) || keyboard.keyDown(KeyCode.Right) then
        C.entity(e.entity)
          .updateComponent[Direction](d =>
            Direction(if keyboard.keyDown(KeyCode.Left) then d.direction - 3 else d.direction + 3)
          )
      // apply thrust
      if keyboard.keyDown(KeyCode.Up) then
        C.entity(e.entity)
          .updateComponent[Movement](m =>
            val (r, a) =
              polarAdd(
                (m.speed, math.toRadians(m.heading)),
                (0.03, math.toRadians(d.direction))
              )
            m.copy(speed = r, heading = math.toDegrees(a))
          )
          .insertComponent(FlameOn())
      else C.entity(e.entity).removeComponent[FlameOn]()
      // fire torpedo
      if keyboard.keyDown(KeyCode.Space) && cO.isEmpty then
        C.entity(e.entity).insertComponent(CoolOff(time + 500))
        C.spawnEntity()
          .insertComponent(Label["torpedo"](0))
          .insertComponent(Movement(m.x, m.y, d.direction, 3))
          .insertComponent(EndOfLife(time + 2000))
          .insertComponent(EventSender[TorpedoPos]())
      // clean up CoolOff
      cO.foreach(c => if c.time < time then C.entity(e.entity).removeComponent[CoolOff]())
    )

  inline def despawnEndOfLifes(
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

  inline def sendTorpedoPos(using Q: Query1[(EventSender[TorpedoPos], Movement)]): Unit =
    Q.result.foreach((s, m) => s.send(TorpedoPos(m.x, m.y)))

  inline def detectTorpedoHits(time: Double)(using
      C: Command,
      Q: Query1[(EntityC, Label["asteroid"], EventReceiver[TorpedoPos], Movement, Scale)]
  ): Unit =
    Q.result.foreach((e, l, r, m, s) =>
      r.receive.foreach(p =>
        if withinRect(p.x, p.y, boundingRect(m.x, m.y, s.scale, asteroidSegments(l.id))) then
          C.despawnEntity(e.entity)
          spawnDebris(C, time, m.x, m.y)
          if s.scale > 1.0 then spawnAsteroids(C, s.scale / 2, Some((m.x, m.y)))
      )
    )

  def init() =
    setup

  def tick(time: Double) =
    updateSpaceship(time)
    despawnEndOfLifes(time)
    updateMovements
    sendTorpedoPos
    detectTorpedoHits(time)

  def beforeRender() =
    renderer.fillRect("black", 0, 0, width, height)

  def renderEntity(entity: Entity, components: Map[ComponentMeta[Component], Component]) =
    components
      .getCs[(Label["spaceship"], Movement, Direction)]
      .foreach((l, m, d) =>
        renderer.strokePolygon(1, d.direction, "white", m.x, m.y, spaceshipSegments)
        if components.contains(ComponentMeta[FlameOn]) then
          renderer.strokePolygon(1, d.direction, "white", m.x, m.y, flameSegments)
      )
    components
      .getCs[(Label["torpedo"], Movement)]
      .foreach((l, m) => renderer.fillRect("white", m.x - 1, m.y - 1, 3, 3))
    components
      .getCs[(Label["asteroid"], Movement, Scale)]
      .foreach((l, m, s) =>
        renderer.strokePolygon(s.scale, 0, "white", m.x - 30, m.y - 30, asteroidSegments(l.id))
      )
    components
      .getCs[(Label["debris"], Movement)]
      .foreach((l, m) => renderer.fillRect("white", m.x, m.y, 2, 2))

  def afterRender() = ()
