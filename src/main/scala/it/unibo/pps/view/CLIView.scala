package it.unibo.pps.view

import it.unibo.pps.controller.ControllerImpl
import it.unibo.pps.state.MatchState
import it.unibo.pps.state.PlayerState.{Opponent, User}
import it.unibo.pps.utils.MatchStatus.*
import it.unibo.pps.utils.{Color, Position, Shape}
import it.unibo.pps.view.i18n.I18n
import it.unibo.pps.view.io.BoardRenderingExtensions.render
import it.unibo.pps.view.io.{IO, given_Monad_IO}
import it.unibo.pps.view.io.IO.{read, write}

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

class CLIView(private val i18n: I18n) extends View(i18n):

  private val minBoardSize = 4

  private val controller = ControllerImpl(this)

  override def showMenu(): Unit =
    println(i18n.t("menu.title"))
    askForActionSelection()

  private def askForActionSelection(): IO[Unit] =
    for
      _ <- write(i18n.t("menu.actions.new_game"))
      _ <- write(i18n.t("menu.actions.load_saved_game"))
      _ <- write(i18n.t("menu.actions.quit"))
      _ <- write(i18n.t("menu.action_request"))
      action <- read()
      _ <- handleSelectedAction(action)
    yield ()

  private def handleSelectedAction(option: String): IO[Unit] = option match
    case Action.NewGame.code => setupMatch()
    case Action.LoadSavedGame.code => write("")  // TODO(eboschetti)
    case Action.Quit.code => write(i18n.t("menu.exit_message"))
    case _ =>
      for
        _ <- write(i18n.t("menu.actions.invalid_action"))
        _ <- askForActionSelection()
      yield ()

  private def setupMatch(): IO[Unit] =
    for
      userColor <- askForUserColor()
      shape <- askForBoardShape()
      _ <- IO(() => controller.startMatch(shape, userColor))
    yield ()

  private def askForUserColor(): IO[Color] =
    for
      _ <- write(i18n.t("menu.user.color_question"))
      _ <- write(i18n.t("menu.user.color_black"))
      _ <- write(i18n.t("menu.user.color_white"))
      _ <- write(i18n.t("menu.choice_request"))
      option <- read()
      color <- handleSelectedColor(option)
    yield color

  private def handleSelectedColor(option: String): IO[Color] = option match
    case ColorOption.Black.code => IO(() => Color.Black)
    case ColorOption.White.code => IO(() => Color.White)
    case _ =>
      for
        _ <- write(i18n.t("menu.invalid_color"))
        color <- askForUserColor()
      yield color

  private def askForBoardShape(): IO[Shape] =
    for
      _ <- write(i18n.t("menu.board.shape_question"))
      _ <- write(i18n.t("menu.board.square_shape"))
      _ <- write(i18n.t("menu.board.rectangular_shape"))
      _ <- write(i18n.t("menu.choice_request"))
      option <- read()
      shape <- handleSelectedShape(option)
    yield shape

  private def handleSelectedShape(option: String): IO[Shape] = option match
    case ShapeOption.Square.code =>
      for
        size <- askForValidInput(
          "menu.board.square_size_question",
          isSelectedSizeValid,
          _.toInt,
          "menu.board.invalid_size"
        )
        shape <- IO(() => Shape.Square(size))
      yield shape
    case ShapeOption.Rectangular.code =>
      for
        height <- askForValidInput(
          "menu.board.rectangle_height_question",
          isSelectedSizeValid,
          _.toInt,
          "menu.board.invalid_size"
        )
        width <- askForValidInput(
          "menu.board.rectangle_width_question",
          isSelectedSizeValid,
          _.toInt,
          "menu.board.invalid_size"
        )
        shape <- IO(() => Shape.Rectangle(height, width))
      yield shape
    case _ =>
      for
        _ <- write(i18n.t("menu.invalid_shape"))
        shape <- askForBoardShape()
      yield shape

  private def askForValidInput[T](
    requestKey: String,
    isInputValid: String => IO[Boolean],
    convert: String => T,
    invalidInputMessageKey: String
  ): IO[T] =
    for
      _ <- write(i18n.t(requestKey))
      input <- read()
      isValid <- isInputValid(input)
      convertedInput <- if isValid then IO(() => convert(input)) else
        for
          _ <- write(i18n.t(invalidInputMessageKey))
          convertedInput <- askForValidInput(requestKey, isInputValid, convert, invalidInputMessageKey)
        yield convertedInput
    yield convertedInput

  private def isSelectedSizeValid(size: String): IO[Boolean] = IO(() =>
    size.toIntOption match
      case Some(n) if n % 2 == 0 && n >= minBoardSize => true
      case _ => false
  )

  override def update(state: MatchState): Unit =
    println(state.board.render())
    handleState(state)

  private def handleState(state: MatchState): Unit =
    state.status match
      case InProgress => state.activePlayer match
        case User(_, _) => onUserTurn(state.board.shape.maxRow, state.board.shape.maxColumn)
        case Opponent(_, _) => onOpponentTurn()
      case UserWon => ???
      case OpponentWon => ???
      case Tie => ???

  private def onUserTurn(maxRow: Int, maxColumn: Int): Unit =
    for
      _ <- write(i18n.t("match.user_turn_message"))
      row <- askForValidInput(
        "match.placement_request_row",
        isIntWithinBounds(0, maxRow),
        _.toInt,
        "match.invalid_row"
      )
      column <- askForValidInput(
        "match.placement_request_column",
        isIntWithinBounds(0, maxColumn),
        _.toInt,
        "match.invalid_column"
      )
      _ <- IO(() => controller.handleSelection(Position(row, column)))
    yield ()

  private def isIntWithinBounds(min: Int, max: Int)(s: String): IO[Boolean] = IO(() =>
    s.toIntOption match
      case Some(n) if n >= min && n <= max => true
      case _ => false
  )

  private def onOpponentTurn(): Unit =
    for
      _ <- write(i18n.t("match.opponent_turn_message"))
    yield ()
