package it.unibo.pps.controller.save

import it.unibo.pps.controller.save.SaveError.*
import it.unibo.pps.controller.save.SaveManager.SaveManagers.*
import it.unibo.pps.domain.{ActivePlayer, Color, MatchStatus, Shape}
import it.unibo.pps.model.player.Opponent.ErraticOpponent
import it.unibo.pps.model.player.User
import it.unibo.pps.state.*
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{a, shouldBe}
import os.{Path, temp, write}

import java.io.RandomAccessFile
import scala.util.Failure

/** Test suite for save errors. */
class SaveErrorTest extends AnyFlatSpec with BeforeAndAfterAll:

  private val tmpDir: Path = temp.dir()
  private val tmpFile: Path = tmpDir / "tmp_save_file.txt"

  // Create a subdirectory and make it non-writable by locking it
  private val inaccessibleDir: Path = tmpDir / "non_writable_dir"
  private val inaccessibleFile: Path = inaccessibleDir / "non_writable_file.txt"
  RandomAccessFile(inaccessibleDir.toIO, "rw").getChannel.lock()

  private val saveManager = MatchStateSaveManager(tmpDir)
  private val exampleMatchState = MatchState(
    MatchStatus.InProgress,
    User(Color.Black),
    ErraticOpponent(Color.White),
    ActivePlayer.User,
    BoardState(Shape.Square(4), Set.empty, Set.empty)
  )

  "Saving" should "fail with a WriteError when trying to save to a non-writable path" in:
    saveManager.save(exampleMatchState)(using inaccessibleFile) shouldBe a [Failure[WriteError]]

  it should "leave no trace after failing" in:
    try
      saveManager.save(exampleMatchState)(using inaccessibleFile)
    catch
      case _ => // Ignore exception
    finally
      assert(!os.exists(inaccessibleFile), "The inaccessible file should not be created after a failed save.")

  "Loading" should "fail with a ReadError when trying to load from a non-existent file" in:
    saveManager.load(using inaccessibleFile) shouldBe a [Failure[ReadError]]

  it should "fail with a DecodeError when trying to load from a file with invalid content" in:
    saveManager.load(using tmpFile) shouldBe a [Failure[DecodeError]]
    write.over(tmpFile, "clearly not a valid MatchState because I'm a string")
    saveManager.load(using tmpFile) shouldBe a [Failure[DecodeError]]

  it should "fail with a DecodeError when trying to load from a file with invalid JSON" in:
    saveManager.load(using tmpFile) shouldBe a [Failure[DecodeError]]
    write.over(tmpFile, "{invalidJson: true, missingQuotes: yes}")
    saveManager.load(using tmpFile) shouldBe a [Failure[DecodeError]]

  it should "fail with a DecodeError when trying to load from an empty file" in:
    saveManager.load(using tmpFile) shouldBe a [Failure[DecodeError]]
    write.over(tmpFile, "")
    saveManager.load(using tmpFile) shouldBe a [Failure[DecodeError]]

  it should "fail with a ReadError when trying to load from a non-readable file" in:
    saveManager.load(using inaccessibleFile) shouldBe a [Failure[ReadError]]

