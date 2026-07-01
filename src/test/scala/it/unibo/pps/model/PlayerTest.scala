package it.unibo.pps.model

import org.scalatest.flatspec.AnyFlatSpec
import org.mockito.MockitoSugar.mock
import org.mockito.IdiomaticMockito.returns

import it.unibo.pps.model.player.{Player, User, Opponent}
import it.unibo.pps.model.placementStrategy.{PlacementStrategy, UserPlacementStrategy, OpponentPlacementStrategy}

class PlayerTest extends AnyFlatSpec:

  "A User" should "return the correct placement strategy" in:
    val user: Player = User()
    val strategy = user.getPlacementStrategy
    assert(strategy.isInstanceOf[UserPlacementStrategy])

  "An Opponent" should "return the correct placement strategy" in:
    val opponent: Player = Opponent()
    val strategy = opponent.getPlacementStrategy
    assert(strategy.isInstanceOf[OpponentPlacementStrategy])