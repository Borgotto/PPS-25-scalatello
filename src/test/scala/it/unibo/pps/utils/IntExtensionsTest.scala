package it.unibo.pps.utils

import it.unibo.pps.utils.IntExtensions.{inRange, inBetween, half}

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{be, should}

class IntExtensionsTest extends AnyFlatSpec:
  private val TEST_NUM = 4

  "Using the inRange function" should "give the same result with any order of params" in:
    TEST_NUM.inRange(0, TEST_NUM) should be(TEST_NUM.inRange(TEST_NUM, 0))

  "Using the inBetween function" should "give the same result with any order of params" in:
    TEST_NUM.inBetween(0, TEST_NUM + 1) should be(TEST_NUM.inBetween(TEST_NUM + 1, 0))
