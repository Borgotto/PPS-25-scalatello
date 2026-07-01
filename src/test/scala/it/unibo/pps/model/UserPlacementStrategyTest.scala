package it.unibo.pps.model

import org.scalatest.flatspec.AnyFlatSpec
import org.mockito.MockitoSugar.mock
import org.mockito.IdiomaticMockito.returns

import it.unibo.pps.model.placementStrategy.UserPlacementStrategy
import it.unibo.pps.model.player.User
import it.unibo.pps.utils.Position

class UserPlacementStrategyTest extends AnyFlatSpec:

  val user: User = mock[User]
  user.getPlacementStrategy returns UserPlacementStrategy()

  "UserPlacementStrategy" should "return the user choice" in:
    // The user choice represents the input coming from the view component
    given userChoice: Position = Position(3, 4)

    user.getPlacementStrategy.computePlacement match
      case result: Position => if result equals userChoice then succeed: Unit
      case _ => fail(s"The strategy's result is not the expected user choice")
