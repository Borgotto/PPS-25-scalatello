package it.unibo.pps

import it.unibo.pps.view.cli.CLIView
import it.unibo.pps.view.i18n.I18n

import java.util.Locale

object Scalatello:

  @main def main(): Unit = 
    val i18n = I18n(Locale.ENGLISH)
    CLIView(i18n).showMainMenu()
