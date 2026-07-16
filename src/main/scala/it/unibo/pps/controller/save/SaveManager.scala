package it.unibo.pps.controller.save

import it.unibo.pps.utils.Serializer.*

import scala.util.{Try, Success, Failure}
import os.{Path, read, write}

object SaveManager:
  trait SaveManager[C](val savePath: Path)(using serializer: Serializer[C]):
    def save(data: C)(using filePath: Path): Try[_] =
      try
        os.makeDir.all(savePath)
        val serializedData = serializer.encode(data)
        write.over(filePath, serializedData)
        Success(())
      catch
        case e => Failure(SaveErrorHandler.handleSaveErrors(e))

    def load(using filePath: Path): Try[C] =
      try
        val serializedData = read(filePath)
        Success(serializer.decode(serializedData))
      catch
        case e => Failure(SaveErrorHandler.handleLoadErrors(e))

    def savefiles: Seq[Path] =
      try
        os.list(savePath).filter(load(using _).isSuccess)
      catch
        case _ => Seq.empty

    def deleteSaveFile(using filePath: Path): Try[Unit] =
      load match
        case Failure(_) => Failure(SaveError.DeleteError)
        case Success(_) => Try(os.remove(filePath))

  enum SaveManagers:
    case StringSaveManager(override val savePath: Path)
      extends SaveManagers, SaveManager(savePath)
      (using Serializers.StringSerializer)
    case MatchStateSaveManager(override val savePath: Path)
      extends SaveManagers, SaveManager(savePath)
      (using Serializers.MatchSerializer)
