package it.unibo.pps.utils

/** A typeclass representing a monad for a type constructor `M[_]`.
 *
 * @tparam M the type constructor representing the monadic context.
 */
trait Monad[M[_]]:

  /** Wraps a value into a monad.
   *
   * @param a the value to wrap.
   * @tparam A the type of the value to wrap.
   * @return the value wrapped inside the monad `M`.
   */
  def unit[A](a: A): M[A]

  extension [A](m: M[A])

    /** Sequentially composes two monads by applying a function
     *  to the value inside the monad `M[A]`.
     *
     * @param f the function taking the inner value of type `A` and returning the new monad `M[B]`.
     * @tparam B the result type of the new monad.
     * @return the combined monad `M[B]`.
     */
    def flatMap[B](f: A => M[B]): M[B]

    /** Transforms the value inside a monad `M[A]` by applying a function.
     *
     * @param f the transformation function from `A` to `B`.
     * @tparam B the target type of the transformation.
     * @return a new monad `M[B]` containing the transformed value.
     */
    def map[B](f: A => B): M[B] = m.flatMap(a => unit(f(a)))
