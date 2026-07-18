package it.unibo.pps.observer

/**
 * Defines an entity that notifies other entities about a specific state.
 * @tparam State the type of the state.
 */
trait Publisher[State]:
  
  /**
   * Subscribes a new entity to this [[Publisher]].
   * @param subscriber the entity to subscribe.
   */
  def subscribe(subscriber: Subscriber[State]): Unit

  /**
   * Unsubscribes an entity from this [[Publisher]].
   * @param subscriber the entity to unsubscribe.
   */
  def unsubscribe(subscriber: Subscriber[State]): Unit

  /**
   * Notifies a state to all subscribers of this [[Publisher]].
   * @param state the state to notify.
   */
  def notifySubscribers(state: State): Unit
  