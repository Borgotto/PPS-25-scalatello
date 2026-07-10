package it.unibo.pps.utils

import it.unibo.pps.state.{MatchState, PlayerState, BoardState}
import it.unibo.pps.utils.{MatchStatus, Shape, Color}
import it.unibo.pps.model.strategy.UserPlacementStrategy

object Serializer:
  trait Serializer[Class](
    val encode: Class => String,
    val decode: String => Class
  )

  object StringSerializer extends Serializer[String](identity, identity)

  // todo: implement proper serialization/deserialization for MatchState
  object MatchSerializer extends Serializer[MatchState](
    (matchState: MatchState) =>
      val status = matchState.status.toString
      val activePlayer = matchState.activePlayer.toString
      val board = matchState.board.toString
      s"$status,$activePlayer,$board"
    ,
    (data: String) =>
      MatchState(
        MatchStatus.InProgress,
        PlayerState.User(Color.Black, UserPlacementStrategy()),
        BoardState(
          Shape.Square(5),
          Seq.empty,
          Set.empty
        )
      )
  )
