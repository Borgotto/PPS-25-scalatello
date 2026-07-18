package it.unibo.pps.observer

/**
 * Defines an entity that listens to state updates notified by a [[Publisher]].
 * @tparam State the type of the state.
 */
trait Subscriber[State]:

  /**
   * Defines the behaviour of this [[Subscriber]] upon the notification of a new state.
   * @param state the notified state.
   */
  def update(state: State): Unit
