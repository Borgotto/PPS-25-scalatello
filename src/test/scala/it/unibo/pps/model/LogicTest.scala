package it.unibo.pps.model

import it.unibo.pps.model.board.Board
import it.unibo.pps.state.BoardState
import it.unibo.pps.state.PlayerState.*
import it.unibo.pps.utils.{Color, Position, Shape}
import it.unibo.pps.utils.Color.*
import it.unibo.pps.utils.Shape.*
import it.unibo.pps.utils.MatchStatus.*
import org.mockito.MockitoSugar.{mock, when}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.be
import org.scalatest.matchers.should.Matchers.should
import it.unibo.pps.testutils.TestExtensions.*

class LogicTest extends AnyFlatSpec:

  val USER_COLOR: Color = Black

  val SQUARE_SIZE = 4
  val SQUARE_SHAPE: Shape = Square(SQUARE_SIZE)
  val INITIAL_SQUARE_BOARD: Board = """
    ....
    .WB.
    .BW.
    ....
  """.toBoard

  val RECTANGLE_HEIGHT = 4
  val RECTANGLE_WIDTH = 6
  val RECTANGULAR_SHAPE: Shape = Rectangle(RECTANGLE_HEIGHT, RECTANGLE_WIDTH)
  val INITIAL_RECTANGULAR_BOARD: Board = """
    ......
    ..WB..
    ..BW..
    ......
  """.toBoard

  // TODO(eboschetti): remove mocks
  val MOCK_BOARD: Board = mock[Board]
  val MOCK_BOARD_STATE: BoardState = mock[BoardState]

  private def mockBoardState(expectedBoard: Board): Unit =
    when(MOCK_BOARD.state) thenReturn expectedBoard.state

  "Square board" should "have correct shape and size" in:
    val logic = LogicImpl(SQUARE_SHAPE, USER_COLOR)
    val boardShape = logic.state.board.shape
    boardShape match
      case Square(n) => n should be(SQUARE_SIZE)
      case shape => fail(s"Board shape is $shape")

  "Rectangular board" should "have correct shape and size" in:
    val logic = LogicImpl(RECTANGULAR_SHAPE, USER_COLOR)
    val boardShape = logic.state.board.shape
    boardShape match
      case Rectangle(h, w) =>
        h should be(RECTANGLE_HEIGHT)
        w should be(RECTANGLE_WIDTH)
      case shape => fail(s"Board shape is $shape")

  "User" should "move first if assigned color black" in:
    val logic = LogicImpl(SQUARE_SHAPE, Black)
    val activePlayer = logic.state.activePlayer
    activePlayer match
      case User(color) => color should be(Black)
      case _ => fail("Opponent is set to move first")

  "Opponent" should "move first if user is assigned color white" in:
    val logic = LogicImpl(SQUARE_SHAPE, White)
    val activePlayer = logic.state.activePlayer
    activePlayer match
      case Opponent(color) => color should be(Black)
      case _ => fail("User is set to move first")

  "Match" should "initially be in progress" in:
    val logic = LogicImpl(SQUARE_SHAPE, USER_COLOR)
    logic.state.status should be(InProgress)

  "Initial board configuration" should "be correct" in:
    mockBoardState(INITIAL_SQUARE_BOARD)
    val logic = LogicImpl(USER_COLOR, MOCK_BOARD)
    logic.state.board.disks should be(INITIAL_SQUARE_BOARD.state.disks)

  "User move that does not capture any opponent disk" should "not be allowed" in:
    mockBoardState(INITIAL_SQUARE_BOARD)
    val targetPosition = Position(0, 0)
    val logic = LogicImpl(USER_COLOR, MOCK_BOARD)
      .placeUserDisk(targetPosition)
    logic.state.board.disks should be(INITIAL_SQUARE_BOARD.state.disks)

  "User move that captures one opponent disk horizontally" should "be allowed and capture target disk" in:
    val initialBoard = """
      ....
      .WB.
      .BW.
      ....
    """.toBoard
    val targetPosition = Position(1, 0)
    val expectedBoard: Board = """
      ....
      BBB.
      .BW.
      ....
    """.toBoard
    mockBoardState(expectedBoard)
    val logic = LogicImpl(USER_COLOR, MOCK_BOARD)
      .placeUserDisk(targetPosition)
    logic.state.board.disks should be(expectedBoard.state.disks)

  "User move that captures more than one opponent disk horizontally" should "be allowed and capture target disks" in :
    val initialBoard = """
      ....
      .WWB
      .BW.
      ....
    """.toBoard
    val targetPosition = Position(1, 0)
    val expectedBoard: Board = """
      ....
      BBBB
      .BW.
      ....
    """.toBoard
    mockBoardState(expectedBoard)
    val logic = LogicImpl(USER_COLOR, MOCK_BOARD)
      .placeUserDisk(targetPosition)
    logic.state.board.disks should be(expectedBoard.state.disks)

  "User move that captures one opponent disk vertically" should "be allowed and capture target disk" in:
    val initialBoard = """
      ....
      .WB.
      .BW.
      ....
    """.toBoard
    val targetPosition = Position(0, 1)
    val expectedBoard: Board = """
      .B..
      .BB.
      .BW.
      ....
    """.toBoard
    mockBoardState(expectedBoard)
    val logic = LogicImpl(USER_COLOR, MOCK_BOARD)
      .placeUserDisk(targetPosition)
    logic.state.board.disks should be(expectedBoard.state.disks)

  "User move that captures more than one opponent disk vertically" should "be allowed and capture target disks" in :
    val initialBoard = """
      ....
      .WW.
      .BW.
      ..B.
    """.toBoard
    val targetPosition = Position(0, 2)
    val expectedBoard: Board = """
      ..B.
      .WB.
      .BB.
      ..B.
    """.toBoard
    mockBoardState(expectedBoard)
    val logic = LogicImpl(USER_COLOR, MOCK_BOARD)
      .placeUserDisk(targetPosition)
    logic.state.board.disks should be(expectedBoard.state.disks)

  "User move that captures one opponent disk along forward diagonal" should "be allowed and capture target disk" in:
    val initialBoard = """
      ....
      .WB.
      BBW.
      ....
    """.toBoard
    val targetPosition = Position(0, 2)
    val expectedBoard: Board = """
      ..B.
      .BB.
      BBW.
      ....
    """.toBoard
    mockBoardState(expectedBoard)
    val logic = LogicImpl(USER_COLOR, MOCK_BOARD)
      .placeUserDisk(targetPosition)
    logic.state.board.disks should be(expectedBoard.state.disks)

  "User move that captures more than one opponent disk along forward diagonal" should "be allowed and capture target disks" in :
    val initialBoard = """
      ....
      .BW.
      .WB.
      B...
    """.toBoard
    val targetPosition = Position(0, 3)
    val expectedBoard: Board = """
      ...B
      .BB.
      .BB.
      B...
    """.toBoard
    mockBoardState(expectedBoard)
    val logic = LogicImpl(USER_COLOR, MOCK_BOARD)
      .placeUserDisk(targetPosition)
    logic.state.board.disks should be(expectedBoard.state.disks)

  "User move that captures one opponent disk along backward diagonal" should "be allowed and capture target disk" in:
    val initialBoard = """
       ....
       .WB.
       .BB.
       ....
     """.toBoard
    val targetPosition = Position(0, 0)
    val expectedBoard: Board = """
       B...
       .BB.
       .BB.
       ....
     """.toBoard
    mockBoardState(expectedBoard)
    val logic = LogicImpl(USER_COLOR, MOCK_BOARD)
      .placeUserDisk(targetPosition)
    logic.state.board.disks should be(expectedBoard.state.disks)

  "User move that captures more than one opponent disk along backward diagonal" should "be allowed and capture target disks" in :
    val initialBoard = """
      ....
      .WB.
      .BW.
      ...B
    """.toBoard
    val targetPosition = Position(0, 0)
    val expectedBoard: Board = """
      B...
      .BB.
      .BB.
      ...B
    """.toBoard
    mockBoardState(expectedBoard)
    val logic = LogicImpl(USER_COLOR, MOCK_BOARD)
      .placeUserDisk(targetPosition)
    logic.state.board.disks should be(expectedBoard.state.disks)

  "Opponent" should "move after the user when having available moves" in:
    val initialBoard = """
       ....
       .WB.
       .BW.
       ....
     """.toBoard
    val targetPosition = Position(1, 0)
    // TODO(eboschetti): remove mock usage
    val mockAvailablePlacements = Seq(Position(0, 0))
    when(MOCK_BOARD.isPlacementValid(USER_COLOR, targetPosition)) thenReturn true
    when(MOCK_BOARD.placeDisk(USER_COLOR, targetPosition)) thenReturn MOCK_BOARD
    when(MOCK_BOARD.captureDisks(targetPosition)) thenReturn MOCK_BOARD
    when(MOCK_BOARD.getAvailablePlacements(USER_COLOR.opposite)) thenReturn mockAvailablePlacements
    val logic = LogicImpl(USER_COLOR, MOCK_BOARD)
      .placeUserDisk(targetPosition)
    logic.state.activePlayer should be(Opponent(USER_COLOR.opposite))

  "Opponent" should "not move after the user when not having available moves" in:
    val initialBoard = """
      ....
      .WBW
      .BBW
      .BBB
     """.toBoard
    val targetPosition = Position(0, 3)
    // TODO(eboschetti): remove mock usage
    when(MOCK_BOARD.isPlacementValid(USER_COLOR, targetPosition)) thenReturn true
    when(MOCK_BOARD.placeDisk(USER_COLOR, targetPosition)) thenReturn MOCK_BOARD
    when(MOCK_BOARD.captureDisks(targetPosition)) thenReturn MOCK_BOARD
    when(MOCK_BOARD.getAvailablePlacements(USER_COLOR.opposite)) thenReturn Seq()
    val logic = LogicImpl(USER_COLOR, MOCK_BOARD)
      .placeUserDisk(targetPosition)
    logic.state.activePlayer should be(User(USER_COLOR))