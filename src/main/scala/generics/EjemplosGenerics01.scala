
package generics

object EjemplosGenerics01 extends App {

  class Caja[T](val valor: T) {
    def getValor: T = valor
  }

  val caja: Caja[String] = new Caja("Hola")
  println(caja.getValor) // Salida: Hola


  def middle[A](input: Seq[A]): A = input(input.size / 2)

  // Ejemplo de uso de middle
  val lista = List(1, 2, 3, 4, 5, 6)
  println(middle(lista)) // Salida: 4

  // type String = Seq[Char]
  val saludo: String = "Hola Mundo"

  println(middle(saludo)) // Salida: M

  val valor = 5
  //println(middle(valor)) // Salida: 5

  trait Desenvolvedor[T] {
    def getValor: T
  }

  case class NestedEvent(
                          name: String,
                          date: String,
                          eventSource: String,
                          mainEvent: Option[NestedEvent],
                        )

  class ExtractorEventos(valor: NestedEvent)
    extends Desenvolvedor[Option[NestedEvent]] {
    // Usamos fold para devolver el valor de mainEvent si existe, si no devolvemos el valor de valor
    // Esto es útil para evitar el uso de nulls
    def getValor: Option[NestedEvent] = this.valor.mainEvent
      .fold(Option(this.valor))(mainEvent => Option(mainEvent))
  }

  val eventoOriginal =
    NestedEvent("Evento Original", "2021-01-01", "Evento", None)
  val extractorEventos = new ExtractorEventos(eventoOriginal)
  println(extractorEventos.getValor) // Salida: NestedEvent("Evento Original","2021-01-01","Evento",None)

  val eventoConMainEvent = NestedEvent(
    "Evento Original",
    "2021-01-01",
    "Evento",
    Option(NestedEvent("Evento Anidado", "2021-01-01", "Evento", None)),
  )
  val extractorEventosConMainEvent = new ExtractorEventos(eventoConMainEvent)
  println(extractorEventosConMainEvent.getValor) // Salida: NestedEvent("Evento Anidado","2021-01-01","Evento",None)

  // Ejemplo con dos tipos de datos genéricos
  class Caja2[T, U](val valor1: T, val valor2: U) {
    def getValor1: T = valor1

    def getValor2: U = valor2
  }

  val caja2: Caja2[String, Int] = new Caja2("Hola", 1)
  println(caja2.getValor1) // Salida: Hola
  println(caja2.getValor2) // Salida: 1

  // Ejemplo con dos tipos de datos genéricos y un método que devuelve un valor de tipo U
  class Caja3[T, U](val valor1: T, val valor2: U) {
    def getValor1: T = valor1

    def getValor2: U = valor2

    def getValor2AsString: String = valor2.toString
  }

  val caja3: Caja3[String, Int] = new Caja3("Hola", 1)
  println(caja3.getValor1) // Salida: Hola
  println(caja3.getValor2) // Salida: 1
  println(caja3.getValor2AsString) // Salida: 1

}
