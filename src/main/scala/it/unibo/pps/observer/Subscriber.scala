package it.unibo.pps.observer

trait Subscriber[State]:
  def update(state: State): Unit
