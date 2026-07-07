package it.unibo.pps.model

import it.unibo.pps.model.board.Board
import it.unibo.pps.state.PlayerState.*
import it.unibo.pps.utils.{Color, Position, Shape}
import it.unibo.pps.utils.Color.*
import it.unibo.pps.utils.Shape.*
import it.unibo.pps.utils.MatchStatus.*

import it.unibo.pps.testutils.TestExtensions.*

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.{an, be, not, thrownBy}
import org.scalatest.matchers.should.Matchers.{should, shouldBe}

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
      case User(color, _) => color should be(Black)
      case _ => fail("Opponent is set to move first")

  "Opponent" should "move first if user is assigned color white" in:
    val userColor = White
    val logic = LogicImpl(SQUARE_SHAPE, userColor)
    val activePlayer = logic.state.activePlayer
    activePlayer match
      case Opponent(color, _) => color should be(Black)
      case _ => fail("User is set to move first")

  "Match" should "initially be in progress" in:
    val logic = LogicImpl(SQUARE_SHAPE, USER_COLOR)
    logic.state.status should be(InProgress)

  "Initial board configuration" should "be correct" in:
    val logic = LogicImpl(SQUARE_SHAPE, USER_COLOR)
    logic.state.board.disks should be(INITIAL_SQUARE_BOARD.state.disks)

  "Trying to place user disk when it is opponent's turn" should "not be allowed" in:
    val userColor = White
    val targetPosition = Position(0, 0)
    val logic = LogicImpl(SQUARE_SHAPE, userColor)
    an [IllegalStateException] shouldBe thrownBy { logic.placeUserDisk(targetPosition) }

  "Trying to place opponent disk when it is user's turn" should "not be allowed" in :
    val userColor = Black
    val logic = LogicImpl(SQUARE_SHAPE, userColor)
    an [IllegalStateException] shouldBe thrownBy { logic.placeOpponentDisk() }

  "User move that does not capture any opponent disk" should "not be allowed" in:
    val userColor = Black
    val targetPosition = Position(0, 0)
    val logic = LogicImpl(SQUARE_SHAPE, userColor).placeUserDisk(targetPosition)
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
    val logic = LogicImpl(userColor, initialBoard).placeUserDisk(targetPosition)
    logic.state.board.disks should be(expectedBoard.state.disks)

  "User move that captures more than one opponent disk horizontally" should "be allowed and capture target disks" in:
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
    val logic = LogicImpl(userColor, initialBoard).placeUserDisk(targetPosition)
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
    val logic = LogicImpl(userColor, initialBoard).placeUserDisk(targetPosition)
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
    val logic = LogicImpl(userColor, initialBoard).placeUserDisk(targetPosition)
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
    val logic = LogicImpl(userColor, initialBoard).placeUserDisk(targetPosition)
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
    val logic = LogicImpl(userColor, initialBoard).placeUserDisk(targetPosition)
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
    val logic = LogicImpl(userColor, initialBoard).placeUserDisk(targetPosition)
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
    val logic = LogicImpl(userColor, initialBoard).placeUserDisk(targetPosition)
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
    val logic = LogicImpl(userColor, initialBoard).placeUserDisk(targetPosition)
    logic.state.activePlayer shouldBe an [Opponent]

  "User" should "move again if opponent does not have available moves" in:
    val userColor = Black
    val initialBoard = """
      ....
      .WBW
      .BBW
      .BBB
     """.toBoard
    val targetPosition = Position(0, 3)
    val logic = LogicImpl(userColor, initialBoard).placeUserDisk(targetPosition)
    logic.state.activePlayer shouldBe an [User]

  "User" should "move after opponent when having available moves" in :
    val userColor = White
    val initialBoard = """
       ....
       .WB.
       .BW.
       ....
     """.toBoard
    val logic = LogicImpl(userColor, initialBoard).placeOpponentDisk()
    logic.state.activePlayer shouldBe an [User]

  "Opponent" should "move again if user does not have available moves" in:
    val userColor = White
    val initialBoard = """
       BBBB
       BBBB
       .BW.
       ....
     """.toBoard
    val logic = LogicImpl(userColor, initialBoard).placeOpponentDisk()
    logic.state.activePlayer shouldBe an [Opponent]

  "Match" should "end if board is full" in:
    val userColor = Black
    val initialBoard = """
       BBBB
       BBBB
       BBBB
       BBW.
     """.toBoard
    val targetPosition = Position(3, 3)
    val logic = LogicImpl(userColor, initialBoard).placeUserDisk(targetPosition)
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
    val logic = LogicImpl(userColor, initialBoard).placeUserDisk(targetPosition)
    logic.state.status should not be InProgress

  "User" should "win if the match is ended and he has more disks on the field than the opponent" in:
    val userColor = White
    val initialBoard = """
       WWWW
       WWWW
       WWWW
       WBW.
     """.toBoard
    val logic = LogicImpl(userColor, initialBoard).placeOpponentDisk()
    logic.state.status should be(UserWon)

  "Opponent" should "win if the match is ended and he has more disks on the field than the user" in :
    val userColor = Black
    val initialBoard = """
       WWWW
       WWWW
       WWWW
       WBW.
     """.toBoard
    val targetPosition = Position(3, 3)
    val logic = LogicImpl(userColor, initialBoard).placeUserDisk(targetPosition)
    logic.state.status should be(OpponentWon)

  "Match result" should "be a tie if the match is ended and players have the same number of disks on the field" in:
    val userColor = Black
    val initialBoard = """
       WWWW
       WWWW
       BBBB
       BBW.
     """.toBoard
    val targetPosition = Position(3, 3)
    val logic = LogicImpl(userColor, initialBoard).placeUserDisk(targetPosition)
    logic.state.status should be(Tie)
