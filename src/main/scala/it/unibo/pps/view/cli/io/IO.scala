package it.unibo.pps.view.cli.io

import it.unibo.pps.utils.Monad

import scala.io.StdIn.readLine

/** Represents a lazy I/O action that produces a value of type [[A]] when executed.
 *
 *  @param exec the function that wraps the operation that produces the value.
 *  @tparam A the type of the produced value.
 */
case class IO[A](exec: () => A)

/** A utility that provides I/O functions. */
object IO:

  /** Reads a line from the standard input.
   *
   * @return an [[IO]] action that returns the read string when executed.
   */
  def read(): IO[String] = IO(() => readLine())

  /** Prints a value to the standard output.
   *
   * @param a the value to be printed.
   * @tparam A the type of the value to be printed.
   * @return an [[IO]] action that performs the print when executed.
   */
  def write[A](a: A): IO[Unit] = IO(() => println(a))

given Monad[IO] with
  def unit[A](a: A): IO[A] = IO(() => a)
  extension [A](m: IO[A])
    def flatMap[B](f: A => IO[B]): IO[B] = m match
      case IO(e) => f(e())
      