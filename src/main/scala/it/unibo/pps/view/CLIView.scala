package it.unibo.pps.view

import it.unibo.pps.state.MatchState
import it.unibo.pps.view.i18n.I18n
import it.unibo.pps.view.io.{IO, given_Monad_IO}
import it.unibo.pps.view.io.IO.{read, write}

class CLIView(private val i18n: I18n) extends View(i18n):

  override def showMenu(): Unit =
    println(i18n.t("menu.title"))
    askForOptionSelection()

  private def askForOptionSelection(): IO[Unit] =
    for
      _ <- write(i18n.t("menu.new_game"))
      _ <- write(i18n.t("menu.load_saved_game"))
      _ <- write(i18n.t("menu.quit"))
      _ <- write(i18n.t("menu.option_request"))
      option <- read()
      _ <- handleSelectedOption(option)
    yield ()

  private def handleSelectedOption(option: String): IO[Unit] = option match
    case "1" => write("")
    case "2" => write("")
    case "3" => write(i18n.t("menu.exit_message"))
    case _ => 
      for
        _ <- write(i18n.t("menu.invalid_option"))
        _ <- askForOptionSelection() 
      yield ()

  override def update(state: MatchState): Unit = ???
