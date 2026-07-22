package it.unibo.pps.view.cli.screens

import it.unibo.pps.controller.Controller
import it.unibo.pps.domain.{Color, OpponentType, Shape}
import it.unibo.pps.view.cli.io.IO.write
import it.unibo.pps.view.cli.io.{IO, InputComponent, given_Monad_IO}
import it.unibo.pps.view.i18n.{I18n, localize}

enum ColorOption:
  case Black, White

enum ShapeOption:
  case Square, Rectangular

enum OpponentOption:
  case Random, Easy, Medium, Hard

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

  private def handleSelectedColor(ordinal: Int): IO[Color] = 
    ColorOption.fromOrdinal(ordinal) match
      case ColorOption.Black => IO(() => Color.Black)
      case ColorOption.White => IO(() => Color.White)

  private def askForBoardShape(): IO[Shape] =
    inputComponent.askForOption(
      requestKey = Some("setup_menu.board.shape_question"),
      options = Seq(
        "setup_menu.board.square_shape",
        "setup_menu.board.rectangular_shape",
      ).localize,
      handleSelectedOption = handleSelectedShape
    )

  private def handleSelectedShape(ordinal: Int): IO[Shape] = 
    ShapeOption.fromOrdinal(ordinal) match
      case ShapeOption.Square =>
        for
          size <- inputComponent.askForInteger(
            requestKey = "setup_menu.board.square_size_question",
            isNumberValid = isSelectedSizeValid,
            invalidInputMessageKey = "setup_menu.board.invalid_size"
          )
          shape <- IO(() => Shape.Square(size))
        yield shape
      case ShapeOption.Rectangular =>
        for
          height <- inputComponent.askForInteger(
            requestKey = "setup_menu.board.rectangle_height_question",
            isNumberValid = isSelectedSizeValid,
            invalidInputMessageKey = "setup_menu.board.invalid_size"
          )
          width <- inputComponent.askForInteger(
            requestKey = "setup_menu.board.rectangle_width_question",
            isNumberValid = isSelectedSizeValid,
            invalidInputMessageKey = "setup_menu.board.invalid_size"
          )
          shape <- IO(() => Shape.Rectangle(height, width))
        yield shape

  private def isSelectedSizeValid(size: Int): Boolean = size % 2 == 0 && size >= minBoardSize

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
  
  private def handleSelectedOpponentType(ordinal: Int): IO[OpponentType] = 
    OpponentOption.fromOrdinal(ordinal) match
      case OpponentOption.Random => IO(() => OpponentType.Random)
      case OpponentOption.Easy => IO(() => OpponentType.Easy)
      case OpponentOption.Medium => IO(() => OpponentType.Medium)
      case OpponentOption.Hard => IO(() => OpponentType.Hard)

  private def showMatchStartMessage(): IO[Unit] =
    for
      _ <- write(i18n.t("match.match_started_message"))
      _ <- write(i18n.t("match.legend"))
      _ <- write(i18n.t("match.save_shortcut"))
    yield ()
    