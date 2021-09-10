package secs

import scala.Tuple.Map
import scala.compiletime.*
import scala.deriving.*

trait Component

trait ComponentMeta[+A]
object ComponentMeta:
  def apply[A: ComponentMeta]: ComponentMeta[A] = summon[ComponentMeta[A]]
  def derived[A]: ComponentMeta[A] = new ComponentMeta[A] {}

case class EntityC(entity: Entity) extends Component derives ComponentMeta

case class Label[L <: String](id: Int) extends Component
object Label:
  var metas = Map.empty[String, ComponentMeta[Label[?]]]
  inline def componentMeta[L <: String]: ComponentMeta[Label[L]] =
    metas
      .get(constValue[L])
      .fold({
        val meta = new ComponentMeta[Label[L]] {}
        metas = metas.updated(constValue[L], meta)
        meta
      })(m => m.asInstanceOf[ComponentMeta[Label[L]]])
inline given [L <: String]: ComponentMeta[Label[L]] = Label.componentMeta[L]
