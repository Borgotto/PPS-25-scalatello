package it.unibo.pps

import it.unibo.pps.view.cli.CLIView
import it.unibo.pps.view.i18n.I18n

import java.util.Locale

/** Provides the entry point of the application. */
object Scalatello:

  /** Implements the entry point of the application. */
  @main def main(): Unit = 
    given i18n: I18n = I18n(Locale.ENGLISH)
    CLIView().show()
