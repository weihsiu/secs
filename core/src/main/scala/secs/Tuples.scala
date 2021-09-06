package secs

import scala.Tuple.*

type IsSome[X] <: Boolean = X match
  case Some[?] => true
  case ?       => false

type ExtractSome[XS <: Tuple] = InverseMap[Filter[XS, IsSome], Some]

extension [XS <: Tuple](xs: XS)
  def extractSome: ExtractSome[XS] =
    def extract(xs: Tuple): Tuple =
      xs match
        case Some(x) *: xs => x *: extract(xs)
        case None *: xs    => extract(xs)
        case EmptyTuple    => EmptyTuple
    extract(xs).asInstanceOf[ExtractSome[XS]]

  def sequenceOptions: Option[ExtractSome[XS]] =
    val ys = xs.extractSome
    if xs.size == ys.size then Some(ys) else None

object Tuples:
  def extractSome[XS <: Tuple](xs: XS): ExtractSome[XS] = xs.extractSome

  def sequenceOptions[XS <: Tuple](xs: XS): Option[ExtractSome[XS]] =
    xs.sequenceOptions
