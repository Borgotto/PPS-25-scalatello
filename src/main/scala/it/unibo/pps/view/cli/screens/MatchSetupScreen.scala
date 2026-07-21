package it.unibo.pps.view.cli.screens

import it.unibo.pps.controller.Controller
import it.unibo.pps.domain.{Color, OpponentType, Shape}
import it.unibo.pps.view.cli.io.IO.write
import it.unibo.pps.view.cli.io.{IO, InputComponent, given_Monad_IO}
import it.unibo.pps.view.i18n.{I18n, localize}

enum ColorOption(val code: String):
  case Black extends ColorOption("1")
  case White extends ColorOption("2")

enum ShapeOption(val code: String):
  case Square extends ShapeOption("1")
  case Rectangular extends ShapeOption("2")

enum OpponentOption(val code: String):
  case Random extends OpponentOption("1")
  case Easy extends OpponentOption("2")
  case Medium extends OpponentOption("3")
  case Hard extends OpponentOption("4")

class MatchSetupScreen(
  controller: Controller,
  enableSaveShortcut: () => Unit
)(using i18n: I18n, inputComponent: InputComponent) extends CLIScreen:

  private val minBoardSize = 4

  override def render(): IO[Unit] =
    for
      userColor <- askForUserColor()
      shape <- askForBoardShape()
      opponentType <- askForOpponentType()
      _ <- showMatchStartMessage()
      _ <- IO(() => enableSaveShortcut())
      _ <- IO(() => controller.startMatch(shape, userColor, opponentType))
    yield ()

  private def askForUserColor(): IO[Color] =
    inputComponent.askForOption(
      requestKey = Some("setup_menu.user.color_question"),
      options = Seq(
        "setup_menu.user.color_black",
        "setup_menu.user.color_white",
      ).localize,
      handleSelectedOption = handleSelectedColor
    )

  private def handleSelectedColor(option: String): IO[Color] = option match
    case ColorOption.Black.code => IO(() => Color.Black)
    case ColorOption.White.code => IO(() => Color.White)

  private def askForBoardShape(): IO[Shape] =
    inputComponent.askForOption(
      requestKey = Some("setup_menu.board.shape_question"),
      options = Seq(
        "setup_menu.board.square_shape",
        "setup_menu.board.rectangular_shape",
      ).localize,
      handleSelectedOption = handleSelectedShape
    )

  private def handleSelectedShape(option: String): IO[Shape] = option match
    case ShapeOption.Square.code =>
      for
        size <- inputComponent.askForValidInput(
          requestKey = "setup_menu.board.square_size_question",
          isInputValid = isSelectedSizeValid,
          convert = _.toInt,
          invalidInputMessageKey = "setup_menu.board.invalid_size"
        )
        shape <- IO(() => Shape.Square(size))
      yield shape
    case ShapeOption.Rectangular.code =>
      for
        height <- inputComponent.askForValidInput(
          requestKey = "setup_menu.board.rectangle_height_question",
          isInputValid = isSelectedSizeValid,
          convert = _.toInt,
          invalidInputMessageKey = "setup_menu.board.invalid_size"
        )
        width <- inputComponent.askForValidInput(
          requestKey = "setup_menu.board.rectangle_width_question",
          isInputValid = isSelectedSizeValid,
          convert = _.toInt,
          invalidInputMessageKey = "setup_menu.board.invalid_size"
        )
        shape <- IO(() => Shape.Rectangle(height, width))
      yield shape

  private def isSelectedSizeValid(size: String): Boolean = size.toIntOption match
    case Some(n) if n % 2 == 0 && n >= minBoardSize => true
    case _ => false

  private def askForOpponentType(): IO[OpponentType] =
    inputComponent.askForOption(
      requestKey = Some("setup_menu.opponent.type_question"),
      options = Seq(
        "setup_menu.opponent.type_random",
        "setup_menu.opponent.type_easy",
        "setup_menu.opponent.type_medium",
        "setup_menu.opponent.type_hard"
      ).localize,
      handleSelectedOption = handleSelectedOpponentType
    )
  
  private def handleSelectedOpponentType(option: String): IO[OpponentType] = option match
    case OpponentOption.Random.code => IO(() => OpponentType.Random)
    case OpponentOption.Easy.code => IO(() => OpponentType.Easy)
    case OpponentOption.Medium.code => IO(() => OpponentType.Medium)
    case OpponentOption.Hard.code => IO(() => OpponentType.Hard)

  private def showMatchStartMessage(): IO[Unit] =
    for
      _ <- write(i18n.t("match.match_started_message"))
      _ <- write(i18n.t("match.legend"))
      _ <- write(i18n.t("match.save_shortcut"))
    yield ()