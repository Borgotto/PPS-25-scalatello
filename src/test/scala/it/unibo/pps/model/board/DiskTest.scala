package it.unibo.pps.model.board

import it.unibo.pps.domain.Color
import it.unibo.pps.model.board.Disk
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{be, should}

class DiskTest extends AnyFlatSpec:
  "A Disk" should "have a color" in:
    val disk = Disk(Color.Black)
    disk.color.equals(Color.Black) should be(true)

  "A Disk" should "have the opposite color when flipped" in:
    val disk = Disk(Color.Black)
    val flippedDisk = disk.flip
    flippedDisk.color.equals(disk.color.opposite) should be(true)
