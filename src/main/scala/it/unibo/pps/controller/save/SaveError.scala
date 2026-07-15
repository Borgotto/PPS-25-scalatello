package it.unibo.pps.controller.save

// Exceptions that are handled
import java.io.{FileNotFoundException, IOException}
import java.nio.file.{AccessDeniedException, NoSuchFileException}
import upickle.core.AbortException
import upickle.core.TraceVisitor.TraceException
import ujson.ParsingFailedException

enum SaveError extends Throwable:
  case WriteError(cause: Throwable)
  case ReadError(cause: Throwable)
  case DecodeError(cause: Throwable)
  case DeleteError

object SaveErrorHandler:
  def handleSaveErrors(cause: Throwable): SaveError.WriteError =
    cause match
      case e: FileNotFoundException => SaveError.WriteError(e) // File inaccessible
      case e: AccessDeniedException => SaveError.WriteError(e) // Permission denied
      case e: IOException           => SaveError.WriteError(e)

  def handleLoadErrors(cause: Throwable): SaveError.ReadError | SaveError.DecodeError =
    cause match
      case e: AbortException         => SaveError.DecodeError(e) // uPickle parsing error
      case e: TraceException         => SaveError.DecodeError(e) // uPickle trace errors
      case e: ParsingFailedException => SaveError.DecodeError(e) // uJSON parsing error
      case e: NoSuchFileException    => SaveError.ReadError(e) // Missing files
      case e: FileNotFoundException  => SaveError.ReadError(e) // File inaccessible
      case e: AccessDeniedException  => SaveError.ReadError(e) // Permission denied
      case e: IOException            => SaveError.ReadError(e)