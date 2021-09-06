package secs

case class EventSender[E]() extends Component:
  def send(event: E)(using CM: ComponentMeta[EventSender[E]], W: World): Unit = W.sendEvent(event)

case class EventReceiver[E]() extends Component:
  def receive(using CM: ComponentMeta[EventSender[E]], W: World): Iterable[E] = W.receiveEvents

trait EventSenderCM[E] extends ComponentMeta[EventSender[E]]
object EventSenderCM:
  def derived[E]: EventSenderCM[E] = new EventSenderCM[E] {}

trait EventReceiverCM[E] extends ComponentMeta[EventReceiver[E]]
object EventReceiverCM:
  def derived[E]: EventReceiverCM[E] = new EventReceiverCM[E] {}
