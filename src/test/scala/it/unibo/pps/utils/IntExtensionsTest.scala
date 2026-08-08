package it.unibo.pps.utils

import it.unibo.pps.utils.IntExtensions.{inBetween, inRange}

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{be, should}

/** Test suite for [[IntExtensions]] */
class IntExtensionsTest extends AnyFlatSpec:
  private val TEST_NUM = 4

  "Using the inRange method" should "give the same result with any order of params" in:
    TEST_NUM.inRange(0, TEST_NUM) should be(TEST_NUM.inRange(TEST_NUM, 0))

  "Using the inBetween method" should "give the same result with any order of params" in:
    TEST_NUM.inBetween(0, TEST_NUM + 1) should be(TEST_NUM.inBetween(TEST_NUM + 1, 0))

  "A number at the limit of the range" should "be in range but not in between" in:
    TEST_NUM.inRange(0, TEST_NUM) should be(!TEST_NUM.inBetween(0, TEST_NUM))
