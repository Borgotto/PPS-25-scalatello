package it.unibo.pps.model.player

import it.unibo.pps.domain.Color
import it.unibo.pps.model.strategy.{OpponentPlacementStrategy, UserPlacementStrategy}
import it.unibo.pps.model.player.{Opponent, Player, User}

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{a, shouldBe}

class PlayerTest extends AnyFlatSpec:

  "A User" should "return the correct placement strategy" in:
    val user: Player = User(Color.Black)
    val strategy = user.strategy
    strategy shouldBe a [UserPlacementStrategy]

  "An Opponent" should "return the correct placement strategy" in:
    val opponent: Player = Opponent.RandomOpponent(Color.White)
    val strategy = opponent.strategy
    strategy shouldBe a [OpponentPlacementStrategy]
