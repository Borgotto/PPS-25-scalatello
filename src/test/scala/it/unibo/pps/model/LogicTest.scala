package it.unibo.pps.model

import it.unibo.pps.domain.{Color, OpponentType, Position, Shape}
import it.unibo.pps.domain.ActivePlayer.{Opponent, User}
import it.unibo.pps.domain.Color.*
import it.unibo.pps.domain.MatchStatus.*
import it.unibo.pps.domain.OpponentType.Random
import it.unibo.pps.domain.Shape.*
import it.unibo.pps.model.board.Board

import it.unibo.pps.testutils.TestExtensions.*

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.{an, be, not, thrownBy}
import org.scalatest.matchers.should.Matchers.should

/** Test suite for [[Logic]]. */
class LogicTest extends AnyFlatSpec:

  private val userColor: Color = Black
  private val opponentType: OpponentType = Random

  private val squareSize = 4
  private val squareShape: Shape = Square(squareSize)
  private val initialSquareBoard: Board = """
    ....
    .WB.
    .BW.
    ....
  """.toBoard

  private val rectangleHeight = 4
  private val rectangleWidth = 6
  private val rectangularShape: Shape = Rectangle(rectangleHeight, rectangleWidth)
  private val initialRectangularBoard: Board = """
    ......
    ..WB..
    ..BW..
    ......
  """.toBoard

  "Square board" should "have correct shape and size" in:
    val logic = Logic(squareShape, userColor, opponentType)
    val boardShape = logic.state.board.shape
    boardShape match
      case Square(n) => n should be(squareSize)
      case shape => fail(s"Board shape is $shape")

  "Rectangular board" should "have correct shape and size" in:
    val logic = Logic(rectangularShape, userColor, opponentType)
    val boardShape = logic.state.board.shape
    boardShape match
      case Rectangle(h, w) =>
        h should be(rectangleHeight)
        w should be(rectangleWidth)
      case shape => fail(s"Board shape is $shape")

  "User" should "move first if assigned color black" in:
    val userColor = Black
    val logic = Logic(squareShape, userColor, opponentType)
    val activePlayer = logic.state.activePlayer
    activePlayer should be(User)

  "Opponent" should "move first if user is assigned color white" in:
    val userColor = White
    val logic = Logic(squareShape, userColor, opponentType)
    val activePlayer = logic.state.activePlayer
    activePlayer should be(Opponent)

  "Match" should "initially be in progress" in:
    val logic = Logic(squareShape, userColor, opponentType)
    logic.state.status should be(InProgress)

  "Initial board configuration" should "be correct" in:
    val logic = Logic(squareShape, userColor, opponentType)
    logic.state.board.disks should be(initialSquareBoard.state.disks)

  "Trying to place user disk when it is opponent's turn" should "not be allowed" in:
    val userColor = White
    val targetPosition = Position(0, 0)
    val logic = Logic(squareShape, userColor, opponentType)
    an [IllegalStateException] shouldBe thrownBy { logic.placeUserDisk(targetPosition) }

  "Trying to place opponent disk when it is user's turn" should "not be allowed" in :
    val userColor = Black
    val logic = Logic(squareShape, userColor, opponentType)
    an [IllegalStateException] shouldBe thrownBy { logic.placeOpponentDisk() }

  "User move that does not capture any opponent disk" should "not be allowed" in:
    val userColor = Black
    val targetPosition = Position(0, 0)
    val logic = Logic(squareShape, userColor, opponentType).placeUserDisk(targetPosition)
    logic.state.board.disks should be(initialSquareBoard.state.disks)

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
    val logic = Logic(userColor, opponentType, initialBoard).placeUserDisk(targetPosition)
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
    val logic = Logic(userColor, opponentType, initialBoard).placeUserDisk(targetPosition)
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
    val logic = Logic(userColor, opponentType, initialBoard).placeUserDisk(targetPosition)
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
    val logic = Logic(userColor, opponentType, initialBoard).placeUserDisk(targetPosition)
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
    val logic = Logic(userColor, opponentType, initialBoard).placeUserDisk(targetPosition)
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
    val logic = Logic(userColor, opponentType, initialBoard).placeUserDisk(targetPosition)
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
    val logic = Logic(userColor, opponentType, initialBoard).placeUserDisk(targetPosition)
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
    val logic = Logic(userColor, opponentType, initialBoard).placeUserDisk(targetPosition)
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
    val logic = Logic(userColor, opponentType, initialBoard).placeUserDisk(targetPosition)
    logic.state.activePlayer should be(Opponent)

  "User" should "move again if opponent does not have available moves" in:
    val userColor = Black
    val initialBoard = """
      ....
      .WBW
      .BBW
      .BBB
     """.toBoard
    val targetPosition = Position(0, 3)
    val logic = Logic(userColor, opponentType, initialBoard).placeUserDisk(targetPosition)
    logic.state.activePlayer should be(User)

  "User" should "move after opponent when having available moves" in :
    val userColor = White
    val initialBoard = """
       ....
       .WB.
       .BW.
       ....
     """.toBoard
    val logic = Logic(userColor, opponentType, initialBoard).placeOpponentDisk()
    logic.state.activePlayer should be(User)

  "Opponent" should "move again if user does not have available moves" in:
    val userColor = White
    val initialBoard = """
       BBBB
       BBBB
       .BW.
       ....
     """.toBoard
    val logic = Logic(userColor, opponentType, initialBoard).placeOpponentDisk()
    logic.state.activePlayer should be(Opponent)

  "Match" should "end if board is full" in:
    val userColor = Black
    val initialBoard = """
       BBBB
       BBBB
       BBBB
       BBW.
     """.toBoard
    val targetPosition = Position(3, 3)
    val logic = Logic(userColor, opponentType, initialBoard).placeUserDisk(targetPosition)
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
    val logic = Logic(userColor, opponentType, initialBoard).placeUserDisk(targetPosition)
    logic.state.status should not be InProgress

  "User" should "win if the match is ended and he has more disks on the field than the opponent" in:
    val userColor = White
    val initialBoard = """
       WWWW
       WWWW
       WWWW
       WBW.
     """.toBoard
    val logic = Logic(userColor, opponentType, initialBoard).placeOpponentDisk()
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
    val logic = Logic(userColor, opponentType, initialBoard).placeUserDisk(targetPosition)
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
    val logic = Logic(userColor, opponentType, initialBoard).placeUserDisk(targetPosition)
    logic.state.status should be(Tie)
