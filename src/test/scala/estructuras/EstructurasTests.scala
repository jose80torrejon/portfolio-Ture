package eoi.de.examples
package estructuras

import org.scalatest.funsuite.AnyFunSuite

class EstructurasTests extends AnyFunSuite {
  test("Perro should run correctly") {
    val perro = Perro("Toby")
    assert(perro.correr() == "Digo Guau porque soy son un Perro - ¡Soy Toby y estoy corriendo!")
  }

  test("Gato should run correctly") {
    val gato = Gato("Garfield")
    assert(gato.correr() == "Digo Miau porque soy son un Gato - ¡Soy Garfield y estoy corriendo!")
  }

  test("Pajaro should fly correctly") {
    val pajaro = Pajaro("Piolín")
    assert(pajaro.volar() == "Digo Pio porque soy son un Pajaro - ¡Soy Piolín y estoy volando!")
  }

  test("ReyLeon should run correctly") {
    assert(ReyLeon.correr() == "Digo Rugido de rey león porque soy son un ReyLeon - ¡Soy ReyLeon y estoy corriendo!")
  }

  test("PajaroCorredor should run and fly correctly") {
    val pajaroCorredor = PajaroCorredor("Piolín")
    assert(pajaroCorredor.correr() == "Digo Pio porque soy son un PajaroCorredor - ¡Soy Piolín y estoy corriendo!")
    assert(pajaroCorredor.volar() == "Digo Pio porque soy son un PajaroCorredor - ¡Soy Piolín y estoy volando!")
  }
}

