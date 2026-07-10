package it.unibo.pps.controller

import it.unibo.pps.state.MatchState

trait SaveManager(filePath: String):
  def save(matchState: MatchState): Unit

class SaveManagerImpl(val filePath: String) extends SaveManager(filePath):
  def save(matchState: MatchState): Unit = ???
