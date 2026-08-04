package it.unibo.pps.utils

import it.unibo.pps.state.MatchState
import upickle.default.{read, write}

/**
 * Serialization utilities used by the [[controller.save.SaveManager]] system.
 *
 * This object provides a small type-class abstraction for converting values
 * to and from [[String]], together with predefined serializers for common project types.
 */
object Serializer:
  /**
   * Type-class describing how to encode and decode a value of type `Class`.
   *
   * @tparam Class the type that can be serialized/deserialized
   * @param encode function used to turn a value into a string
   * @param decode function used to restore a value from its string representation
   */
  trait Serializer[Class](
    val encode: Class => String,
    val decode: String => Class
  )

  /**
   * Built-in serializer implementations
   */
  enum Serializers:
    case StringSerializer extends Serializers with Serializer[String](identity, identity)
    case MatchSerializer extends Serializers with Serializer[MatchState](write(_), read(_))

  given Serializer[String] = Serializers.StringSerializer
  given Serializer[MatchState] = Serializers.MatchSerializer
