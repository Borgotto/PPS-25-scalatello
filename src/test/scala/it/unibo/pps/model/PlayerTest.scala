package it.unibo.pps.model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{a, shouldBe}
import it.unibo.pps.model.player.{Opponent, Player, User}
import it.unibo.pps.model.placementStrategy.{OpponentPlacementStrategy, UserPlacementStrategy}
import it.unibo.pps.utils.Color

class PlayerTest extends AnyFlatSpec:

  "A User" should "return the correct placement strategy" in:
    val user: Player = User(Color.Black)
    val strategy = user.placementStrategy
    strategy shouldBe a [UserPlacementStrategy]

  "An Opponent" should "return the correct placement strategy" in:
    val opponent: Player = Opponent.RandomOpponent(Color.White)
    val strategy = opponent.placementStrategy
    strategy shouldBe a [OpponentPlacementStrategy]
