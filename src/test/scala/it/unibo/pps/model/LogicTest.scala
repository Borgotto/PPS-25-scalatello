package it.unibo.pps.model

import it.unibo.pps.model.board.Board
import it.unibo.pps.utils.{Color, Position}
import it.unibo.pps.utils.Color.*
import it.unibo.pps.utils.Shape.*
import it.unibo.pps.utils.Player.*
import it.unibo.pps.utils.MatchStatus.*
import org.mockito.MockitoSugar.{mock, when}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.be
import org.scalatest.matchers.should.Matchers.should
import it.unibo.pps.testutils.TestExtensions._

class LogicTest extends AnyFlatSpec:

  val USER_COLOR: Color = Black

  val SQUARE_SIZE = 4
  val SQUARE_SHAPE = Square(SQUARE_SIZE)
  val INITIAL_SQUARE_BOARD: Board = """
    ....
    .WB.
    .BW.
    ....
  """.toBoard

  val RECTANGLE_HEIGHT = 4
  val RECTANGLE_WIDTH = 6
  val RECTANGULAR_SHAPE = Rectangle(RECTANGLE_HEIGHT, RECTANGLE_WIDTH)
  val INITIAL_RECTANGULAR_BOARD: Board = """
    ......
    ..WB..
    ..BW..
    ......
  """.toBoard

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
    when(MOCK_BOARD.disks) thenReturn INITIAL_SQUARE_BOARD.disks
    val logic = LogicImpl(SQUARE_SHAPE, USER_COLOR, MOCK_BOARD)
    logic.board.disks should be(INITIAL_SQUARE_BOARD.disks)

  "User move that does not capture any opponent disk" should "not be allowed" in:
    when(MOCK_BOARD.disks) thenReturn INITIAL_SQUARE_BOARD.disks  // TODO(eboschetti): remove mock usage
    val targetPosition = Position(0, 0)
    val logic = LogicImpl(SQUARE_SHAPE, USER_COLOR, MOCK_BOARD)
      .placeUserDisk(targetPosition)
    logic.board.disks should be(INITIAL_SQUARE_BOARD.disks)

  "User move that captures one opponent disk horizontally" should "be allowed and capture target disk" in:
    val targetPosition = Position(3, 2)
    val expectedBoard: Board = """
      ....
      BBB.
      .BW.
      ....
    """.toBoard
    when(MOCK_BOARD.disks) thenReturn expectedBoard.disks   // TODO(eboschetti): remove mock usage
    val logic = LogicImpl(SQUARE_SHAPE, USER_COLOR, MOCK_BOARD)
      .placeUserDisk(targetPosition)
    logic.board.disks should be(expectedBoard.disks)

  "User move that captures one opponent disk vertically" should "be allowed and capture target disk" in :
    val targetPosition = Position(2, 3)
    val expectedBoard: Board = """
      .B..
      .BB.
      .BW.
      ....
    """.toBoard
    when(MOCK_BOARD.disks) thenReturn expectedBoard.disks // TODO(eboschetti): remove mock usage
    val logic = LogicImpl(SQUARE_SHAPE, USER_COLOR, MOCK_BOARD)
      .placeUserDisk(targetPosition)
    logic.board.disks should be(expectedBoard.disks)
