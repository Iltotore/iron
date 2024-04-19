package io.github.iltotore.iron

import scala.collection.IterableOnceOps
import scala.concurrent.{ExecutionContext, Future}

/**
 * A typeclass providing a `map` method. Mainly used to abstract over Cats and ZIO Prelude.
 *
 * @tparam F the wrapper type
 */
trait MapLogic[F[_]]:

  def map[A, B](wrapper: F[A], f: A => B): F[B]

object MapLogic:

  given [C, CC[x] <: IterableOnceOps[x, CC, C]]: MapLogic[CC] with

    def map[A, B](wrapper: CC[A], f: A => B): CC[B] = wrapper.map(f)

  given [L]: MapLogic[[x] =>> Either[L, x]] with

    override def map[A, B](wrapper: Either[L, A], f: A => B): Either[L, B] = wrapper.map(f)

  given (using ExecutionContext): MapLogic[Future] with

    override def map[A, B](wrapper: Future[A], f: A => B): Future[B] = wrapper.map(f)
