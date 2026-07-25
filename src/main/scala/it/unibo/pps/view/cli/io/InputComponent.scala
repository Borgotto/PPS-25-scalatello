package it.unibo.pps.view.cli.io

import it.unibo.pps.view.cli.io.Sanitizer.sanitize
import it.unibo.pps.view.cli.io.IO.write
import it.unibo.pps.view.i18n.I18n

import org.jline.reader.LineReader

/** Provides reusable functions for multi-step I/O operations on CLI.
 *
 * @param reader the [[LineReader]] instance that reads from the terminal.
 * @param i18n the i18n provider of the application.
 */
class InputComponent(private val reader: LineReader)(using i18n: I18n):

  private enum ReadResult:
    case Success(value: String)
    case SaveInterrupt

  def pass: IO[Unit] = IO(() => ())

  private def interruptableRead(): IO[ReadResult] = IO(() =>
    try ReadResult.Success(reader.readLine())
    catch case _: SaveInterruptException => ReadResult.SaveInterrupt
  )

  /** Implements the I/O scenario in which the user has to choose an option
   *  from a list of options.
   *
   * Firstly, if its key is provided, a string that explicits the request is shown.
   *
   * Secondly, the options are displayed as a list indexed from 1 to the number of options.
   *
   * Thirdly, if its key is provided, a string that explicits an extra message is shown.
   *
   * Lastly, the user is prompted to choose one of the listed options by entering the number
   * associated to the chosen option. The input is then validated. If the input is valid,
   * then the chosen option is handled accordingly. If the input is not valid,
   * the user is notified about that and then prompted to enter their choice again.
   *
   * @param requestKey the key that identifies the string to show as request.
   * @param options the options to display.
   * @param extraMessageKey the key that identifies the string to show as extra message.
   * @param handleSelectedOption the function that defines the behavior according to the selected option.
   * @tparam T the type of value returned at the end of the operation.
   * @return an [[IO]] action that triggers the described I/O scenario when executed and wraps
   *         the input value.
   */
  def askForOption[T](
    requestKey: Option[String] = None,
    options: Seq[String],
    extraMessageKey: Option[String] = None,
    handleSelectedOption: Int => IO[T]
  ): IO[T] =
    for
      _ <- requestKey.map(key => write(i18n.t(key))).getOrElse(pass)
      _ <- displayOptions(options)
      _ <- extraMessageKey.map(key => write(i18n.t(key))).getOrElse(pass)
      option <- askForInteger(
        requestKey = "generic.choice_request",
        isNumberValid = isValidOption(options.size),
        invalidInputMessageKey = "generic.invalid_choice"
      )
      ordinal <- IO(() => option - 1)
      output <- handleSelectedOption(ordinal)
    yield output

  /** Implements the I/O scenario in which the user has to enter a valid integer.
   *
   * Firstly, the user is prompted to enter an integer that satisfies the provided
   * predicate. The input is then validated. If the input is valid, then it is returned
   * by this function. If the input is not valid, the user is notified about that and then
   * prompted to enter their choice again.
   *
   * @param requestKey the key that identifies the string to show as request.
   * @param isNumberValid the predicate that determines if an integer is a valid choice.
   * @param invalidInputMessageKey the key that identifies the string to show in case the provided
   *                               input is not valid.
   * @param onSaveInterrupt the behavior in case the read is interrupted by a save interrupt.
   * @return an [[IO]] action that triggers the described I/O scenario when executed and wraps
   *         the input value.
   */
  def askForInteger(
    requestKey: String,
    isNumberValid: Int => Boolean,
    invalidInputMessageKey: String
  )(using onSaveInterrupt: => IO[Unit] = pass): IO[Int] =
    askForValidInput(
      requestKey = requestKey,
      isInputValid = input => isConvertibleToInt(input) && isNumberValid(input.toInt),
      convert = _.toInt,
      invalidInputMessageKey = invalidInputMessageKey
    )

  /** Implements the I/O scenario in which the user has to answer a yes/no question.
   *
   * After being shown the provided question, the user has to either accept (by typing 'y' or 'Y')
   * or deny (by typing 'n' or 'N'). If the input is not valid, the user is notified about that
   * and then prompted to enter their choice again.
   *
   * @param requestKey the key that identifies the string to show as request.
   * @param invalidInputMessageKey the key that identifies the string to show in case the provided
   *                               input is not valid.
   * @return an [[IO]] action that triggers the described I/O scenario when executed and wraps
   *         a boolean value reflecting the choice of the user (true for acceptance, false for denial).
   */
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

  /** Implements the I/O scenario in which the user has to enter the name for a file.
   *
   * Note that the provided filename is sanitized.
   *
   * @param requestKey the key that identifies the string to show as request.
   * @return an [[IO]] action that triggers the described I/O scenario when executed and wraps
   *         the sanitized filename.
   */
  def askForFilename(requestKey: String): IO[String] =
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
  
  private def isValidOption(numOptions: Int)(option: Int): Boolean = (1 to numOptions).contains(option)

  private def isConvertibleToInt(s: String): Boolean = s.toIntOption.isDefined
