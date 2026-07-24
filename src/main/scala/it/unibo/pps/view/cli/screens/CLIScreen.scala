package it.unibo.pps.view.cli.screens

import it.unibo.pps.view.cli.io.IO

trait CLIScreen:
  def render(): IO[Unit]
