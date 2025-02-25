
package colecciones

object EjemploSetsApp extends App {

  // Immutable Set
  val immSet = Set(1, 2, 3, 4, 5)
  println(immSet) // Salida: Set(1, 2, 3, 4, 5)

  // Probar agregar un elemento a immSet
  val newImmSet = immSet + 6
  println(newImmSet) // Salida: Set(1, 2, 3, 4, 5, 6)

  // Probar quitar un elemento
  val removedImmSet = newImmSet - 1
  println(removedImmSet) // Salida: Set(2, 3, 4, 5, 6)

  // Colecciones MUTABLES

  // Para Mutable Set, recuerda que debes importarlo explícitamente.
  import scala.collection.mutable
  val mutSet = mutable.Set(1, 2, 3, 4, 5)
  println(mutSet) // Salida: Set(5, 1, 2, 3, 4)

  // Agregar elemento a mutSet
  mutSet += 6
  println(mutSet) // Salida: Set(5, 1, 6, 2, 3, 4)

  // Remover elemento de mutSet
  mutSet -= 1
  println(mutSet) // Salida: Set(5, 6, 2, 3, 4)

  print(mutSet(2))
}
