package eoi.de.examples
package estructuras

import org.scalatest.funsuite.AnyFunSuite

class AnimalTests extends AnyFunSuite {

  class Cat(nombre: String)
    extends AnimalConNombre(nombre)
      with Corredor with Volador {
    override val sonido: String = "Meow"
  }

  class Dog(nombre: String)
    extends AnimalConNombre(nombre)
      with Corredor {
    override val sonido: String = "Woof"
  }

  test("Cat correr") {
    val cat = new Cat("Kitty")
    assert(cat.correr() == "Digo Meow porque soy son un Cat - ¡Soy Kitty y estoy corriendo!")
  }

  test("Cat volar") {
    val cat = new Cat("Kitty")
    assert(cat.volar() == "Digo Meow porque soy son un Cat - ¡Soy Kitty y estoy volando!")
  }

  test("Dog correr") {
    val dog = new Dog("Doggy")
    assert(dog.correr() == "Digo Woof porque soy son un Dog - ¡Soy Doggy y estoy corriendo!")
  }

  test("Perro constructor") {
    val perro = Perro("TestDog")
    assert(perro.nombre == "TestDog")
    assert(perro.sonido == "Guau")
  }

  test("Gato constructor") {
    val gato = Gato("TestCat")
    assert(gato.nombre == "TestCat")
    assert(gato.sonido == "Miau")
  }

  test("Pajaro constructor") {
    val pajaro = Pajaro("TestBird")
    assert(pajaro.nombre == "TestBird")
    assert(pajaro.sonido == "Pio")
  }

  test("Leon constructor") {
    val leon = new Leon("TestLion")
    assert(leon.nombre == "TestLion")
    assert(leon.sonido == "Rugido de león")
  }

  test("ReyLeon constructor") {
    assert(ReyLeon.nombre == "ReyLeon")
    assert(ReyLeon.sonido == "Rugido de rey león")
  }

}
