package it.unibo.pps.model.board

import scala.collection.immutable.HashMap
import it.unibo.pps.utils.{Position, Color, Shape}

trait Board:
  def shape: Shape
  def size: Int
  def disks: HashMap[Position, Disk]
  
object Board:
  def apply(shape: Shape, size: Int, disks: HashMap[Position, Disk]): Board = {
    disks match
      case d if d.isEmpty =>
        val leftCenter = size / 2
        val rightCenter = leftCenter + 1
        val initialDisks = HashMap(
          Position(leftCenter, leftCenter) -> Disk(Color.White),
          Position(leftCenter, rightCenter) -> Disk(Color.Black),
          Position(rightCenter, leftCenter) -> Disk(Color.Black),
          Position(rightCenter, rightCenter) -> Disk(Color.White)
        )
        new StandardBoard(shape, size, initialDisks)
      case _ => new StandardBoard(shape, size, disks)
  }

  private class StandardBoard(override val shape: Shape, 
                              override val size: Int, 
                              override val disks: HashMap[Position, Disk]) extends Board