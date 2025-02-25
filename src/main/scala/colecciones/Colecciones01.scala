
package colecciones

object Colecciones01 extends App {

  // ****************************************************
  // Colecciones Inmutables
  // ****************************************************

  // Ejemplo de Set
  val set = Set(1, 2, 3, 4, 5)
  println(set) // Salida: Set(1, 2, 3, 4, 5)

  // Probar agregar un elemento a set
  val newSet = set + 6
  println(newSet) // Salida: Set(1, 2, 3, 4, 5, 6)

  // Listas
  val lista = List(1, 2, 3, 4, 5)
  lista :+ 6
  println(lista) // Salida: List(1, 2, 3, 4, 5)

  // ****************************************************
  // Colecciones Mutables
  // ****************************************************

  val array = Array(1, 2, 3, 4, 5)
  println(array(0)) // Salida: 1
  array(0) = 10
  println(array(0)) // Salida: 10

  // List
  val list = List(1, 2, 3, 4, 5)

  // Vector es una colección inmutable que proporciona un rendimiento constante para la mayoría de las operaciones.
  val vector = Vector(1, 2, 3, 4, 5)
  vector :+ 6
  println(vector) // Salida: Vector(1, 2, 3, 4, 5)
  // vector(0) = 10 // Error: Vector is immutable

  // Map es una colección de pares clave-valor. Es inmutable por defecto.
  val map = Map("one" -> 1, "two" -> 2, "three" -> 3)
  map + ("four" -> 4)
  println(map) // Salida: Map(one -> 1, two -> 2, three -> 3)

  // Seq es una colección ordenada de elementos que puede ser mutable o inmutable.
  val seq = Seq(1, 2, 3, 4, 5)

}
