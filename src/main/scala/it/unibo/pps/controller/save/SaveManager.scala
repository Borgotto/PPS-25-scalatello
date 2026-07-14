package it.unibo.pps.controller.save

import it.unibo.pps.utils.Serializer.*
import os.{Path, read, write}

object SaveManager:
  trait SaveManager[C](val filePath: Path)(using serializer: Serializer[C]):
    def save(data: C): Unit =
      val serializedData = serializer.encode(data)
      write.over(filePath, serializedData)
    def load(): C =
      val serializedData = read(filePath)
      serializer.decode(serializedData)
  enum SaveManagers:
    case StringSaveManager(override val filePath: Path)
      extends SaveManagers, SaveManager(filePath)
      (using Serializers.StringSerializer)
    case MatchStateSaveManager(override val filePath: Path)
      extends SaveManagers, SaveManager(filePath)
      (using Serializers.MatchSerializer)
