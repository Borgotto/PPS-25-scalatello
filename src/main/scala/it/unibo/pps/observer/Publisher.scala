package it.unibo.pps.observer

trait Publisher[State]:
  def subscribe(subscriber: Subscriber[State]): Unit
  def unsubscribe(subscriber: Subscriber[State]): Unit
  def notifySubscribers(state: State): Unit
  