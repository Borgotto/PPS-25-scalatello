package it.unibo.pps.model.board

import it.unibo.pps.utils.{Color, PlacementStrategy, Shape}

trait Board:
  val shape: Shape
  val disks: Seq[Disk]

  def placeDisk(color: Color, strategy: PlacementStrategy): Board

class BoardImpl(
  override val shape: Shape,
  override val disks: Seq[Disk]
) extends Board:
  
  def this(shape: Shape) = this(shape, Seq())

  def placeDisk(color: Color, strategy: PlacementStrategy): Board = this
