package it.unibo.pps.model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{shouldBe, a}

import it.unibo.pps.model.player.{Player, User, Opponent}
import it.unibo.pps.model.placementStrategy.{UserPlacementStrategy, OpponentPlacementStrategy}

class PlayerTest extends AnyFlatSpec:

  "A User" should "return the correct placement strategy" in:
    val user: Player = User()
    val strategy = user.placementStrategy
    strategy shouldBe a [UserPlacementStrategy]

  "An Opponent" should "return the correct placement strategy" in:
    val opponent: Player = Opponent.RandomOpponent
    val strategy = opponent.placementStrategy
    strategy shouldBe a [OpponentPlacementStrategy]
