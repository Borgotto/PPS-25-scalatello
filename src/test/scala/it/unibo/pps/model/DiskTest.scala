package it.unibo.pps.model

import org.scalatest.flatspec.AnyFlatSpec
import it.unibo.pps.utils.Color
import it.unibo.pps.model.board.Disk

class DiskTest extends AnyFlatSpec:
  "A Disk" should "have a color" in:
    val disk = Disk(Color.Black)
    assert(disk.color == Color.Black)
