package it.unibo.pps.model

import org.scalatest.flatspec.AnyFlatSpec
import org.mockito.MockitoSugar.mock
import org.mockito.IdiomaticMockito.returns
import org.scalatest.matchers.should.Matchers.shouldEqual

import it.unibo.pps.model.placementStrategy.UserPlacementStrategy
import it.unibo.pps.model.player.User
import it.unibo.pps.utils.Position

class UserPlacementStrategyTest extends AnyFlatSpec:

  "UserPlacementStrategy" should "return the user choice" in:
    // The user choice represents the input coming from the view component
    given userChoice: Position = Position(3, 4)
    val user: User = mock[User]
    user.getPlacementStrategy returns UserPlacementStrategy()
    user.getPlacementStrategy.computePlacement shouldEqual userChoice
