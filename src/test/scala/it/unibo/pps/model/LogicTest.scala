package it.unibo.pps.model

import it.unibo.pps.model.board.Board
import it.unibo.pps.state.{BoardState, DiskState}
import it.unibo.pps.state.PlayerState.*
import it.unibo.pps.utils.{Color, Position, Shape}
import it.unibo.pps.utils.Color.*
import it.unibo.pps.utils.Shape.*
import it.unibo.pps.utils.MatchStatus.*

import it.unibo.pps.testutils.TestExtensions.*

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.{an, be, not, thrownBy}
import org.scalatest.matchers.should.Matchers.should

import org.mockito.MockitoSugar.{mock, when}
import org.mockito.ArgumentMatchersSugar.*

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

  private def mockUserMove(
    mockBoardStateDisks: Seq[DiskState],
    mockOpponentPlacements: Seq[Position]
  ): Unit =
    when(MOCK_BOARD.state) thenReturn MOCK_BOARD_STATE
    when(MOCK_BOARD_STATE.disks) thenReturn mockBoardStateDisks
    when(MOCK_BOARD.isPlacementValid(any[Color](), any[Position]())) thenReturn true
    when(MOCK_BOARD.placeDisk(any[Color](), any[Position]())) thenReturn MOCK_BOARD
    when(MOCK_BOARD.captureDisks(any[Position]())) thenReturn MOCK_BOARD
    when(MOCK_BOARD.getAvailablePlacements(any[Color]())) thenReturn mockOpponentPlacements

  private def mockOpponentMove(
    mockBoardStateDisks: Seq[DiskState],
    mockOpponentPlacements: Seq[Position],
    mockUserPlacements: Seq[Position]
  ): Unit =
    when(MOCK_BOARD.state) thenReturn MOCK_BOARD_STATE
    when(MOCK_BOARD_STATE.disks) thenReturn mockBoardStateDisks
    when(MOCK_BOARD.isPlacementValid(any[Color](), any[Position]())) thenReturn true
    when(MOCK_BOARD.state.availablePlacements) thenReturn mockOpponentPlacements
    when(MOCK_BOARD.placeDisk(any[Color](), any[Position]())) thenReturn MOCK_BOARD
    when(MOCK_BOARD.captureDisks(any[Position]())) thenReturn MOCK_BOARD
    when(MOCK_BOARD.getAvailablePlacements(any[Color]())) thenReturn mockUserPlacements

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
    val userColor = Black
    val logic = LogicImpl(SQUARE_SHAPE, userColor)
    val activePlayer = logic.state.activePlayer
    activePlayer match
      case User(color) => color should be(Black)
      case _ => fail("Opponent is set to move first")

  "Opponent" should "move first if user is assigned color white" in:
    val userColor = White
    val logic = LogicImpl(SQUARE_SHAPE, userColor)
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

  "Trying to place user disk when it is opponent's turn" should "not be allowed" in:
    val userColor = White
    val targetPosition = Position(0, 0)
    val logic = LogicImpl(userColor, MOCK_BOARD)
    an [IllegalStateException] shouldBe thrownBy { logic.placeUserDisk(targetPosition) }

  "Trying to place opponent disk when it is user's turn" should "not be allowed" in :
    val userColor = Black
    val logic = LogicImpl(userColor, MOCK_BOARD)
    an [IllegalStateException] shouldBe thrownBy { logic.placeOpponentDisk() }

  "User move that does not capture any opponent disk" should "not be allowed" in:
    val userColor = Black
    mockBoardState(INITIAL_SQUARE_BOARD)
    val targetPosition = Position(0, 0)
    val logic = LogicImpl(userColor, MOCK_BOARD).placeUserDisk(targetPosition)
    logic.state.board.disks should be(INITIAL_SQUARE_BOARD.state.disks)

  "User move that captures one opponent disk horizontally" should "be allowed and capture target disk" in:
    val userColor = Black
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
    val logic = LogicImpl(userColor, MOCK_BOARD).placeUserDisk(targetPosition)
    logic.state.board.disks should be(expectedBoard.state.disks)

  "User move that captures more than one opponent disk horizontally" should "be allowed and capture target disks" in :
    val userColor = Black
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
    val logic = LogicImpl(userColor, MOCK_BOARD).placeUserDisk(targetPosition)
    logic.state.board.disks should be(expectedBoard.state.disks)

  "User move that captures one opponent disk vertically" should "be allowed and capture target disk" in:
    val userColor = Black
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
    val logic = LogicImpl(userColor, MOCK_BOARD).placeUserDisk(targetPosition)
    logic.state.board.disks should be(expectedBoard.state.disks)

  "User move that captures more than one opponent disk vertically" should "be allowed and capture target disks" in :
    val userColor = Black
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
    val logic = LogicImpl(userColor, MOCK_BOARD).placeUserDisk(targetPosition)
    logic.state.board.disks should be(expectedBoard.state.disks)

  "User move that captures one opponent disk along forward diagonal" should "be allowed and capture target disk" in:
    val userColor = Black
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
    val logic = LogicImpl(userColor, MOCK_BOARD).placeUserDisk(targetPosition)
    logic.state.board.disks should be(expectedBoard.state.disks)

  "User move that captures more than one opponent disk along forward diagonal" should "be allowed and capture target disks" in :
    val userColor = Black
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
    val logic = LogicImpl(userColor, MOCK_BOARD).placeUserDisk(targetPosition)
    logic.state.board.disks should be(expectedBoard.state.disks)

  "User move that captures one opponent disk along backward diagonal" should "be allowed and capture target disk" in:
    val userColor = Black
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
    val logic = LogicImpl(userColor, MOCK_BOARD).placeUserDisk(targetPosition)
    logic.state.board.disks should be(expectedBoard.state.disks)

  "User move that captures more than one opponent disk along backward diagonal" should "be allowed and capture target disks" in :
    val userColor = Black
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
    val logic = LogicImpl(userColor, MOCK_BOARD).placeUserDisk(targetPosition)
    logic.state.board.disks should be(expectedBoard.state.disks)

  "Opponent" should "move after the user when having available moves" in:
    val userColor = Black
    val initialBoard = """
       ....
       .WB.
       .BW.
       ....
     """.toBoard
    val targetPosition = Position(1, 0)
    val mockAvailablePlacements = Seq(Position(0, 0))
    mockUserMove(initialBoard.state.disks, mockAvailablePlacements)
    val logic = LogicImpl(userColor, MOCK_BOARD).placeUserDisk(targetPosition)
    logic.state.activePlayer should be(Opponent(USER_COLOR.opposite))

  "User" should "move again if opponent does not have available moves" in:
    val userColor = Black
    val initialBoard = """
      ....
      .WBW
      .BBW
      .BBB
     """.toBoard
    val targetPosition = Position(0, 3)
    val mockAvailablePlacements = Seq()
    mockUserMove(initialBoard.state.disks, mockAvailablePlacements)
    val logic = LogicImpl(userColor, MOCK_BOARD).placeUserDisk(targetPosition)
    logic.state.activePlayer should be(User(USER_COLOR))

  "User" should "move after opponent when having available moves" in :
    val userColor = White
    val initialBoard = """
       ....
       .WB.
       .BW.
       ....
     """.toBoard
    val mockOpponentPlacements = Seq(Position(0, 0))
    val mockUserPlacements = Seq(Position(0, 0))
    mockOpponentMove(initialBoard.state.disks, mockOpponentPlacements, mockUserPlacements)
    val logic = LogicImpl(userColor, MOCK_BOARD).placeOpponentDisk()
    logic.state.activePlayer should be(User(userColor))

  "Opponent" should "move again if user does not have available moves" in:
    val userColor = White
    val initialBoard = """
       BBBB
       BBBB
       .BW.
       ....
     """.toBoard
    val mockOpponentPlacements = Seq(Position(0, 0))
    val mockUserPlacements = Seq()
    mockOpponentMove(initialBoard.state.disks, mockOpponentPlacements, mockUserPlacements)
    val logic = LogicImpl(userColor, MOCK_BOARD).placeOpponentDisk()
    logic.state.activePlayer should be(Opponent(userColor.opposite))

  "Match" should "end if board is full" in:
    val userColor = Black
    val initialBoard = """
       BBBB
       BBBB
       BBBB
       BBW.
     """.toBoard
    val targetPosition = Position(3, 3)
    val expectedBoard: Board = """
      BBBB
      BBBB
      BBBB
      BBBB
    """.toBoard
    val mockAvailablePlacements = Seq()
    mockUserMove(expectedBoard.state.disks, mockAvailablePlacements)
    val logic = LogicImpl(userColor, MOCK_BOARD).placeUserDisk(targetPosition)
    logic.state.status should not be InProgress

  "Match" should "end if both players do not have available moves" in:
    val userColor = Black
    val initialBoard = """
       BBBB
       BBBB
       BBW.
       ....
     """.toBoard
    val targetPosition = Position(2, 3)
    val expectedBoard: Board ="""
      BBBB
      BBBB
      BBBB
      ....
    """.toBoard
    val mockAvailablePlacements = Seq()
    mockUserMove(expectedBoard.state.disks, mockAvailablePlacements)
    val logic = LogicImpl(userColor, MOCK_BOARD).placeUserDisk(targetPosition)
    logic.state.status should not be InProgress

  "User" should "win if the match is ended and he has more disks on the field than the opponent" in:
    val userColor = White
    val initialBoard = """
       WWWW
       WWWW
       WWWW
       WBW.
     """.toBoard
    val expectedBoard = """
       WWWW
       WWWW
       WWWW
       WBBB
     """.toBoard
    val mockOpponentPlacements = Seq(Position(3, 3))
    val mockUserPlacements = Seq()
    mockOpponentMove(expectedBoard.state.disks, mockOpponentPlacements, mockUserPlacements)
    val logic = LogicImpl(userColor, MOCK_BOARD).placeOpponentDisk()
    logic.state.status should be(UserWon)

  "Opponent" should "win if the match is ended and he has more disks on the field than the user" in :
    val userColor = Black
    val initialBoard = """
       WWWW
       WWWW
       WWWW
       WBW.
     """.toBoard
    val expectedBoard = """
       WWWW
       WWWW
       WWWW
       WBBB
     """.toBoard
    val mockAvailablePlacements = Seq()
    mockUserMove(expectedBoard.state.disks, mockAvailablePlacements)
    val targetPosition = Position(3, 3)
    val logic = LogicImpl(userColor, MOCK_BOARD).placeUserDisk(targetPosition)
    logic.state.status should be(OpponentWon)

  "Match result" should "be a tie if the match is ended and players have the same number of disks on the field" in:
    val userColor = Black
    val initialBoard = """
       WWWW
       BBWW
       BBWW
       BBW.
     """.toBoard
    val expectedBoard = """
       WWWW
       BBWW
       BBWW
       BBBB
     """.toBoard
    val mockAvailablePlacements = Seq()
    mockUserMove(expectedBoard.state.disks, mockAvailablePlacements)
    val targetPosition = Position(3, 3)
    val logic = LogicImpl(userColor, MOCK_BOARD).placeUserDisk(targetPosition)
    logic.state.status should be(Tie)
