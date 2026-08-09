package it.unibo.pps.model.board

import it.unibo.pps.domain.{Color, Position, Shape}
import it.unibo.pps.model.board.Board
import it.unibo.pps.state.BoardState
import it.unibo.pps.model.board.BoardCreationExtensions.toPosDiskMap

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{an, be, should}
import org.scalatest.prop.TableDrivenPropertyChecks

/** Test suite for [[Board]] */
class BoardTest extends AnyFlatSpec with TableDrivenPropertyChecks:
  private val BOARD_SIZE: Int = 4
  private val BOARD_HEIGHT: Int = 4
  private val BOARD_WIDTH: Int = 6

  private val squareParams: BoardTestParams = BoardTestParams(Shape.Square(BOARD_SIZE))
  private val rectangleParams: BoardTestParams = BoardTestParams(Shape.Rectangle(BOARD_HEIGHT, BOARD_WIDTH))

  private val disksMapCreationTestTable = Table(
    ("stringToTest", "expectedMap"),
    ("(0,0) -> W", Map[Position, Disk](Position(0,0) -> Disk(Color.White))),
    (
      """
         (0,0) -> b
         (1,1) -> B
      """,
      Map[Position, Disk](
        Position(0, 0) -> Disk(Color.Black),
        Position(1, 1) -> Disk(Color.Black)
      )
    )
  )
  "Using the conversion from String to Map" should "create the correct Map of disks" in:
    forEvery(disksMapCreationTestTable):
      (stringToTest, expectedMap) =>
        stringToTest.toPosDiskMap should be(expectedMap)

  private val initialBoardsTestTable = Table(
    ("initialBoard", "initialDisksOnBoard"),
    (squareParams.initialBoard, squareParams.initialDisksOnBoard),
    (rectangleParams.initialBoard, rectangleParams.initialDisksOnBoard)
  )
  "A Board without disks" should "initialize itself with the disks in the correct positions" in:
    forEvery(initialBoardsTestTable):
      (initialBoard, initialDisksOnBoard) =>
        initialBoard.disks should be(initialDisksOnBoard)

  private val creationBoardsTestTable = Table(
    ("initialBoard", "boardShape", "initialDisksOnBoard"),
    (squareParams.initialBoard, squareParams.shape, squareParams.initialDisksOnBoard),
    (rectangleParams.initialBoard, rectangleParams.shape, rectangleParams.initialDisksOnBoard)
  )
  "A Board, given some disks" should "be created with those disks" in:
    forEvery(creationBoardsTestTable):
      (initialBoard, boardShape, initialDisksOnBoard) =>
        initialBoard.disks should be(Board(boardShape, initialDisksOnBoard).disks)

  private val availableMovesTestTable = Table(
    ("initialBoard", "expectedMoves"),
    (squareParams.initialBoard, squareParams.expectedAvailableMoves),
    (rectangleParams.initialBoard, rectangleParams.expectedAvailableMoves)
  )
  "A Board" should "know which moves are available for a given player" in:
    forEvery(availableMovesTestTable):
      (initialBoard, expectedMoves) =>
        initialBoard.getAvailablePlacements(Color.Black).equals(expectedMoves) should be(true)

  private val outOfBoundsTestTable = Table(
    ("board", "expectedMoves"),
    (squareParams.outOfBoundsTestBoard, squareParams.expectedNotOutOfBoundsMoves),
    (rectangleParams.outOfBoundsTestBoard, rectangleParams.expectedNotOutOfBoundsMoves)
  )
  "A Board" should "not say that moves out of bounds are available" in:
    forEvery(outOfBoundsTestTable):
      (board, expectedMoves) =>
        board.getAvailablePlacements(Color.Black).equals(expectedMoves) should be(true)

  private val validMovesTestTable = Table(
    ("board", "validMovePos"),
    (squareParams.boardDuringMatch, squareParams.validMovePos),
    (rectangleParams.boardDuringMatch, rectangleParams.validMovePos)
  )
  "A Board" should "know if a move is valid" in:
    forEvery(validMovesTestTable):
      (board, validMovePos) =>
      board.isPlacementValid(Color.Black, validMovePos) should be(true)

  private val notValidMovesTestTable = Table(
    ("board", "notValidMovePos"),
    (squareParams.boardDuringMatch, squareParams.notValidMovePos),
    (rectangleParams.boardDuringMatch, rectangleParams.notValidMovePos)
  )
  "A Board" should "know if a move is not valid" in:
    forEvery(notValidMovesTestTable):
      (board, notValidMovePos) =>
        board.isPlacementValid(Color.Black, notValidMovePos) should be(false)

  private val equalsTestTable = Table(
    "board",
    squareParams.initialBoard,
    rectangleParams.initialBoard
  )
  "A Board" should "know if is equal to another board" in:
    forEvery(equalsTestTable):
      board => board.equals(board) should be(true)

  private val notEqualsTestTable = Table(
    ("board", "notEqualBoard"),
    (squareParams.initialBoard, squareParams.boardDuringMatch),
    (rectangleParams.initialBoard, rectangleParams.boardDuringMatch)
  )
  "A Board" should "know if is not equal to another board" in:
    forEvery(notEqualsTestTable):
      (board, notEqualBoard) =>
        board.equals(notEqualBoard) should be(false)

  private val notPlaceDiskTestTable = Table(
    ("boardDuringMatch", "notValidMovePos"),
    (squareParams.boardDuringMatch, squareParams.notValidMovePos),
    (rectangleParams.boardDuringMatch, rectangleParams.notValidMovePos)
  )
  "A Board, if the move is not valid" should "not place the new disk" in:
    forEvery(notPlaceDiskTestTable):
      (boardDuringMatch, notValidMovePos) =>
        an [IllegalArgumentException] should be thrownBy boardDuringMatch.placeDisk(Color.Black, notValidMovePos)

  private val placeDiskTestTable = Table(
    ("boardDuringMatch", "validMovePos", "expectedBoardAfterNewDisk"),
    (squareParams.boardDuringMatch, squareParams.validMovePos, squareParams.expectedBoardAfterNewDisk),
    (rectangleParams.boardDuringMatch, rectangleParams.validMovePos, rectangleParams.expectedBoardAfterNewDisk)
  )
  "A Board" should "place a disk and then capture the correct disks" in:
    forEvery(placeDiskTestTable):
      (boardDuringMatch, validMovePos, expectedBoardAfterNewDisk) =>
        val board: Board = boardDuringMatch.placeDisk(Color.Black, validMovePos)
        board.equals(expectedBoardAfterNewDisk) should be(true)

  private val boardStateTestTable = Table(
    ("initialBoard", "boardShape", "expectedDiskStates"),
    (squareParams.initialBoard, squareParams.shape, squareParams.expectedDiskStates),
    (rectangleParams.initialBoard, rectangleParams.shape, rectangleParams.expectedDiskStates)
  )
  "A Board" should "return its correct state" in:
    forEvery(boardStateTestTable):
      (initialBoard, boardShape, expectedDiskStates) =>
        val boardState: BoardState = initialBoard.state
        val expectedState: BoardState = BoardState(boardShape, expectedDiskStates, Set[Position]())
        boardState.equals(expectedState) should be(true)

  private val creationBoardFromStateTestTable = Table(
    ("state", "expectedBoard"),
    (squareParams.initialBoardState, squareParams.initialBoard),
    (rectangleParams.initialBoardState, rectangleParams.initialBoard)
  )
  "A Board, given a BoardState" should "be created with those information" in:
    forEvery(creationBoardFromStateTestTable):
      (state, expectedBoard) =>
        Board(state) should be(expectedBoard)

  private val movesNotAfterSameColor = Table(
    ("board", "expectedAvailableMoves"),
    (squareParams.notAfterSameColorTestBoard, squareParams.expectedNotAfterSameColorMoves),
    (rectangleParams.notAfterSameColorTestBoard, rectangleParams.expectedNotAfterSameColorMoves)
  )
  "A Board" should "not say that a placement is available if it is after a disk of the same color" in:
    forEvery(movesNotAfterSameColor):
      (board, expectedAvailableMoves) =>
        board.getAvailablePlacements(Color.Black).equals(expectedAvailableMoves) should be(true)
