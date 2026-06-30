package it.unibo.pps.model.board

import it.unibo.pps.utils.{Color, Player, Position, Shape}

trait Board:
  val shape: Shape
  val disks: Seq[Disk]

  def isPlacementValid(color: Color, position: Position): Boolean
  def placeDisk(color: Color, position: Position): Board
  def captureDisks(newDiskPosition: Position): Board
  def getAvailablePlacements(color: Color): Seq[Position]

class BoardImpl(
  override val shape: Shape,
  override val disks: Seq[Disk]
) extends Board:
  
  def this(shape: Shape) = this(shape, Seq())

  override def isPlacementValid(color: Color, position: Position): Boolean = true

  override def placeDisk(color: Color, position: Position): Board = this

  override def captureDisks(newDiskPosition: Position): Board = this

  override def getAvailablePlacements(color: Color): Seq[Position] = Seq()
  