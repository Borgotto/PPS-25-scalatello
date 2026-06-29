package it.unibo.pps.model

import it.unibo.pps.model.board.{Board, Disk}
import it.unibo.pps.utils.{Color, Position}
import it.unibo.pps.utils.Color.*
import it.unibo.pps.utils.Shape.*
import it.unibo.pps.utils.Player.*
import it.unibo.pps.utils.MatchStatus.*
import org.mockito.ArgumentMatchersSugar.*
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

  val EXPECTED_INITIAL_BOARD: Seq[Disk] = List(
    Disk(Color.White, Position(SQUARE_SIZE / 2 - 1, SQUARE_SIZE / 2 - 1)),
    Disk(Color.Black, Position(SQUARE_SIZE / 2 - 1, SQUARE_SIZE / 2)),
    Disk(Color.Black, Position(SQUARE_SIZE / 2, SQUARE_SIZE / 2 - 1)),
    Disk(Color.White, Position(SQUARE_SIZE / 2, SQUARE_SIZE / 2))
  )

  // TODO(eboschetti): remove mocks
  val MOCK_BOARD: Board = mock[Board]

  "Square board" should "have correct shape and size" in:
    val logic = LogicImpl(SQUARE_SHAPE, USER_COLOR)
    val boardShape = logic.board.shape
    boardShape match
      case Square(n) => n should be(SQUARE_SIZE)
      case shape => fail(s"Board shape is $shape")

  "Rectangular board" should "have correct shape and size" in:
    val logic = LogicImpl(RECTANGULAR_SHAPE, USER_COLOR)
    val boardShape = logic.board.shape
    boardShape match
      case Rectangle(h, w) =>
        h should be(RECTANGLE_HEIGHT)
        w should be(RECTANGLE_WIDTH)
      case shape => fail(s"Board shape is $shape")

  "User" should "move first if assigned color black" in:
    val logic = LogicImpl(SQUARE_SHAPE, Black)
    val activePlayer = logic.activePlayer
    activePlayer match
      case User(color) => color should be(Black)
      case _ => fail("Opponent is set to move first")

  "Opponent" should "move first if user is assigned color white" in:
    val logic = LogicImpl(SQUARE_SHAPE, White)
    val activePlayer = logic.activePlayer
    activePlayer match
      case Opponent(color) => color should be(Black)
      case _ => fail("User is set to move first")

  "Match" should "initially be in progress" in:
    val logic = LogicImpl(SQUARE_SHAPE, USER_COLOR)
    logic.status should be(InProgress)

  "Initial board configuration" should "be correct" in:
    // TODO(eboschetti): remove mock usage
    when(MOCK_BOARD.disks) thenReturn EXPECTED_INITIAL_BOARD
    val logic = LogicImpl(SQUARE_SHAPE, USER_COLOR, MOCK_BOARD)
    logic.board.disks should be(EXPECTED_INITIAL_BOARD)

  "User move that does not capture any opponent disk" should "not be allowed" in:
    // TODO(eboschetti): remove mock usage
    when(MOCK_BOARD.placeDisk(*, *)) thenReturn MOCK_BOARD
    when(MOCK_BOARD.disks) thenReturn EXPECTED_INITIAL_BOARD
    val targetPosition = Position(0, 0)
    val logic = LogicImpl(SQUARE_SHAPE, USER_COLOR, MOCK_BOARD)
      .placeUserDisk(targetPosition)
    logic.board.disks should be(EXPECTED_INITIAL_BOARD)

  "User move that captures one opponent disk" should "be allowed and capture target disk" in:
    val targetPosition = Position(3, 2)
    val expectedBoard: Seq[Disk] = List(
      Disk(USER_COLOR, targetPosition),
      Disk(USER_COLOR, Position(SQUARE_SIZE / 2 - 1, SQUARE_SIZE / 2 - 1)),
      Disk(USER_COLOR, Position(SQUARE_SIZE / 2 - 1, SQUARE_SIZE / 2)),
      Disk(USER_COLOR, Position(SQUARE_SIZE / 2, SQUARE_SIZE / 2 - 1)),
      Disk(USER_COLOR.opposite, Position(SQUARE_SIZE / 2, SQUARE_SIZE / 2))
    )
    // TODO(eboschetti): remove mock usage
    when(MOCK_BOARD.placeDisk(*, *)) thenReturn MOCK_BOARD
    when(MOCK_BOARD.disks) thenReturn expectedBoard
    val logic = LogicImpl(SQUARE_SHAPE, USER_COLOR, MOCK_BOARD)
      .placeUserDisk(targetPosition)
    logic.board.disks should be(expectedBoard)
