package it.unibo.pps

import it.unibo.pps.view.cli.CLIView
import it.unibo.pps.view.i18n.I18n

import java.util.Locale

object Scalatello:

  @main def main(): Unit = 
    given i18n: I18n = I18n(Locale.ENGLISH)
    CLIView().show()
