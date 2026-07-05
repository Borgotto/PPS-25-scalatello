package it.unibo.pps.model

import it.unibo.pps.model.board.{Board, Disk}
import it.unibo.pps.state.BoardState
import it.unibo.pps.utils.{Color, Shape}

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{be, should}
import org.scalatest.prop.TableDrivenPropertyChecks

class BoardTest extends AnyFlatSpec with TableDrivenPropertyChecks:
  private val BOARD_SIZE: Int = 4
  private val BOARD_HEIGHT: Int = 4
  private val BOARD_WIDTH: Int = 6

  private val squareParams: BoardTestParams = BoardTestParams(Shape.Square(BOARD_SIZE))
  private val rectangleParams: BoardTestParams = BoardTestParams(Shape.Rectangle(BOARD_HEIGHT, BOARD_WIDTH))

  private val initialBoardsTestTable = Table(
    ("initialBoard", "initialDisksOnBoard"),
    (squareParams.initialBoard, squareParams.initialDisksOnBoard),
    (rectangleParams.initialBoard, rectangleParams.initialDisksOnBoard)
  )
  "A Board without disks" should "initialize itself with the disks in the correct positions" in:
    forEvery(initialBoardsTestTable) { (initialBoard, initialDisksOnBoard) =>
      initialBoard.disks should be(initialDisksOnBoard)
    }

  private val creationBoardsTestTable = Table(
    ("initialBoard", "boardShape", "initialDisksOnBoard"),
    (squareParams.initialBoard, squareParams.shape, squareParams.initialDisksOnBoard),
    (rectangleParams.initialBoard, rectangleParams.shape, rectangleParams.initialDisksOnBoard)
  )
  "A Board given some disks" should "be created with those disks" in:
    forEvery(creationBoardsTestTable) { (initialBoard, boardShape, initialDisksOnBoard) =>
      initialBoard.disks should be(Board(boardShape, initialDisksOnBoard).disks)
    }

  private val availableMovesTestTable = Table(
    ("initialBoard", "expectedMoves"),
    (squareParams.initialBoard, squareParams.expectedAvailableMoves),
    (rectangleParams.initialBoard, rectangleParams.expectedAvailableMoves)
  )
  "A Board" should "know which moves are available for a given player" in:
    forEvery(availableMovesTestTable) { (initialBoard, expectedMoves) =>
      initialBoard.getAvailableMoves(Color.Black).equals(expectedMoves) should be(true)
    }

  private val validMovesTestTable = Table(
    ("board", "validMovePos"),
    (squareParams.boardDuringMatch, squareParams.validMovePos),
    (rectangleParams.boardDuringMatch, rectangleParams.validMovePos)
  )
  "A Board" should "know if a move is valid" in:
    forEvery(validMovesTestTable) { (board, validMovePos) =>
      board.isMoveValid(validMovePos, Color.Black) should be(true)
    }

  private val notValidMovesTestTable = Table(
    ("board", "notValidMovePos"),
    (squareParams.boardDuringMatch, squareParams.notValidMovePos),
    (rectangleParams.boardDuringMatch, rectangleParams.notValidMovePos)
  )
  "A Board" should "know if a move is not valid" in:
    forEvery(notValidMovesTestTable) { (board, notValidMovePos) =>
      board.isMoveValid(notValidMovePos, Color.Black) should be(false)
    }

  private val equalsTestTable = Table(
    "board",
    squareParams.initialBoard,
    rectangleParams.initialBoard
  )
  "A Board" should "know if is equal to another board" in:
    forEvery(equalsTestTable) { board =>
      board.equals(board) should be(true)
    }

  private val notEqualsTestTable = Table(
    ("board", "notEqualBoard"),
    (squareParams.initialBoard, squareParams.boardDuringMatch),
    (rectangleParams.initialBoard, rectangleParams.boardDuringMatch)
  )
  "A Board" should "know if is not equal to another board" in:
    forEvery(notEqualsTestTable) { (board, notEqualBoard) =>
      board.equals(notEqualBoard) should be(false)
    }

  private val placeDiskTestTable = Table(
    ("boardDuringMatch", "boardShape", "validMovePos"),
    (squareParams.boardDuringMatch, squareParams.shape, squareParams.validMovePos),
    (rectangleParams.boardDuringMatch, rectangleParams.shape, rectangleParams.validMovePos)
  )
  "A Board" should "be able to place a new disk in a valid position" in:
    forEvery(placeDiskTestTable) { (boardDuringMatch, boardShape, validMovePos) =>
      val board: Board = boardDuringMatch.placeDisk(validMovePos, Color.Black)
      val expectedBoard: Board = Board(boardShape, boardDuringMatch.disks + (validMovePos -> Disk(Color.Black)))
      board.equals(expectedBoard) should be(true)
    }

  private val notPlaceDiskTestTable = Table(
    ("boardDuringMatch", "notValidMovePos"),
    (squareParams.boardDuringMatch, squareParams.notValidMovePos),
    (rectangleParams.boardDuringMatch, rectangleParams.notValidMovePos)
  )
  "A Board, if the move is not valid" should "not place the new disk" in:
    forEvery(notPlaceDiskTestTable) { (boardDuringMatch, notValidMovePos) =>
      val board: Board = boardDuringMatch.placeDisk(notValidMovePos, Color.Black)
      board.equals(boardDuringMatch) should be(true)
    }

  private val captureDisksTestTable = Table(
    ("boardDuringMatch", "validMovePos", "expectedBoardAfterCapture"),
    (squareParams.boardDuringMatch, squareParams.validMovePos, squareParams.expectedBoardAfterCapture),
    (rectangleParams.boardDuringMatch, rectangleParams.validMovePos, rectangleParams.expectedBoardAfterCapture)
  )
  "A Board, after placing a disk" should "capture the correct disks" in:
    forEvery(captureDisksTestTable) { (boardDuringMatch, validMovePos, expectedBoardAfterCapture) =>
      val board: Board = boardDuringMatch.placeDisk(validMovePos, Color.Black).captureDisks(validMovePos)
      board.equals(expectedBoardAfterCapture) should be(true)
    }

  private val boardStateTestTable = Table(
    ("initialBoard", "boardShape", "expectedDiskStates"),
    (squareParams.initialBoard, squareParams.shape, squareParams.expectedDiskStates),
    (rectangleParams.initialBoard, rectangleParams.shape, rectangleParams.expectedDiskStates)
  )
  "A Board" should "return its correct state" in:
    forEvery(boardStateTestTable) { (initialBoard, boardShape, expectedDiskStates) =>
      val boardState: BoardState = initialBoard.state
      val expectedState: BoardState = BoardState(boardShape, expectedDiskStates)
      boardState.shape.equals(expectedState.shape) should be(true)
      boardState.disks.toSet.equals(expectedState.disks.toSet) should be(true)
    }
