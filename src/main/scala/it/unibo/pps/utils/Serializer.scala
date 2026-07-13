package it.unibo.pps.utils

import it.unibo.pps.state.MatchState
import upickle.default.{read, write}

object Serializer:
  trait Serializer[Class](
    val encode: Class => String,
    val decode: String => Class
  )
  enum Serializers:
    case StringSerializer extends Serializers with Serializer[String](identity, identity)
    case MatchSerializer extends Serializers with Serializer[MatchState](write(_), read(_))
  given Serializer[String] = Serializers.StringSerializer
  given Serializer[MatchState] = Serializers.MatchSerializer
