package it.unibo.pps.controller

import os.{Path, read, temp, write}
import it.unibo.pps.state.{BoardState, MatchState, PlayerState}
import it.unibo.pps.utils.{Color, MatchStatus, Shape}
import it.unibo.pps.model.strategy.UserPlacementStrategy

import it.unibo.pps.controller.saveManager.*
import it.unibo.pps.utils.Serializer.*

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{be, noException, shouldBe}
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}

class SaveManagerTest extends AnyFlatSpec with TableDrivenPropertyChecks:

  private val tmpFile: Path = temp()
  private val inputs: TableFor3[SaveManager[?], Serializer[?], ?] = Table(
    ("an instance of SaveManager", "its serializer", "some test data"),
    (StringSaveManager(tmpFile), StringSerializer, "Test string"),
    (MatchStateSaveManager(tmpFile), MatchSerializer, MatchState(MatchStatus.InProgress,PlayerState.User(Color.Black, UserPlacementStrategy()), BoardState(Shape.Square(5), Seq.empty, Set.empty)))
  )

  behave like saveManager (using inputs)

  // Shared tests for SaveManager instances
  def saveManager[C](using inputs: TableFor3[SaveManager[?], Serializer[?], C]): Unit =
    forEvery(inputs): (sm, se, data) =>
        val saveManager = sm.asInstanceOf[SaveManager[C]]
        val serializer = se.asInstanceOf[Serializer[C]]
        val encoded = serializer.encode
        val testSubject = saveManager.getClass.getSimpleName + " (input #" + inputs.indexOf((sm, se, data)) + ")"

        testSubject should "save data correctly" in:
          saveManager.save(data)
          read(saveManager.filePath) shouldBe encoded(data)

        it should "load data correctly" in:
          write.over(saveManager.filePath, encoded(data))
          saveManager.load shouldBe data

        it should "not throw an exception when saving data" in:
          noException should be thrownBy saveManager.save

        it should "not throw an exception when loading data" in:
          noException should be thrownBy saveManager.load
