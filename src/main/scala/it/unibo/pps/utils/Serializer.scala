package it.unibo.pps.utils

import it.unibo.pps.model.*
import it.unibo.pps.state.*
import it.unibo.pps.utils.*
import it.unibo.pps.model.strategy.*

import upickle.default.{read, write, ReadWriter as RW, macroRW as mRW}

object Serializer:
  trait Serializer[Class](
    val encode: Class => String,
    val decode: String => Class
  )

  object StringSerializer extends Serializer[String](identity, identity)

  object MatchSerializer extends Serializer[MatchState](MatchSerializer.encode, MatchSerializer.decode):
    override val encode: MatchState => String = write(_)
    override val decode: String => MatchState = read[MatchState](_)

    // Generate ReadWriters for MatchState's class components
    private given RW[Position] = mRW
    private given RW[UserPlacementStrategy] = mRW
    private given RW[OpponentPlacementStrategy] = mRW
    private given RW[RandomPlacementStrategy] = mRW
    private given RW[DiskState] = mRW
    private given RW[PlayerState.User] = mRW
    private given RW[PlayerState.Opponent] = mRW
    private given RW[PlayerState] = mRW
    private given RW[BoardState] = mRW
    // ... and finally for the MatchState itself
    private given RW[MatchState] = mRW
