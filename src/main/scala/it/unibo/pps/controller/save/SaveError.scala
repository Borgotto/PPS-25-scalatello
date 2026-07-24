package it.unibo.pps.controller.save

// Exceptions that are handled
import java.io.{FileNotFoundException, IOException}
import java.nio.file.{AccessDeniedException, NoSuchFileException}
import upickle.core.AbortException
import upickle.core.TraceVisitor.TraceException
import ujson.ParsingFailedException

/**
 * Domain-specific errors used by the [[SaveManager]] system.
 *
 * These errors wrap lower-level I/O and parsing exceptions so that the rest of the
 * application can work with a smaller, predictable set of failure cases.
 */
enum SaveError extends Throwable:
  /**
   * Represents an error that occurred while writing data to disk.
   *
   * @param cause the underlying exception that caused the write failure
   */
  case WriteError(cause: Throwable)

  /**
   * Represents an error that occurred while reading data from disk.
   *
   * @param cause the underlying exception that caused the read failure
   */
  case ReadError(cause: Throwable)

  /**
   * Represents an error that occurred while decoding serialized data.
   *
   * @param cause the underlying exception that caused the decode failure
   */
  case DecodeError(cause: Throwable)

  /**
   * Represents a failure while deleting a save file.
   * 
   * @param cause the underlying exception that caused the delete failure
   */
  case DeleteError(cause: Throwable)

/**
 * Utility object that maps low-level exceptions to the corresponding [[SaveError]].
 *
 * The handler separates write and read/decode failures:
 * - write-related failures are mapped to [[SaveError.WriteError]]
 * - load/read failures are mapped to either [[SaveError.ReadError]] or [[SaveError.DecodeError]]
 */
object SaveErrorHandler:
  /**
   * Converts a thrown exception into a [[SaveError.WriteError]].
   *
   * Typical cases include missing files, access denied errors, and generic I/O failures.
   *
   * @param cause the original exception thrown during save
   * @return a [[SaveError.WriteError]] wrapping the original cause
   */
  def handleSaveErrors(cause: Throwable): SaveError.WriteError =
    cause match
      case e: FileNotFoundException => SaveError.WriteError(e) // File inaccessible
      case e: AccessDeniedException => SaveError.WriteError(e) // Permission denied
      case e: IOException           => SaveError.WriteError(e)

  /**
   * Converts a thrown exception into either a [[SaveError.ReadError]] or a [[SaveError.DecodeError]].
   *
   * File access problems are mapped to [[SaveError.ReadError]],
   * while parsing/decoding-related exceptions are mapped to [[SaveError.DecodeError]].
   *
   * @param cause the original exception thrown during load
   * @return a [[SaveError.ReadError]] or [[SaveError.DecodeError]] depending on the failure type
   */
  def handleLoadErrors(cause: Throwable): SaveError.ReadError | SaveError.DecodeError =
    cause match
      case e: AbortException         => SaveError.DecodeError(e) // uPickle parsing error
      case e: TraceException         => SaveError.DecodeError(e) // uPickle trace errors
      case e: ParsingFailedException => SaveError.DecodeError(e) // uJSON parsing error
      case e: NoSuchFileException    => SaveError.ReadError(e) // Missing files
      case e: FileNotFoundException  => SaveError.ReadError(e) // File inaccessible
      case e: AccessDeniedException  => SaveError.ReadError(e) // Permission denied
      case e: IOException            => SaveError.ReadError(e)