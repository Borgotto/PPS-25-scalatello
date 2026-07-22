package it.unibo.pps.model.player

import it.unibo.pps.domain.Color
import it.unibo.pps.model.strategy.*

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

class PlayerTest extends AnyFlatSpec:

  "A Player" should "preserve its color" in:
    val user: User = User(Color.White)
    user.color shouldBe Color.White

  "A User" should "return a UserPlacementStrategy" in:
    val user: Player = User(Color.Black)
    val strategy = user.strategy
    strategy shouldBe a [UserPlacementStrategy]

  "An Opponent" should "return an OpponentPlacementStrategy" in:
    val opponent: Player = Opponent.RandomOpponent(Color.White)
    val strategy = opponent.strategy
    strategy shouldBe an [OpponentPlacementStrategy]

  it should "use the random strategy for RandomOpponent" in:
    val opponent = Opponent.RandomOpponent(Color.Black)
    opponent.strategy shouldBe a [RandomPlacementStrategy]

  it should "use the expected smart strategy depth for predefined opponents" in:
    Opponent.EasyOpponent(Color.Black).strategy shouldBe SmartPlacementStrategy(Color.Black, 1)
    Opponent.MediumOpponent(Color.Black).strategy shouldBe SmartPlacementStrategy(Color.Black, 3)
    Opponent.HardOpponent(Color.Black).strategy shouldBe SmartPlacementStrategy(Color.Black, 5)

  it should "support unapply" in:
    val opponent: Opponent = Opponent.RandomOpponent(Color.White)
    opponent match
      case Opponent(color) => color shouldBe Color.White
      case _ => fail("Unapply did not work as expected")