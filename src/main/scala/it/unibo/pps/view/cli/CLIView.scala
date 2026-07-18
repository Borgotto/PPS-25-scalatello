package it.unibo.pps.view.cli

import it.unibo.pps.controller.Controller
import it.unibo.pps.domain.ActivePlayer.*
import it.unibo.pps.domain.MatchStatus.*
import it.unibo.pps.domain.{Color, Position, Shape}
import it.unibo.pps.state.{BoardState, MatchState}
import it.unibo.pps.view.View
import it.unibo.pps.view.cli.io.BoardRenderingExtensions.render
import it.unibo.pps.view.cli.io.{CLIInputComponent, IO, ShortcutListener, Sanitizer, given_Monad_IO}
import it.unibo.pps.view.cli.io.IO.write
import it.unibo.pps.view.cli.io.Sanitizer.*
import it.unibo.pps.view.i18n.I18n

import scala.util.{Failure, Success}
import org.jline.reader.{LineReader, LineReaderBuilder}
import org.jline.terminal.TerminalBuilder

enum Action(val code: String):
  case NewGame extends Action("1")
  case SaveFiles extends Action("2")
  case Quit extends Action("3")

enum ShapeOption(val code: String):
  case Square extends ShapeOption("1")
  case Rectangular extends ShapeOption("2")

enum ColorOption(val code: String):
  case Black extends ColorOption("1")
  case White extends ColorOption("2")

enum SaveMenuOption(val code: String):
  case Load extends SaveMenuOption("1")
  case Delete extends SaveMenuOption("2")

class CLIView(private val i18n: I18n) extends View(i18n) with ShortcutListener:

  private val minBoardSize = 4

  private val controller = Controller(this)

  private var lastMatchState: MatchState = _

  private val terminal = TerminalBuilder.builder().system(true).build()
  protected val reader: LineReader = LineReaderBuilder.builder().terminal(terminal).build()
  
  private val inputComponent = CLIInputComponent(reader)
  
  // Input

  private def askForInput[T](
    request: String,
    isInputValid: String => Boolean,
    convert: String => T,
    invalidInputMessage: String
  ): IO[T] =
    given localInterrupt: IO[T] =
      for
        _ <- showSaveCreationMenu()
        retry <- inputComponent.askForValidInput(
          request, 
          isInputValid, 
          convert, 
          invalidInputMessage
        )
      yield retry
    inputComponent.askForValidInput(
      request,
      isInputValid,
      convert,
      invalidInputMessage
    )(using localInterrupt)

  // Main menu

  override def showMainMenu(): Unit =
    for
      _ <- write(i18n.t("main_menu.title"))
      _ <- askForActionSelection()
    yield ()

  private def askForActionSelection(): IO[Unit] =
    val options = Seq(
      i18n.t("main_menu.actions.new_game"),
      i18n.t("main_menu.actions.load_saved_game"),
      i18n.t("main_menu.actions.quit")
    )
    for
      _ <- write(options.mkString("\n", "\n", ""))
      action <- askForInput(
        i18n.t("main_menu.action_request"),
        inputComponent.isValidOptionChoice(options.size),
        identity,
        i18n.t("generic.invalid_choice")
      )
      _ <- handleSelectedAction(action)
    yield ()

  private def handleSelectedAction(option: String): IO[Unit] = option match
    case Action.NewGame.code => setupMatch()
    case Action.SaveFiles.code => showSaveManagementMenu()
    case Action.Quit.code => write(i18n.t("main_menu.exit_message"))

  // Match setup menu

  private def setupMatch(): IO[Unit] =
    for
      userColor <- askForUserColor()
      shape <- askForBoardShape()
      _ <- showMatchStartMessage()
      _ <- IO(() => enableSaveShortcut(showSaveManagementMenu()))
      _ <- IO(() => controller.startMatch(shape, userColor))
    yield ()

  private def askForUserColor(): IO[Color] =
    val options = Seq(
      i18n.t("setup_menu.user.color_black"),
      i18n.t("setup_menu.user.color_white"),
    )
    for
      _ <- write(i18n.t("setup_menu.user.color_question"))
      _ <- write(options.mkString("\n", "\n", ""))
      option <- askForInput(
        i18n.t("generic.choice_request"),
        inputComponent.isValidOptionChoice(options.size),
        identity,
        i18n.t("generic.invalid_choice")
      )
      color <- handleSelectedColor(option)
    yield color

  private def handleSelectedColor(option: String): IO[Color] = option match
    case ColorOption.Black.code => IO(() => Color.Black)
    case ColorOption.White.code => IO(() => Color.White)

  private def askForBoardShape(): IO[Shape] =
    val options = Seq(
      i18n.t("setup_menu.board.square_shape"),
      i18n.t("setup_menu.board.rectangular_shape"),
    )
    for
      _ <- write(i18n.t("setup_menu.board.shape_question"))
      _ <- write(options.mkString("\n", "\n", ""))
      option <- askForInput(
        i18n.t("generic.choice_request"),
        inputComponent.isValidOptionChoice(options.size),
        identity,
        i18n.t("generic.invalid_choice")
      )
      shape <- handleSelectedShape(option)
    yield shape

  private def handleSelectedShape(option: String): IO[Shape] = option match
    case ShapeOption.Square.code =>
      for
        size <- askForInput(
          i18n.t("setup_menu.board.square_size_question"),
          isSelectedSizeValid,
          _.toInt,
          i18n.t("setup_menu.board.invalid_size")
        )
        shape <- IO(() => Shape.Square(size))
      yield shape
    case ShapeOption.Rectangular.code =>
      for
        height <- askForInput(
          i18n.t("setup_menu.board.rectangle_height_question"),
          isSelectedSizeValid,
          _.toInt,
          i18n.t("setup_menu.board.invalid_size")
        )
        width <- askForInput(
          i18n.t("setup_menu.board.rectangle_width_question"),
          isSelectedSizeValid,
          _.toInt,
          i18n.t("setup_menu.board.invalid_size")
        )
        shape <- IO(() => Shape.Rectangle(height, width))
      yield shape

  private def isSelectedSizeValid(size: String): Boolean = size.toIntOption match
      case Some(n) if n % 2 == 0 && n >= minBoardSize => true
      case _ => false

  private def showMatchStartMessage(): IO[Unit] =
    for
      _ <- write(i18n.t("match.match_started_message"))
      _ <- write(i18n.t("match.legend"))
      _ <- write(i18n.t("match.save_shortcut"))
    yield ()

  // Match view

  override def update(state: MatchState): Unit =
    lastMatchState = state
    println(state.board.render())
    handleState(state)

  private def handleState(state: MatchState): Unit =
    state.status match
      case InProgress => state.activePlayer match
        case User => onUserTurn(state.board)
        case Opponent => onOpponentTurn()
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
      row <- askForInput(
        i18n.t("match.placement_request_row"),
        isValidRowForPlacement(state),
        _.toInt,
        i18n.t("match.invalid_row")
      )
      column <- askForInput(
        i18n.t("match.placement_request_column"),
        isValidColumnForPlacement(state, row),
        _.toInt,
        i18n.t("match.invalid_column")
      )
      position <- IO(() => Position(row, column))
    yield position

  private def isValidRowForPlacement(state: BoardState)(s: String): Boolean =
    inputComponent.isConvertibleToInt(s)
      && inputComponent.isWithinBounds(0, state.shape.maxRow)(s.toInt)
      && isRowAmongAvailablePlacements(state.userAvailablePlacements)(s.toInt)

  private def isValidColumnForPlacement(state: BoardState, selectedRow: Int)(s: String): Boolean =
    inputComponent.isConvertibleToInt(s)
      && inputComponent.isWithinBounds(0, state.shape.maxColumn)(s.toInt)
      && state.userAvailablePlacements.contains(Position(selectedRow, s.toInt))

  private def isRowAmongAvailablePlacements(availablePlacements: Set[Position])(row: Int): Boolean =
    availablePlacements.map(position => position.row).contains(row)
  
  private def onMatchEnd(matchResultMessageKey: String): Unit =
    for
      _ <- IO(() => disableSaveShortcut())
      _ <- write(i18n.t(matchResultMessageKey))
      _ <- write(i18n.t("generic.back_to_menu_message"))
      _ <- IO(() => showMainMenu())
    yield ()

  // Save creation menu

  private def showSaveCreationMenu(): IO[Unit] =
    for
      input <- askForInput(
        i18n.t("save_creation_menu.save_request"),
        s => s.toLowerCase() == "y" || s.toLowerCase() == "n",
        identity,
        i18n.t("save_creation_menu.invalid_choice")
      )
      saveMatch <- IO(() => input.toLowerCase() == "y")
      _ <- if saveMatch then handleSave() else IO(() => update(lastMatchState))
    yield ()

  private def handleSave(): IO[Unit] =
    for
      _ <- write(i18n.t("save_creation_menu.filename_request"))
      filename <- inputComponent.read()
      sanitizedFilename <- IO(() => sanitize(filename))
      result <- IO(() => controller.saveMatch(sanitizedFilename))
      _ <- result match
        case Success(_) => write(i18n.t("save_creation_menu.save_success"))
        case Failure(exception) => write(i18n.t("save_creation_menu.save_failure") :+ exception.getMessage)
      _ <- IO(() => update(lastMatchState))
    yield ()

  // Save management menu (load and delete)

  private def showSaveManagementMenu(): IO[Unit] =
    val options = Seq(
      i18n.t("save_menu.load_action"),
      i18n.t("save_menu.delete_action")
    )
    for
      _ <- write(i18n.t("save_menu.action_request"))
      _ <- write(options.mkString("\n", "\n", ""))
      option <- askForInput(
        i18n.t("generic.choice_request"),
        inputComponent.isValidOptionChoice(options.size),
        identity,
        i18n.t("generic.invalid_choice")
      )
      _ <- handleSelectedSaveMenuOption(option)
    yield ()

  private def handleSelectedSaveMenuOption(option: String): IO[Unit] = option match
    case SaveMenuOption.Load.code => showSaveLoadingMenu()
    case SaveMenuOption.Delete.code => showSaveDeletionMenu()

  private def saveFileOptions: String =
    controller.saveFileNames
      .zipWithIndex
      .map((filename, index) => s"[${index + 1}] $filename")
      .mkString("\n", "\n", "")

  private def saveFilesCount: Int = controller.saveFileNames.size

  private def showSaveLoadingMenu(): IO[Unit] =
    for
      _ <- write(i18n.t("save_loading_menu.file_choice_request"))
      _ <- write(saveFileOptions)
      position <- askForInput(
        i18n.t("generic.choice_request"),
        inputComponent.isValidOptionChoice(saveFilesCount),
        _.toInt,
        i18n.t("generic.invalid_choice", saveFilesCount)
      )
      _ <- handleFileLoading(position)
    yield ()

  private def handleFileLoading(position: Int): IO[Unit] =
    val fileName = controller.saveFileNames(position - 1)
    val result = controller.loadMatch(fileName)
    result match
      case Success(state: MatchState) =>
        for
          _ <- write(i18n.t("save_loading_menu.loading_success"))
          _ <- IO(() => update(state))
        yield ()
      case Failure(_) =>
        for
          _ <- write(i18n.t("save_loading_menu.loading_failure"))
          _ <- write(i18n.t("generic.back_to_menu_message"))
          _ <- IO(() => showMainMenu())
        yield ()

  private def showSaveDeletionMenu(): IO[Unit] =
    for
      _ <- write(i18n.t("save_deletion_menu.file_choice_request"))
      _ <- write(saveFileOptions)
      position <- askForInput(
        i18n.t("generic.choice_request"),
        inputComponent.isValidOptionChoice(saveFilesCount),
        _.toInt,
        i18n.t("generic.invalid_choice", saveFilesCount)
      )
      _ <- handleFileDeletion(position)
      _ <- write(i18n.t("generic.back_to_menu_message"))
      _ <- IO(() => showMainMenu())
    yield ()

  private def handleFileDeletion(position: Int): IO[Unit] =
    val fileName = controller.saveFileNames(position - 1)
    val result = controller.deleteSaveFile(fileName)
    result match
      case Success(_) => write(i18n.t("save_deletion_menu.loading_success"))
      case Failure(_) => write(i18n.t("save_deletion_menu.loading_failure"))
