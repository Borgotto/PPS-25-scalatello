package it.unibo.pps.view.cli.io

import IO.write

import org.jline.reader.LineReader

enum ReadResult:
  case Success(value: String)
  case SaveInterrupt

class InputComponent(private val reader: LineReader):

  def read(): IO[String] = IO(() => reader.readLine())

  private def interruptableRead(): IO[ReadResult] = IO(() =>
    try
      ReadResult.Success(reader.readLine())
    catch
      case _: SaveInterruptException => ReadResult.SaveInterrupt
  )

  def askForValidInput[T](
    request: String,
    isInputValid: String => Boolean,
    convert: String => T,
    invalidInputMessage: String
  )(using onSaveInterrupt: => IO[Unit]): IO[T] =
    for
      _ <- write(request)
      result <- interruptableRead()
      convertedInput <- handleReadResult(
        result,
        request,
        isInputValid,
        convert,
        invalidInputMessage
      )
    yield convertedInput

  private def handleReadResult[T](
    result: ReadResult,
    request: String,
    isInputValid: String => Boolean,
    convert: String => T,
    invalidInputMessage: String
  )(using onSaveInterrupt: => IO[Unit]): IO[T] = result match
    case ReadResult.SaveInterrupt => 
      for
        _ <- onSaveInterrupt
        input <- askForValidInput(request, isInputValid, convert, invalidInputMessage)
      yield input
    case ReadResult.Success(input) =>
      val isValid = isInputValid(input)
      if isValid then
        IO(() => convert(input))
      else
        for
          _ <- write(invalidInputMessage)
          input <- askForValidInput(request, isInputValid, convert, invalidInputMessage)
        yield input
  
  def isValidOptionChoice(nOptions: Int)(input: String): Boolean =
    isConvertibleToInt(input) && isWithinBounds(1, nOptions)(input.toInt)

  def isConvertibleToInt(s: String): Boolean = s.toIntOption match
    case Some(_) => true
    case _ => false

  def isWithinBounds(min: Int, max: Int)(n: Int): Boolean = n >= min && n <= max
  