package it.unibo.pps.view.observer

trait Subscriber[State]:
  def update(state: State): Unit
