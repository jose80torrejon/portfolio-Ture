
package manejo_estructuras_scala

object IntroOption extends App {

  // Option es un tipo de dato que representa un valor que puede ser nulo o no nulo.
  // Option tiene dos subtipos: Some y None.
  // - Some representa un valor no nulo.
  // - None representa un valor nulo.
  // Option es útil para evitar errores como NullPointerException.
  // A este tipo de datos se le conoce como "monada" en programación funcional.
  // Una monada es un tipo de dato que representa un contexto que admite operaciones de mapeo y combinación.

  // Ejemplo de Option
  val someValue: Option[Int] = Some(5)
  val noneValue: Option[Int] = None

  // Pattern matching es una forma de manejar valores nulos en Scala muy similar a switch en Java.
  // Option se puede usar con pattern matching para manejar valores nulos.
  someValue match {
    case Some(value) => println(s"someValue - Value is $value")
    case None => println("someValue - Value is null")
  }

  noneValue match {
    case Some(value) => println(s"noneValue - Value is $value")
    case None => println("noneValue - Value is null")
  }

  // Optener el valor de un Option

  // Se puede obtener el valor de un Option usando el método get.
  // get devuelve el valor del Option si no es nulo, de lo contrario lanza una excepción.
  val value: Int = someValue.get
  println(s"value: $value") // Salida: 5
  //println(noneValue.get)

  // Se puede obtener el valor de un Option usando el método getOrElse.
  // getOrElse devuelve el valor del Option si no es nulo, de lo contrario devuelve un valor predeterminado.
  val value1: Int = someValue.getOrElse(0)
  println(s"value1: $value1") // Salida: 5
  println(None.getOrElse(-1)) // Salida: -1

  // Option también se puede usar con funciones de alto orden como map, flatMap y filter.
  // map aplica una función a un valor no nulo.
  val mappedValue: Option[Int] = someValue.map(value => value * 2)
  println(s"mappedValue: $mappedValue") // Salida: Some(10)

  // flatMap aplica una función que devuelve un Option a un valor no nulo.
  // flatMap es similar a map, pero la función que se pasa a flatMap debe devolver un Option.
  // flatMap se usa para evitar anidar Option.
  val flatMappedValue = someValue.flatMap(value => Some(value * 3))
  println(s"flatMappedValue: $flatMappedValue") // Salida: Some(15)

  // filter filtra un valor no nulo si cumple una condición.
  println(s"someValue es: $someValue")
  val filteredValue: Option[Int] = someValue.filter(value => value > 5)
  println(s"filteredValue: $filteredValue") // Salida: None


}
