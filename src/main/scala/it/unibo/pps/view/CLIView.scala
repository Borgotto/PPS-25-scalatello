package it.unibo.pps.view

import it.unibo.pps.controller.MatchController
import it.unibo.pps.state.{BoardState, MatchState}
import it.unibo.pps.state.PlayerState.{Opponent, User}
import it.unibo.pps.utils.MatchStatus.*
import it.unibo.pps.utils.{Color, Position, Shape}
import it.unibo.pps.view.i18n.I18n
import it.unibo.pps.view.io.BoardRenderingExtensions.render
import it.unibo.pps.view.io.{IO, Sanitizer, SaveInterruptException, ShortcutListener, given_Monad_IO}
import it.unibo.pps.view.io.Sanitizer.*
import it.unibo.pps.view.io.IO.write
import org.jline.terminal.TerminalBuilder
import org.jline.reader.{LineReader, LineReaderBuilder}

enum Action(val code: String):
  case NewGame extends Action("1")
  case LoadSavedGame extends Action("2")
  case Quit extends Action("3")

enum ShapeOption(val code: String):
  case Square extends ShapeOption("1")
  case Rectangular extends ShapeOption("2")

enum ColorOption(val code: String):
  case Black extends ColorOption("1")
  case White extends ColorOption("2")

enum ReadResult:
  case Success(value: String)
  case SaveInterruption

class CLIView(private val i18n: I18n) extends View(i18n) with ShortcutListener:

  private val minBoardSize = 4

  private val controller = MatchController(this)

  private var lastMatchState: MatchState = _

  private val terminal = TerminalBuilder.builder().system(true).build()
  protected val reader: LineReader = LineReaderBuilder.builder().terminal(terminal).build()

  private def read(): IO[String] = IO(() => reader.readLine())

  private def interruptableRead(): IO[ReadResult] = IO(() => {
    try
      ReadResult.Success(reader.readLine())
    catch
      case _: SaveInterruptException => ReadResult.SaveInterruption
  })

  override def showMenu(): Unit =
    for
      _ <- write(i18n.t("main_menu.title"))
      _ <- askForActionSelection()
    yield ()

  private def askForActionSelection(): IO[Unit] =
    for
      _ <- write(i18n.t("main_menu.actions.new_game"))
      _ <- write(i18n.t("main_menu.actions.load_saved_game"))
      _ <- write(i18n.t("main_menu.actions.quit"))
      _ <- write(i18n.t("main_menu.action_request"))
      action <- read()
      _ <- handleSelectedAction(action)
    yield ()

  private def handleSelectedAction(option: String): IO[Unit] = option match
    case Action.NewGame.code => setupMatch()
    case Action.LoadSavedGame.code => write("")  // TODO(eboschetti)
    case Action.Quit.code => write(i18n.t("main_menu.exit_message"))
    case _ =>
      for
        _ <- write(i18n.t("main_menu.actions.invalid_action"))
        _ <- askForActionSelection()
      yield ()

  private def setupMatch(): IO[Unit] =
    for
      userColor <- askForUserColor()
      shape <- askForBoardShape()
      _ <- showMatchStartMessage()
      _ <- IO(() => enableSaveShortcut(showSaveMenu()))
      _ <- IO(() => controller.startMatch(shape, userColor))
    yield ()

  private def showMatchStartMessage(): IO[Unit] =
    for
      _ <- write(i18n.t("match.match_started_message"))
      _ <- write(i18n.t("match.legend"))
      _ <- write(i18n.t("match.save_shortcut"))
    yield ()

  private def askForUserColor(): IO[Color] =
    for
      _ <- write(i18n.t("setup_menu.user.color_question"))
      _ <- write(i18n.t("setup_menu.user.color_black"))
      _ <- write(i18n.t("setup_menu.user.color_white"))
      _ <- write(i18n.t("setup_menu.choice_request"))
      option <- read()
      color <- handleSelectedColor(option)
    yield color

  private def handleSelectedColor(option: String): IO[Color] = option match
    case ColorOption.Black.code => IO(() => Color.Black)
    case ColorOption.White.code => IO(() => Color.White)
    case _ =>
      for
        _ <- write(i18n.t("setup_menu.invalid_color"))
        color <- askForUserColor()
      yield color

  private def askForBoardShape(): IO[Shape] =
    for
      _ <- write(i18n.t("setup_menu.board.shape_question"))
      _ <- write(i18n.t("setup_menu.board.square_shape"))
      _ <- write(i18n.t("setup_menu.board.rectangular_shape"))
      _ <- write(i18n.t("setup_menu.choice_request"))
      option <- read()
      shape <- handleSelectedShape(option)
    yield shape

  private def handleSelectedShape(option: String): IO[Shape] = option match
    case ShapeOption.Square.code =>
      for
        size <- askForValidInput(
          "setup_menu.board.square_size_question",
          isSelectedSizeValid,
          _.toInt,
          "setup_menu.board.invalid_size"
        )
        shape <- IO(() => Shape.Square(size))
      yield shape
    case ShapeOption.Rectangular.code =>
      for
        height <- askForValidInput(
          "setup_menu.board.rectangle_height_question",
          isSelectedSizeValid,
          _.toInt,
          "setup_menu.board.invalid_size"
        )
        width <- askForValidInput(
          "setup_menu.board.rectangle_width_question",
          isSelectedSizeValid,
          _.toInt,
          "setup_menu.board.invalid_size"
        )
        shape <- IO(() => Shape.Rectangle(height, width))
      yield shape
    case _ =>
      for
        _ <- write(i18n.t("setup_menu.invalid_shape"))
        shape <- askForBoardShape()
      yield shape

  private def askForValidInput[T](
    requestKey: String,
    isInputValid: String => Boolean,
    convert: String => T,
    invalidInputMessageKey: String
  ): IO[T] =
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
  ): IO[T] = result match
    case ReadResult.SaveInterruption =>
      for
        _ <- showSaveMenu()
        retry <- askForValidInput(requestKey, isInputValid, convert, invalidInputMessageKey)
      yield retry
    case ReadResult.Success(input) =>
      val isValid = isInputValid(input)
      if isValid then
        IO(() => convert(input))
      else
        for
          _ <- write(i18n.t(invalidInputMessageKey))
          retry <- askForValidInput(requestKey, isInputValid, convert, invalidInputMessageKey)
        yield retry

  private def isSelectedSizeValid(size: String): Boolean = size.toIntOption match
      case Some(n) if n % 2 == 0 && n >= minBoardSize => true
      case _ => false

  override def update(state: MatchState): Unit =
    lastMatchState = state
    println(state.board.render())
    handleState(state)

  private def handleState(state: MatchState): Unit =
    state.status match
      case InProgress => state.activePlayer match
        case User(_, _) => onUserTurn(state.board)
        case Opponent(_, _) => onOpponentTurn()
      case UserWon => onMatchEnd("match.result.user_won")
      case OpponentWon => onMatchEnd("match.result.opponent_won")
      case Tie => onMatchEnd("match.result.tie")

  private def onOpponentTurn(): Unit =
    for
      _ <- write(i18n.t("match.opponent_turn_message"))
    yield ()

  private def onUserTurn(state: BoardState): Unit =
    for
      _ <- write(i18n.t("match.user_turn_message"))
      position <- askForValidPlacement(state)
      _ <- IO(() => controller.handleSelection(position))
    yield ()

  private def askForValidPlacement(state: BoardState): IO[Position] =
    for
      row <- askForValidInput(
        "match.placement_request_row",
        isValidRowForPlacement(state),
        _.toInt,
        "match.invalid_row"
      )
      column <- askForValidInput(
        "match.placement_request_column",
        isValidColumnForPlacement(state, row),
        _.toInt,
        "match.invalid_column"
      )
      position <- IO(() => Position(row, column))
    yield position

  private def isValidRowForPlacement(state: BoardState)(s: String): Boolean =
    isConvertibleToInt(s)
      && isWithinBounds(0, state.shape.maxRow)(s.toInt)
      && isRowAmongAvailablePlacements(state.userAvailablePlacements)(s.toInt)

  private def isValidColumnForPlacement(state: BoardState, selectedRow: Int)(s: String): Boolean =
    isConvertibleToInt(s)
      && isWithinBounds(0, state.shape.maxColumn)(s.toInt)
      && state.userAvailablePlacements.contains(Position(selectedRow, s.toInt))

  private def isConvertibleToInt(s: String): Boolean = s.toIntOption match
    case Some(_) => true
    case _ => false

  private def isWithinBounds(min: Int, max: Int)(n: Int): Boolean = n >= min && n <= max

  private def isRowAmongAvailablePlacements(availablePlacements: Set[Position])(row: Int): Boolean =
    availablePlacements.map(position => position.row).contains(row)
  
  private def onMatchEnd(matchResultMessageKey: String): Unit =
    for
      _ <- IO(() => disableSaveShortcut())
      _ <- write(i18n.t(matchResultMessageKey))
      _ <- write(i18n.t("match.back_to_menu_message"))
    yield ()

  private def showSaveMenu(): IO[Unit] =
    for
      input <- askForValidInput(
        i18n.t("save_menu.save_request"),
        s => s.toLowerCase() == "y" || s.toLowerCase() == "n",
        identity,
        i18n.t("save_menu.invalid_choice")
      )
      saveMatch <- IO(() => input.toLowerCase() == "y")
      _ <- if saveMatch then handleSave() else IO(() => update(lastMatchState))
    yield ()

  private def handleSave(): IO[Unit] =
    for
      _ <- write(i18n.t("save_menu.filename_request"))
      filename <- read()
      sanitizedFilename <- IO(() => sanitize(filename))
      _ <- IO(() => controller.saveMatch(sanitizedFilename))
    yield ()
