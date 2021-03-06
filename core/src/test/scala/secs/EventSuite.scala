package secs

class EventSuite extends munit.FunSuite:
  case class MissleLaunched(id: Int) derives EventSenderCM, EventReceiverCM
  // given ComponentMeta[EventSender[MissleLaunched]]()
  // given ComponentMeta[EventReceiver[MissleLaunched]]()

  test("ComponentMetas for same EventSender is the same") {
    assertEquals(
      ComponentMeta[EventSender[MissleLaunched]],
      ComponentMeta[EventSender[MissleLaunched]]
    )
  }

  test("event send and receive") {
    inline def fireMissle(using Q: Query1[(EntityC, EventSender[MissleLaunched])]): Unit =
      // fire missle
      Q.result.foreach((_, eventSender) => eventSender.send(MissleLaunched(1)))
    inline def scanRadar(using Q: Query1[(EntityC, EventReceiver[MissleLaunched])]): Unit =
      Q.result.foreach((_, eventReceiver) =>
        eventReceiver.receive.foreach(e => assertEquals(MissleLaunched(1), e))
      )
    val command = summon[Command]
    command.spawnEntity().insertComponent(EventSender[MissleLaunched]())
    command.spawnEntity().insertComponent(EventReceiver[MissleLaunched]())
    fireMissle
    scanRadar
  }
