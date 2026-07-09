package it.unibo.pps.view.io

import it.unibo.pps.utils.Monad

import scala.io.StdIn.readLine

case class IO[A](exec: () => A)

object IO:
  def read(): IO[String] = IO(() => readLine())
  def write[A](a: A): IO[Unit] = IO(() => println(a))

given Monad[IO] with
  def unit[A](a: A): IO[A] = IO(() => a)
  extension [A](m: IO[A])
    def flatMap[B](f: A => IO[B]): IO[B] = m match
      case IO(e) => f(e())
      