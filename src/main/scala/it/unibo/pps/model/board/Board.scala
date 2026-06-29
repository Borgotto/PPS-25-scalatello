package it.unibo.pps.model.board

import it.unibo.pps.state.BoardState
import it.unibo.pps.utils.Shape

trait Board:
  val state: BoardState

class BoardImpl(private val shape: Shape) extends Board:
  override val state: BoardState = BoardState(shape, Seq())
