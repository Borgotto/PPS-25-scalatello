package it.unibo.pps.controller.save

import it.unibo.pps.utils.Serializer.*
import it.unibo.pps.controller.save.SaveError.*

import scala.util.{Try, Success, Failure}
import os.{Path, read, write}

object SaveManager:
  /**
   * Base trait for a persistence manager for values of type `C`.
   *
   * @param savePath directory where save files are stored
   * @param serializer type-class instance used to encode/decode values
   * @tparam C the type of data handled by the manager
   */
  trait SaveManager[C](val savePath: Path)(using serializer: Serializer[C]):
    /**
     * Saves data to the given file path.
     *
     * The save directory is created if necessary, the value is serialized, and
     * then written to disk.
     *
     * @param data the value to persist
     * @param filePath destination file path (provided as a contextual parameter)
     * @return a `Try` containing success or the translated `SaveError`
     */
    def save(data: C)(using filePath: Path): Try[_] =
      try
        os.makeDir.all(savePath)
        val serializedData = serializer.encode(data)
        write.over(filePath, serializedData)
        Success(())
      catch
        case e => Failure(handleSaveErrors(e))

    /**
     * Loads data from the given file path.
     *
     * The file contents are read and then decoded using the configured serializer.
     *
     * @param filePath source file path (provided as a contextual parameter)
     * @return a `Try` containing the decoded value or a translated `SaveError`
     */
    def load(using filePath: Path): Try[C] =
      try
        val serializedData = read(filePath)
        Success(serializer.decode(serializedData))
      catch
        case e => Failure(handleLoadErrors(e))

    /**
     * Deletes the passed save file.
     *
     * @param filePath file to delete (provided as a contextual parameter)
     * @return a `Try[Unit]` representing success or failure
     * @note If the file cannot be loaded, deletion is refused and `DeleteError` is returned.
     */
    def deleteSaveFile(using filePath: Path): Try[Unit] =
      load match
        case Failure(e) => Failure(handleDeleteErrors(e))
        case Success(_) => Try(os.remove(filePath))

    /**
     * Lists the names of all (valid) files saved in `savePath`.
     *
     * @return a sequence of savefile names, empty if no valid files are found
     * @note Files that fail deserialization are filtered out.
     */
    def saveFileNames: Seq[String] =
      try os.list(savePath).filter(load(using _).isSuccess).map(_.last)
      catch case _ => Seq.empty

  class MatchStateSaveManager(override val savePath: Path) extends SaveManager(savePath)(using Serializers.MatchSerializer)
  /**
   * Concrete save managers for the supported serializable types.
   */
  enum SaveManagers:
    case StringSaveManager(override val savePath: Path)
      extends SaveManagers, SaveManager(savePath)
      (using Serializers.StringSerializer)

    case MatchStateSaveManager(override val savePath: Path)
      extends SaveManagers, SaveManager(savePath)
      (using Serializers.MatchSerializer)
