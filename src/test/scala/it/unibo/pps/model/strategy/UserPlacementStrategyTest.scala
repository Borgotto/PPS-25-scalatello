package it.unibo.pps.model.strategy

import it.unibo.pps.model.player.User
import it.unibo.pps.utils.Color.Black
import it.unibo.pps.utils.Position

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.shouldEqual

class UserPlacementStrategyTest extends AnyFlatSpec:

  "UserPlacementStrategy" should "return the user choice" in:
    // The user choice represents the input coming from the view component
    given userChoice: Position = Position(3, 4)
    val user: User = User(Black)
    user.strategy.computePlacement shouldEqual userChoice
