
package manejo_estructuras_scala

// AnimalConNombre(nombre: String)
// El sonido es abstracto ya que no se puede definir un sonido genérico para todos los animales
abstract class AnimalConNombre(val nombre: String) {
  val sonido: String

}

// correr()
trait Corredor extends AnimalConNombre {
  def correr(): String =
    s"Digo $sonido porque soy son un ${this.getClass.getSimpleName.stripSuffix("$")} - ¡Soy $nombre y estoy corriendo!"
}

trait Volador extends AnimalConNombre {
  def volar(): String =
    s"Digo $sonido porque soy son un ${this.getClass.getSimpleName.stripSuffix("$")} - ¡Soy $nombre y estoy volando!"
}

case class Perro(override val nombre: String, override val sonido: String = "Guau")
  extends AnimalConNombre(nombre) with Corredor

case class Gato(override val nombre: String, override val sonido: String = "Miau")
  extends AnimalConNombre(nombre) with Corredor

case class Pajaro(override val nombre: String, override val sonido: String = "Pio")
  extends AnimalConNombre(nombre) with Volador

class Leon(override val nombre: String, override val sonido: String = "Rugido de león")
  extends AnimalConNombre(nombre)
    with Corredor

case object ReyLeon extends Leon("ReyLeon", "Rugido de rey león")

// Ejemplo de animal que corre y vuela

case class PajaroCorredor(override val nombre: String, override val sonido: String = "Pio")
  extends AnimalConNombre(nombre)
    with Corredor
    with Volador

object EjemploSealTraitConAbstract01 extends App {
  val perro = Perro("Toby")
  println(perro.correr())

  val gato = Gato("Garfield")
  println(gato.correr())

  val pajaro = Pajaro("Piolín")
  println(pajaro.volar())

  println(ReyLeon.correr())

  val pajaroCorredor = PajaroCorredor("Piolín")
  println(pajaroCorredor.correr())
  println(pajaroCorredor.volar())

}