package it.unibo.pps.utils

import it.unibo.pps.model.board.Board
import it.unibo.pps.utils.Position

type PlacementStrategy = (board: Board) => Position
