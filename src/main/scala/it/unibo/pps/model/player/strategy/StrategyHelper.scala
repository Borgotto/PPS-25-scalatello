package it.unibo.pps.model.player.strategy

import it.unibo.pps.domain.{Color, Position, Shape}
import it.unibo.pps.model.board.Board

private object StrategyHelper:
  private enum PositionWeight(val weight: Int):
    private case Corner extends PositionWeight(10)
    private case InnerCorner extends PositionWeight(-4)
    private case Edge extends PositionWeight(2)
    private case Inner extends PositionWeight(1)

  private object PositionWeight:
    def apply(pos: Position)(using shape: Shape): PositionWeight =
      def isEdge(i: Int, max: Int) = i == 0 || i == max

      def isInner(i: Int, max: Int) = i == 1 || i == max - 1

      val rEdge = isEdge(pos.row, shape.maxRowIndex)
      val cEdge = isEdge(pos.column, shape.maxColumnIndex)

      if rEdge && cEdge then Corner
      else if isInner(pos.row, shape.maxRowIndex) && isInner(pos.column, shape.maxColumnIndex) then InnerCorner
      else if rEdge || cEdge then Edge
      else Inner

  extension (pos: Position)(using shape: Shape)
    private def weight: Int = PositionWeight(pos).weight

  extension (board: Board)(using shape: Shape = board.state.shape)
    private[strategy] def score(color: Color): Int =
      board.state.disks.iterator.map(disk =>
        val diskScore = 1
        val positionScore: Int = disk.position.weight
        val totalScore = diskScore + positionScore
        if disk.color == color then totalScore else -totalScore
      ).sum