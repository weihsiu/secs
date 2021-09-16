package secs

import scala.compiletime.*

enum Decorator:
  case Added[A <: Component](cm: ComponentMeta[?])
  case Changed[A <: Component](cm: ComponentMeta[?])
export Decorator.*

enum BoolOps:
  case ¬[A <: BoolOps | Decorator | Component](x: A | Decorator | ComponentMeta[?])
  case ∧[A <: BoolOps | Decorator | Component, B <: BoolOps | Decorator | Component](
      x: A | Decorator | ComponentMeta[?],
      y: B | Decorator | ComponentMeta[?]
  )
  case ∨[A <: BoolOps | Decorator | Component, B <: BoolOps | Decorator | Component](
      x: A | Decorator | ComponentMeta[?],
      y: B | Decorator | ComponentMeta[?]
  )
export BoolOps.*

trait Filter[OS <: BoolOps]:
  inline def boolOps: OS

inline given [OS <: BoolOps]: Filter[OS] with
  type NotInner[OS] = OS match
    case ¬[x] => x
  type AndFirstInner[OS] = OS match
    case ∧[x, ?] => x
  type AndSecondInner[OS] = OS match
    case ∧[?, x] => x
  type OrFirstInner[OS] = OS match
    case ∨[x, ?] => x
  type OrSecondInner[OS] = OS match
    case ∨[?, x] => x

  inline def toBoolOps[OS]: BoolOps =
    inline erasedValue[OS] match
      case _: ¬[Added[c]] => ¬(Added(summonInline[ComponentMeta[c]]))
      case _: ¬[Changed[c]] =>
        ¬(Changed(summonInline[ComponentMeta[c]]))
      case _: ¬[? <: Component] =>
        ¬(summonInline[ComponentMeta[NotInner[OS]]])
      case _: ¬[x] => ¬(toBoolOps[x])
      case _: ∧[Added[c1], Added[c2]] =>
        ∧(Added(summonInline[ComponentMeta[c1]]), Added(summonInline[ComponentMeta[c2]]))
      case _: ∧[Added[c1], Changed[c2]] =>
        ∧(Added(summonInline[ComponentMeta[c1]]), Changed(summonInline[ComponentMeta[c2]]))
      case _: ∧[Changed[c1], Added[c2]] =>
        ∧(Changed(summonInline[ComponentMeta[c1]]), Added(summonInline[ComponentMeta[c2]]))
      case _: ∧[Changed[c1], Changed[c2]] =>
        ∧(Changed(summonInline[ComponentMeta[c1]]), Changed(summonInline[ComponentMeta[c2]]))
      // case _: ∧[? <: Component, Added[c]] =>
      //   ∧(
      //     summonInline[ComponentMeta[AndFirstInner[OS]]],
      //     Added(summonInline[ComponentMeta[c]])
      //   )
      // case _: ∧[Added[c], ? <: Component] =>
      //   ∧(
      //     Added(summonInline[ComponentMeta[c]]),
      //     summonInline[ComponentMeta[AndSecondInner[OS]]]
      //   )
      // case _: ∧[? <: Component, Changed[c]] =>
      //   ∧(
      //     summonInline[ComponentMeta[AndFirstInner[OS]]],
      //     Changed(summonInline[ComponentMeta[c]])
      //   )
      // case _: ∧[Changed[c], ? <: Component] =>
      //   ∧(
      //     Changed(summonInline[ComponentMeta[c]]),
      //     summonInline[ComponentMeta[AndSecondInner[OS]]]
      //   )
      case _: ∧[Added[c], x]   => ∧(Added(summonInline[ComponentMeta[c]]), toBoolOps[x])
      case _: ∧[x, Added[c]]   => ∧(toBoolOps[x], Added(summonInline[ComponentMeta[c]]))
      case _: ∧[Changed[c], x] => ∧(Changed(summonInline[ComponentMeta[c]]), toBoolOps[x])
      case _: ∧[x, Changed[c]] => ∧(toBoolOps[x], Changed(summonInline[ComponentMeta[c]]))
      case _: ∧[? <: Component, ? <: Component] =>
        ∧(
          summonInline[ComponentMeta[AndFirstInner[OS]]],
          summonInline[ComponentMeta[AndSecondInner[OS]]]
        )
      case _: ∧[? <: Component, x] =>
        ∧(summonInline[ComponentMeta[AndFirstInner[OS]]], toBoolOps[x])
      case _: ∧[x, ? <: Component] =>
        ∧(toBoolOps[x], summonInline[ComponentMeta[AndSecondInner[OS]]])
      case _: ∧[x, y] => ∧(toBoolOps[x], toBoolOps[y])
      case _: ∨[Added[c1], Added[c2]] =>
        ∨(Added(summonInline[ComponentMeta[c1]]), Added(summonInline[ComponentMeta[c2]]))
      case _: ∨[Added[c1], Changed[c2]] =>
        ∨(Added(summonInline[ComponentMeta[c1]]), Changed(summonInline[ComponentMeta[c2]]))
      case _: ∨[Changed[c1], Added[c2]] =>
        ∨(Changed(summonInline[ComponentMeta[c1]]), Added(summonInline[ComponentMeta[c2]]))
      case _: ∨[Changed[c1], Changed[c2]] =>
        ∨(Changed(summonInline[ComponentMeta[c1]]), Changed(summonInline[ComponentMeta[c2]]))
      // case _: ∨[? <: Component, Added[c]] =>
      //   ∨(
      //     summonInline[ComponentMeta[OrFirstInner[OS]]],
      //     Added(summonInline[ComponentMeta[c]])
      //   )
      // case _: ∨[Added[c], ? <: Component] =>
      //   ∨(
      //     Added(summonInline[ComponentMeta[c]]),
      //     summonInline[ComponentMeta[OrSecondInner[OS]]]
      //   )
      // case _: ∨[? <: Component, Changed[c]] =>
      //   ∨(
      //     summonInline[ComponentMeta[OrFirstInner[OS]]],
      //     Changed(summonInline[ComponentMeta[c]])
      //   )
      // case _: ∨[Changed[c], ? <: Component] =>
      //   ∨(
      //     Changed(summonInline[ComponentMeta[c]]),
      //     summonInline[ComponentMeta[OrSecondInner[OS]]]
      //   )
      case _: ∨[Added[c], x]   => ∨(Added(summonInline[ComponentMeta[c]]), toBoolOps[x])
      case _: ∨[x, Added[c]]   => ∨(toBoolOps[x], Added(summonInline[ComponentMeta[c]]))
      case _: ∨[Changed[c], x] => ∨(Changed(summonInline[ComponentMeta[c]]), toBoolOps[x])
      case _: ∨[x, Changed[c]] => ∨(toBoolOps[x], Changed(summonInline[ComponentMeta[c]]))
      case _: ∨[? <: Component, ? <: Component] =>
        ∨(
          summonInline[ComponentMeta[OrFirstInner[OS]]],
          summonInline[ComponentMeta[OrSecondInner[OS]]]
        )
      case _: ∨[? <: Component, x] =>
        ∨(summonInline[ComponentMeta[OrFirstInner[OS]]], toBoolOps[x])
      case _: ∨[x, ? <: Component] =>
        ∨(toBoolOps[x], summonInline[ComponentMeta[OrSecondInner[OS]]])
      case _: ∨[x, y] => ∨(toBoolOps[x], toBoolOps[y])

  inline def boolOps = toBoolOps[OS].asInstanceOf[OS]
