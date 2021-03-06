package secs.examples.asteroids

import secs.{*, given}

class AsteroidsSecs(keyboard: Keyboard, renderer: Renderer)
    extends Secs[SpawnedAndAlive.type *: EmptyTuple]:
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
    ),
    List(
      (-4.0, 8.0, 4.0, 8.0),
      (4.0, 8.0, 8.0, 1.0),
      (8.0, 1.0, 8.0, -5.0),
      (8.0, -5.0, -3.0, -8.0),
      (-3.0, -8.0, -3.0, -3.0),
      (-3.0, -3.0, -8.0, -3.0),
      (-8.0, -3.0, -4.0, 8.0)
    )
  )

  val spaceshipRadius = 10.0
  val asteroidRadius = 8.0

  def collide(
      pos1: (Double, Double),
      radius1: Double,
      pos2: (Double, Double),
      radius2: Double
  ): Boolean =
    val a = math.abs(pos1._1 - pos2._1)
    val o = math.abs(pos1._2 - pos2._2)
    math.sqrt(a * a + o * o) < radius1 + radius2

  def polarAdd(p1: (Double, Double), p2: (Double, Double)): (Double, Double) =
    import math.*
    (
      sqrt(p1._1 * p1._1 + p2._1 * p2._1 + 2 * p1._1 * p2._1 * cos(p2._2 - p1._2)),
      p1._2 + atan2(p2._1 * sin(p2._2 - p1._2), p1._1 + p2._1 * cos(p2._2 - p1._2))
    )

  case class TorpedoPosition(entity: Entity, pos: (Double, Double))
      derives EventSenderCM,
        EventReceiverCM
  case class SpaceshipPosition(entity: Entity, pos: (Double, Double))
      derives EventSenderCM,
        EventReceiverCM

  case class NewSpaceship(time: Double) extends Component derives ComponentMeta
  case class FlameOn() extends Component derives ComponentMeta
  case class Hyperspace(time: Double) extends Component derives ComponentMeta
  case class CoolOff(time: Double) extends Component derives ComponentMeta
  case class EndOfLife(time: Double) extends Component derives ComponentMeta
  case class Direction(direction: Double) extends Component derives ComponentMeta
  case class Scale(scale: Double) extends Component derives ComponentMeta
  case class Movement(pos: (Double, Double), heading: Double, speed: Double) extends Component
      derives ComponentMeta

  val width = renderer.width
  val height = renderer.height

  def spawnAsteroids(command: Command, scale: Double, pos: Option[(Double, Double)]): Unit =
    for i <- 0 to 4 do
      val p = pos.getOrElse((math.random * width, math.random * height))
      command
        .spawnEntity()
        .insertComponent(Label["asteroid"](i))
        .insertComponent(Movement(p, math.random * 360, 1.5))
        .insertComponent(Scale(scale))
        .insertComponent(EventReceiver[TorpedoPosition]())
        .insertComponent(EventReceiver[SpaceshipPosition]())

  def spawnDebris(command: Command, time: Double, pos: (Double, Double)): Unit =
    for i <- 0 to 5 do
      command
        .spawnEntity()
        .insertComponent(Label["debris"](0))
        .insertComponent(Movement(pos, math.random * 360, math.random * 2))
        .insertComponent(EndOfLife(time + 500))

  def spawnSpaceship(command: Command): Unit =
    command
      .spawnEntity()
      .insertComponent(Label["spaceship"](0))
      .insertComponent(Movement((width / 2, height / 2), -90, 0))
      .insertComponent(Direction(-90))
      .insertComponent(EventSender[SpaceshipPosition]())

  inline def setup(using C: Command): Unit =
    spawnSpaceship(C)
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
      // hyperspace
      if keyboard.keyDown(KeyCode.Down) then
        C.entity(e.entity)
          .updateComponent[Movement](_.copy(pos = (math.random() * width, math.random() * height)))
          .insertComponent(Hyperspace(time + 200))
      // fire torpedo
      if keyboard.keyDown(KeyCode.Space) && cO.isEmpty then
        C.entity(e.entity).insertComponent(CoolOff(time + 500))
        C.spawnEntity()
          .insertComponent(Label["torpedo"](0))
          .insertComponent(Movement(m.pos, d.direction, 3))
          .insertComponent(EndOfLife(time + 2000))
          .insertComponent(EventSender[TorpedoPosition]())
      // clean up CoolOff
      cO.foreach(c => if c.time < time then C.entity(e.entity).removeComponent[CoolOff]())
    )

  inline def despawnEndOfLives(
      time: Double
  )(using C: Command, Q: Query1[(EntityC, EndOfLife)]): Unit =
    Q.result.foreach((e, l) => if l.time < time then C.despawnEntity(e.entity))

  inline def updateMovements(time: Double)(using
      C: Command,
      Q: Query1[
        (
            EntityC,
            Movement,
            Option[Hyperspace],
            Option[EventSender[TorpedoPosition]],
            Option[EventSender[SpaceshipPosition]]
        )
      ]
  ): Unit =
    Q.result.foreach((e, m, hO, sO1, sO2) =>
      var newPos = (
        m.pos._1 + math.cos(math.toRadians(m.heading)) * m.speed,
        m.pos._2 + math.sin(math.toRadians(m.heading)) * m.speed
      )
      newPos = (
        if newPos._1 < 0 then newPos._1 + width
        else if newPos._1 >= width then newPos._1 - width
        else newPos._1,
        if newPos._2 < 0 then newPos._2 + height
        else if newPos._2 >= height then newPos._2 - height
        else newPos._2
      )
      C.entity(e.entity).updateComponent[Movement](_.copy(pos = newPos))
      sO1.foreach(_.send(TorpedoPosition(e.entity, newPos)))
      hO.fold(sO2.foreach(_.send(SpaceshipPosition(e.entity, newPos))))(h =>
        if h.time < time then C.entity(e.entity).removeComponent[Hyperspace]()
      )
    )

  inline def detectTorpedoHits(time: Double)(using
      C: Command,
      Q: Query1[(EntityC, Label["asteroid"], EventReceiver[TorpedoPosition], Movement, Scale)]
  ): Unit =
    Q.result.foreach((e, l, r, m, s) =>
      r.receive.foreach(p =>
        if collide(m.pos, asteroidRadius * s.scale, p.pos, 1) then
          C.despawnEntity(e.entity).despawnEntity(p.entity)
          spawnDebris(C, time, m.pos)
          if s.scale > 1.0 then spawnAsteroids(C, s.scale / 2, Some(m.pos))
      )
    )

  inline def detectSpaceshipCollision(time: Double)(using
      C: Command,
      Q: Query1[(Label["asteroid"], EventReceiver[SpaceshipPosition], Movement, Scale)]
  ): Unit =
    Q.result.foreach((l, r, m, s) =>
      r.receive.foreach(p =>
        if collide(m.pos, asteroidRadius * s.scale, p.pos, spaceshipRadius) then
          C.despawnEntity(p.entity)
          spawnDebris(C, time, p.pos)
          C.spawnEntity().insertComponent(NewSpaceship(time + 1000))
      )
    )

  inline def newSpaceship(
      time: Double
  )(using C: Command, Q: Query1[(EntityC, NewSpaceship)]): Unit =
    Q.result.foreach((e, n) =>
      if n.time < time then
        C.despawnEntity(e.entity)
        spawnSpaceship(C)
    )
  def init() =
    setup

  def tick(time: Double) =
    updateSpaceship(time)
    despawnEndOfLives(time)
    updateMovements(time)
    detectTorpedoHits(time)
    detectSpaceshipCollision(time)
    newSpaceship(time)

  def beforeRender() =
    renderer.fillRect("black", 0, 0, width, height)

  def renderEntity(
      entity: Entity,
      status: EntityStatus,
      components: Components,
      previousComponent: => Components
  ) =
    components
      .getComponents[(Label["spaceship"], Movement, Direction)]
      .foreach((l, m, d) =>
        if components.getComponent[Hyperspace].isEmpty then
          renderer.strokePolygon(1, d.direction, "white", m.pos._1, m.pos._2, spaceshipSegments)
          components
            .getComponent[FlameOn]
            .foreach(_ =>
              renderer.strokePolygon(1, d.direction, "white", m.pos._1, m.pos._2, flameSegments)
            )
      )
    components
      .getComponents[(Label["torpedo"], Movement)]
      .foreach((l, m) => renderer.fillRect("white", m.pos._1 - 1, m.pos._2 - 1, 3, 3))
    components
      .getComponents[(Label["asteroid"], Movement, Scale)]
      .foreach((l, m, s) =>
        renderer
          .strokePolygon(
            s.scale,
            360 / asteroidSegments.length * l.id,
            "white",
            m.pos._1,
            m.pos._2,
            asteroidSegments(l.id)
          )
      )
    components
      .getComponents[(Label["debris"], Movement)]
      .foreach((l, m) => renderer.fillRect("white", m.pos._1, m.pos._2, 2, 2))

  def afterRender() = ()
