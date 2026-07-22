package it.unibo.pps.view.cli.io

import it.unibo.pps.view.cli.io.IO.write
import it.unibo.pps.view.i18n.I18n

import org.jline.reader.LineReader

enum ReadResult:
  case Success(value: String)
  case SaveInterrupt

class InputComponent(private val reader: LineReader)(using i18n: I18n):

  def pass: IO[Unit] = IO(() => ())

  private def interruptableRead(): IO[ReadResult] = IO(() =>
    try ReadResult.Success(reader.readLine())
    catch case _: SaveInterruptException => ReadResult.SaveInterrupt
  )

  def askForOption[T](
    requestKey: Option[String] = None,
    options: Seq[String],
    handleSelectedOption: String => IO[T]
  ): IO[T] =
    for
      _ <- requestKey.map(key => write(i18n.t(key))).getOrElse(pass)
      _ <- displayOptions(options)
      option <- askForValidInput(
        requestKey = "generic.choice_request",
        isInputValid = isValidOptionChoice(options.size),
        invalidInputMessageKey = "generic.invalid_choice"
      )
      output <- handleSelectedOption(option)
    yield output

  def askForInteger(
    requestKey: String,
    isNumberValid: Int => Boolean,
    invalidInputMessageKey: String
  ): IO[Int] =
    askForValidInput(
      requestKey = requestKey,
      isInputValid = input => isConvertibleToInt(input) && isNumberValid(input.toInt),
      convert = _.toInt,
      invalidInputMessageKey = invalidInputMessageKey
    )

  def askForConfirmation(
    requestKey: String,
    invalidInputMessageKey: String
  ): IO[Boolean] =
    for
      input <- askForValidInput(
        requestKey = requestKey,
        isInputValid = s => s.toLowerCase() == "y" || s.toLowerCase() == "n",
        invalidInputMessageKey = invalidInputMessageKey
      )
      hasUserConfirmed <- IO(() => input.toLowerCase() == "y")
    yield hasUserConfirmed

  def askForFilename(requestKey: String, sanitize: String => String): IO[String] =
    askForValidInput(
      requestKey = requestKey,
      isInputValid = _ => true, 
      convert = sanitize, 
      invalidInputMessageKey = ""
    )

  private def displayOptions(optionsKeys: Seq[String]): IO[Unit] =
    val indexedOptions = optionsKeys
      .zipWithIndex
      .map((option, index) => s"[${index + 1}] $option")
      .mkString("\n", "\n", "")
    write(indexedOptions)

  private def askForValidInput[T](
    requestKey: String,
    isInputValid: String => Boolean,
    convert: String => T = identity,
    invalidInputMessageKey: String
  )(using onSaveInterrupt: => IO[Unit] = pass): IO[T] =
    for
      _ <- write(i18n.t(requestKey))
      result <- interruptableRead()
      convertedInput <- handleReadResult(
        result,
        requestKey,
        isInputValid,
        convert,
        invalidInputMessageKey
      )
    yield convertedInput

  private def handleReadResult[T](
    result: ReadResult,
    requestKey: String,
    isInputValid: String => Boolean,
    convert: String => T,
    invalidInputMessageKey: String
  )(using onSaveInterrupt: => IO[Unit]): IO[T] = result match
    case ReadResult.SaveInterrupt => 
      for
        _ <- onSaveInterrupt
        input <- askForValidInput(requestKey, isInputValid, convert, invalidInputMessageKey)
      yield input
    case ReadResult.Success(input) =>
      if isInputValid(input) then IO(() => convert(input))
      else
        for
          _ <- write(i18n.t(invalidInputMessageKey))
          input <- askForValidInput(requestKey, isInputValid, convert, invalidInputMessageKey)
        yield input
  
  private def isValidOptionChoice(numOptions: Int)(input: String): Boolean =
    isConvertibleToInt(input) && (1 to numOptions).contains(input.toInt)

  private def isConvertibleToInt(s: String): Boolean = s.toIntOption.isDefined
