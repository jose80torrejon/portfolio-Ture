package eoi.de.examples
package packageobjects.domain

import org.scalatest.funsuite.AnyFunSuite

class IntroTypeAliasTest extends AnyFunSuite {

  import IntroTypeAlias._

  test("Type alias regarding Passenger Info") {
    assert(listOfPassengerInfo1 == List(("Alice", 25), ("Bob", 30), ("Charlie", 35)))
    assert(listOfPassengerInfo2 == List(("Alice", 25), ("Bob", 30), ("Charlie", 35)))
  }

  test("Type alias regarding Integer to Integer function") {
    assert(compute(5) == 11)
    assert(compute(6) == 13)
    assert(compute(7) == 15)
    assert(compute(0) == 1)
    assert(compute(-1) == -1)
    assert(addOne(5) == 6)
    assert(addOne(0) == 1)
    assert(addOne(-1) == 0)
    assert(double(5) == 10)
    assert(subtractOne(5) == 4)

  }
}