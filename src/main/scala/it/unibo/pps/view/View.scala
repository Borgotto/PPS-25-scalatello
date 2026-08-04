package it.unibo.pps.view

import it.unibo.pps.observer.Subscriber
import it.unibo.pps.state.MatchState

/** Models the possible ways to interact with the view of the application.
 * 
 *  The view is also a [[observer.Subscriber]] that listens to
 *  [[state.MatchState]] updates, in order to display the current state of a match
 *  as it changes.
 */
trait View extends Subscriber[MatchState]:

  /** Displays the view of the application. */
  def show(): Unit
