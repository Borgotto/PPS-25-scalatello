package it.unibo.pps.controller

import it.unibo.pps.controller.saveManager.*

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{shouldBe, be, noException}

class SaveManagerTest extends AnyFlatSpec:

  trait SaveManagerFactory:
    val tmpFile: os.Path = os.temp()
    val saveManager: SaveManager = SaveManagerImpl(tmpFile)
    val testData: String = "test input"
    given Saveable[String] with
        def encode(data: String): String = data
        def decode(data: String): String = data

  "SaveManager" should "save data correctly" in new SaveManagerFactory:
    saveManager.save(testData)
    os.read(tmpFile) shouldBe testData

  it should "load data correctly" in new SaveManagerFactory:
    os.write.over(tmpFile, testData)
    saveManager.load() shouldBe testData

  it should "not throw an exception when saving data" in new SaveManagerFactory:
    noException should be thrownBy saveManager.save(testData)

  it should "not throw an exception when loading data" in new SaveManagerFactory:
    noException should be thrownBy saveManager.load()
