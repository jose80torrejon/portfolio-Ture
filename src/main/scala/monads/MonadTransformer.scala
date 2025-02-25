
package monads

import cats.Monad

object MonadTransformer extends App {

  // Monad transformer es una clase que envuelve un valor y permite aplicar funciones
  // @param T Tipo del valor
  // @param M Monad, M[_] significa que M es un tipo de dato que recibe un tipo de dato
  // @param value Valor
  final case class MonadTransformer[T, M[_]](value: M[T]) {

    // Permite aplicar una función al valor envuelto
    // @param f Función
    // @tparam B Tipo del resultado
    def map[B](f: T => B)(implicit monad: Monad[M]): MonadTransformer[B, M] =
      MonadTransformer(monad.map(value)(f))

    // Permite aplicar una función al valor envuelto
    // @param that Función envuelta
    // @tparam B Tipo del resultado
    def flatMap[B](f: T => MonadTransformer[B, M])(implicit monad: Monad[M]): MonadTransformer[B, M] =
      MonadTransformer(monad.flatMap(value)(f(_).value))

    // Permite aplicar una función envuelta al valor envuelto
    // @param that Función envuelta
    // @tparam B Tipo del resultado
    def <*>[B](that: MonadTransformer[T => B, M])(implicit monad: Monad[M]): MonadTransformer[B, M] =
      MonadTransformer(monad.flatMap(that.value)(f => monad.map(value)(f)))

  }

  // Función que suma 1 a un entero
  val addOne: Int => Int = _ + 1
  // Valor envuelto
  val x: MonadTransformer[Int, List] = MonadTransformer(List(5))

  // Función envuelta
  val addOneWrapped: MonadTransformer[Int => Int, List] = MonadTransformer(List(addOne))

  // Aplicamos la función envuelta al valor envuelto
  val result: MonadTransformer[Int, List] = x <*> addOneWrapped

  println(result.value) // Output: List(6)


}
