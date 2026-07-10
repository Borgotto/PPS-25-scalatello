package it.unibo.pps.controller.saveManager

import os.{Path, read, write}
import it.unibo.pps.utils.Serializer.{Serializer, StringSerializer, MatchSerializer}

trait SaveManager[C](val filePath: Path)(using serializer: Serializer[C]):
  def save(data: C): Unit =
    val serializedData = serializer.encode(data)
    write.over(filePath, serializedData)
  def load: C =
    val serializedData = read(filePath)
    serializer.decode(serializedData)

case class StringSaveManager(override val filePath: Path)
  extends SaveManager(filePath)
  (using StringSerializer)

case class MatchStateSaveManager(override val filePath: Path)
  extends SaveManager(filePath)
  (using MatchSerializer)
