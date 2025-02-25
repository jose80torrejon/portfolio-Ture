package eoi.de.examples
package composicion

import org.scalatest.funsuite.AnyFunSuite

class EjemploAndThen03Test extends AnyFunSuite {

  // Definimos tres funciones simples
  val addOne: Int => Int = (x: Int) => x + 1
  val double: Int => Int = (x: Int) => x * 2
  val subtractOne: Int => Int = (x: Int) => x - 1

  // La composición usando `andThen` permite una fácil lectura y comprensión
  val compute: Int => Int = addOne andThen double andThen subtractOne

  test("compute function computes correctly") {
    assert(compute(5) == 11)
    assert(compute(6) == 13)
    assert(compute(7) == 15)
    assert(compute(0) == 1)
    assert(compute(-1) == -1)

  }

}
