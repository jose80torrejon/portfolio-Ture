
package manejo_estructuras_scala

object EjemploSealTrait01 extends App {

  sealed trait Animal {
    def sonido(): String
  }

  trait Corredor {
    def correr(): String = "¡Estoy corriendo!"
  }

  trait Volador {
    def volar(): String = "¡Estoy volando!"
  }

  case class Perro(nombre: String) extends Animal with Corredor {
    def sonido(): String = "Guau"
    override def correr(): String =
      s"¡Soy $nombre y estoy corriendo como un perro!"
  }

  case class Gato(nombre: String) extends Animal with Corredor {
    def sonido(): String = "Miau"
  }

  case class Pajaro(nombre: String) extends Animal with Volador {
    def sonido(): String = "Pio"
  }

  val perro = Perro("Toby")
  println(perro.correr())

  val gato = Gato("Garfield")
  println(gato.correr())

  val pajaro = Pajaro("Piolín")
  println(pajaro.volar())

}
