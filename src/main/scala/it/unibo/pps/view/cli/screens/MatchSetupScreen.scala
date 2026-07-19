package it.unibo.pps.view.cli.screens

import it.unibo.pps.controller.Controller
import it.unibo.pps.domain.{Color, Shape}
import it.unibo.pps.view.cli.io.IO.write
import it.unibo.pps.view.cli.io.{InputComponent, IO, given_Monad_IO}
import it.unibo.pps.view.i18n.I18n

enum ColorOption(val code: String):
  case Black extends ColorOption("1")
  case White extends ColorOption("2")

enum ShapeOption(val code: String):
  case Square extends ShapeOption("1")
  case Rectangular extends ShapeOption("2")

class MatchSetupMenu(
  i18n: I18n,
  inputComponent: InputComponent,
  controller: Controller,
  enableSaveShortcut: () => Unit
) extends CLIScreen:

  private val minBoardSize = 4

  given onSaveInterrupt: IO[Unit] = IO(() => ())

  override def render(): IO[Unit] =
    for
      userColor <- askForUserColor()
      shape <- askForBoardShape()
      _ <- showMatchStartMessage()
      _ <- IO(() => enableSaveShortcut())
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
      option <- inputComponent.askForValidInput(
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
      option <- inputComponent.askForValidInput(
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
        size <- inputComponent.askForValidInput(
          i18n.t("setup_menu.board.square_size_question"),
          isSelectedSizeValid,
          _.toInt,
          i18n.t("setup_menu.board.invalid_size")
        )
        shape <- IO(() => Shape.Square(size))
      yield shape
    case ShapeOption.Rectangular.code =>
      for
        height <- inputComponent.askForValidInput(
          i18n.t("setup_menu.board.rectangle_height_question"),
          isSelectedSizeValid,
          _.toInt,
          i18n.t("setup_menu.board.invalid_size")
        )
        width <- inputComponent.askForValidInput(
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