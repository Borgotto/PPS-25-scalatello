package it.unibo.pps.view.cli.screens

import it.unibo.pps.view.cli.io.IO

/** Models the possible operations on a screen of the CLI view. */
trait CLIScreen:

  /** @return an [[IO]] action that displays the screen when executed. */
  def render(): IO[Unit]
