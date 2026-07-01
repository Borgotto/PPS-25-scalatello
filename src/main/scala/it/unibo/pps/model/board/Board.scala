package it.unibo.pps.model.board

import it.unibo.pps.state.BoardState
import it.unibo.pps.utils.{Color, Player, Position, Shape}

trait Board:
  val state: BoardState
  def isPlacementValid(color: Color, position: Position): Boolean
  def placeDisk(color: Color, position: Position): Board
  def captureDisks(newDiskPosition: Position): Board
  def getAvailablePlacements(color: Color): Seq[Position]

class BoardImpl(
  private val shape: Shape,
  private val disks: Seq[Disk]
) extends Board:
  
  def this(shape: Shape) = this(shape, Seq())

  override val state = BoardState(shape, disks.map(disk => disk.state))

  override def isPlacementValid(color: Color, position: Position): Boolean = true

  override def placeDisk(color: Color, position: Position): Board = this

  override def captureDisks(newDiskPosition: Position): Board = this

  override def getAvailablePlacements(color: Color): Seq[Position] = Seq()
  