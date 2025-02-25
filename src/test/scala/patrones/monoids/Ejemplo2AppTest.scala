package eoi.de.examples
package patrones.monoids

// Here we are importing the Semigroup instance for Int explicitly
import cats.Monoid
import cats.implicits.catsSyntaxSemigroup
import org.scalatest.funsuite.AnyFunSuite



class Ejemplo2AppTest extends AnyFunSuite {

  test("Monoid setSymmetricDifferenceMonoid should return the set itself when combined with empty set") {
    val conjunto1 = Set(1, 2, 3, 4)
    val conjunto2 = Set.empty[Int]
    val resultado = conjunto1 |+| conjunto2
    assert(resultado === conjunto1)
  }
}