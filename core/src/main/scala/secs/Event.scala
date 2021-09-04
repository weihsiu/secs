package secs

case class EventSender[E]() extends Component:
  def send(event: E)(using CM: ComponentMeta[EventSender[E]], W: World): Unit = W.sendEvent(event)

case class EventReceiver[E]() extends Component:
  def receive(using CM: ComponentMeta[EventSender[E]], W: World): Iterable[E] = W.receiveEvents

trait EventSenderCM[A] extends ComponentMeta[EventSender[A]]
object EventSenderCM:
  def derived[A]: EventSenderCM[A] = new EventSenderCM[A] {}

trait EventReceiverCM[A] extends ComponentMeta[EventReceiver[A]]
object EventReceiverCM:
  def derived[A]: EventReceiverCM[A] = new EventReceiverCM[A] {}
