
package composicion

object EjemploAndThen01 extends App {

  val f = (x: Int) => x + 1
  val g = (x: Int) => x * 2

  val h = f.andThen(g)
  println(h(1)) // 4

  val i = f.compose(g)
  println(i(1)) // 3

}

// En este ejemplo, j es una composición de h (que a su vez es una composición de f y g) y k. La función j toma un entero, suma 1 (f), multiplica por 2 (g) y resta 1 (k).
// Por otro lado, l es una composición de k y i (que a su vez es una composición de g y f). La función l toma un entero, multiplica por 2 (g), suma 1 (f) y resta 1 (k).
object EjemploAndThen02 extends App {
  val f = (x: Int) => x + 1 // f(x) = x+1
  val g = (x: Int) => x * 2 // g(x) = x*2
  val h = f.andThen(g) // h(x) = g(f(x)) = 2*(x+1)
  println(h(1)) // 4

  val i = f.compose(g) // i(x) = f(g(x)) = 2x+1
  println(i(1)) // 3

  val k = (x: Int) => x - 1 // k(x) = x-1

  val j = h.andThen(k) // j(x) = k(h(x)) = 2*(x+1) - 1
  println(j(1)) // 3

  val l = k.compose(i) // l(x) = k(i(x)) = 2x+1 - 1
  println(l(1)) // 2
}

object EjemploAndThen03 extends App {
  // Definimos tres funciones simples
  val addOne = (x: Int) => x + 1
  val double = (x: Int) => x * 2
  val subtractOne = (x: Int) => x - 1

  // La composición usando `andThen` permite una fácil lectura y comprensión
  val compute = addOne.andThen(double).andThen(subtractOne)

  // Usamos la función compuesta
  println(compute(5)) // 11
}
