package it.unibo.pps.controller

import it.unibo.pps.state.*
import it.unibo.pps.utils.*
import it.unibo.pps.model.strategy.*
import it.unibo.pps.controller.save.SaveManager.*
import it.unibo.pps.utils.Serializer.{Serializer, Serializers}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{a, be, noException, shouldBe, shouldEqual, shouldNot}
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}
import os.{Path, pwd, read, temp, write}

import scala.util.{Failure, Success}

class SaveManagerTest extends AnyFlatSpec with TableDrivenPropertyChecks:

  private val tmpDir: Path = temp.dir()
  private val tmpFile: Path = tmpDir / "tmp_save_file.txt"
  private val nonExistentFile: Path = "/non_existent_file.txt"

  private val inputs: TableFor3[SaveManager[?], Serializer[?], ?] = Table(
    ("an instance of SaveManager", "its serializer", "some test data"),
    (SaveManagers.StringSaveManager(tmpDir), Serializers.StringSerializer, "Test string"),
    (
      SaveManagers.MatchStateSaveManager(tmpDir),
      Serializers.MatchSerializer,
      MatchState(
        MatchStatus.InProgress,
        PlayerState.User(Color.Black, UserPlacementStrategy()),
        BoardState(Shape.Square(4), Seq.empty, Set.empty)
      )
    ),
    (
      SaveManagers.MatchStateSaveManager(tmpDir),
      Serializers.MatchSerializer,
      MatchState(
        MatchStatus.InProgress,
        PlayerState.Opponent(Color.White, RandomPlacementStrategy(Color.White)),
        BoardState(
          Shape.Rectangle(4, 6),
          Seq(
            DiskState(Color.Black, Position(1, 2)),
            DiskState(Color.White, Position(2, 3))
          ),
          Set(Position(0, 1), Position(2, 2))
        )
      )
    ),
    (
      SaveManagers.MatchStateSaveManager(tmpDir),
      Serializers.MatchSerializer,
      MatchState(
        MatchStatus.UserWon,
        PlayerState.User(Color.White, UserPlacementStrategy()),
        BoardState(Shape.Square(8), Seq.empty, Set.empty)
      )
    )
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
          saveManager.save(data)(using tmpFile)
          read(tmpFile) shouldBe encoded(data)

        it should "load data correctly" in:
          write.over(tmpFile, encoded(data))
          saveManager.load(using tmpFile) shouldBe Success(data)

        it should "not throw an exception when saving data" in:
          noException should be thrownBy saveManager.save(data)(using tmpFile)

        it should "not throw an exception when loading data" in:
          noException should be thrownBy saveManager.load(using tmpFile)
