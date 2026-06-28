package it.unibo.pps.model.board

import it.unibo.pps.state.BoardState
import it.unibo.pps.utils.Shape

trait Board:
  def state: BoardState

class BoardImpl(private val shape: Shape) extends Board:
  override def state: BoardState = BoardState(shape, Seq())
