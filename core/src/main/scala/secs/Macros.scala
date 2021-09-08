package secs

import scala.quoted.*

inline def isCaseClass[A]: Boolean = ${ isCaseClassImpl[A] }
private def isCaseClassImpl[A: Type](using qctx: Quotes): Expr[Boolean] =
  import qctx.reflect.*
  val sym = TypeRepr.of[A].typeSymbol
  Expr(sym.isClassDef && sym.flags.is(Flags.Case))

inline def companionObject[A]: String = ${ companionObjectImpl[A] }
private def companionObjectImpl[A: Type](using quotes: Quotes): Expr[String] =
  import quotes.reflect.*
  val sym = TypeRepr.of[A].typeSymbol
  Expr(sym.companionClass.fullName)
