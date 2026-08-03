package it.unibo.pps.model.player

import it.unibo.pps.domain.Color
import it.unibo.pps.model.player.strategy.{ErraticPlacementStrategy, OpponentPlacementStrategy, SmartPlacementStrategy}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

/** Test suite for player. */
class PlayerTest extends AnyFlatSpec:

  "A Player" should "preserve its color" in:
    val user: User = User(Color.White)
    user.color shouldBe Color.White

  "An Opponent" should "return an OpponentPlacementStrategy" in:
    val opponent = Opponent.ErraticOpponent(Color.White)
    val strategy = opponent.strategy
    strategy shouldBe an [OpponentPlacementStrategy]

  it should "use the erratic strategy for ErraticOpponent" in:
    val opponent = Opponent.ErraticOpponent(Color.Black)
    opponent.strategy shouldBe a [ErraticPlacementStrategy]

  it should "use the expected smart strategy depth for predefined opponents" in:
    Opponent.EasyOpponent(Color.Black).strategy shouldBe SmartPlacementStrategy(Color.Black, 1)
    Opponent.MediumOpponent(Color.Black).strategy shouldBe SmartPlacementStrategy(Color.Black, 2)
    Opponent.HardOpponent(Color.Black).strategy shouldBe SmartPlacementStrategy(Color.Black, 4)

  it should "support unapply" in:
    val opponent: Opponent = Opponent.ErraticOpponent(Color.White)
    opponent match
      case Opponent(color) => color shouldBe Color.White
      case _ => fail("Unapply did not work as expected")