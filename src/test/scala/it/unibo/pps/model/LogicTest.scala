package it.unibo.pps.model

import it.unibo.pps.model.board.Board
import it.unibo.pps.state.{BoardState, DiskState}
import it.unibo.pps.state.PlayerState.*
import it.unibo.pps.utils.{Color, Position}
import it.unibo.pps.utils.Color.*
import it.unibo.pps.utils.Shape.*
import it.unibo.pps.utils.Status.*
import org.mockito.MockitoSugar.{mock, when}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.be
import org.scalatest.matchers.should.Matchers.should

class LogicTest extends AnyFlatSpec:

  val SQUARE_SIZE = 8
  val SQUARE_SHAPE = Square(SQUARE_SIZE)

  val RECTANGLE_HEIGHT = 8
  val RECTANGLE_WIDTH = 10
  val RECTANGULAR_SHAPE = Rectangle(RECTANGLE_HEIGHT, RECTANGLE_WIDTH)

  val USER_COLOR: Color = Black

  val EXPECTED_INITIAL_BOARD_CONFIG: Seq[DiskState] = List(
    DiskState(Color.White, Position(SQUARE_SIZE / 2 - 1, SQUARE_SIZE / 2 - 1)),
    DiskState(Color.Black, Position(SQUARE_SIZE / 2 - 1, SQUARE_SIZE / 2)),
    DiskState(Color.Black, Position(SQUARE_SIZE / 2, SQUARE_SIZE / 2 - 1)),
    DiskState(Color.White, Position(SQUARE_SIZE / 2, SQUARE_SIZE / 2))
  )

  // TODO(eboschetti): remove mocks
  val MOCK_BOARD: Board = mock[Board]

  "Square board" should "have correct shape and size" in:
    val logic = LogicImpl(SQUARE_SHAPE, USER_COLOR)
    val boardShape = logic.matchState.board.shape
    boardShape match
      case Square(n) => n should be(SQUARE_SIZE)
      case shape => fail(s"Board shape is $shape")

  "Rectangular board" should "have correct shape and size" in:
    val logic = LogicImpl(RECTANGULAR_SHAPE, USER_COLOR)
    val boardShape = logic.matchState.board.shape
    boardShape match
      case Rectangle(h, w) =>
        h should be(RECTANGLE_HEIGHT)
        w should be(RECTANGLE_WIDTH)
      case shape => fail(s"Board shape is $shape")

  "User" should "move first if assigned color black" in:
    val logic = LogicImpl(SQUARE_SHAPE, Black)
    val activePlayer = logic.matchState.activePlayer
    activePlayer match
      case User(color) => color should be(Black)
      case _ => fail("Opponent is set to move first")

  "Opponent" should "move first if user is assigned color white" in:
    val logic = LogicImpl(SQUARE_SHAPE, White)
    val activePlayer = logic.matchState.activePlayer
    activePlayer match
      case Opponent(color) => color should be(Black)
      case _ => fail("User is set to move first")

  "Match" should "initially be in progress" in:
    val logic = LogicImpl(SQUARE_SHAPE, USER_COLOR)
    logic.matchState.status should be(InProgress)

  "Initial board configuration" should "be correct" in:
    val expectedBoardState = BoardState(SQUARE_SHAPE, EXPECTED_INITIAL_BOARD_CONFIG)
    // TODO(eboschetti): replace mock usage
    when(MOCK_BOARD.state) thenReturn expectedBoardState
    val logic = LogicImpl(SQUARE_SHAPE, USER_COLOR, MOCK_BOARD)
    logic.matchState.board.disks should be(EXPECTED_INITIAL_BOARD_CONFIG)
