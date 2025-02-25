
package packageobjects.domain

object IntroTypeAlias {
  type TrainId = Int
  type TrainName = String

  type PassengerName = String
  type PassengerAge = Int
  type ListOfPassengerInfo = List[(PassengerName, PassengerAge)] // Usamos un alias de tipo para una lista de tuplas de String e Int

  val listOfPassengerInfo1: List[(String, Int)] = List(("Alice", 25), ("Bob", 30), ("Charlie", 35))
  val listOfPassengerInfo2: ListOfPassengerInfo = List(("Alice", 25), ("Bob", 30), ("Charlie", 35))

  // Definimos un alias de tipo para una función que toma un entero y devuelve un entero
  // Esto es equivalente a: type def intToInt(Int): Int = ???
  type IntToInt = Int => Int

  // Definimos tres funciones simples
  val addOne: IntToInt = (x: Int) => x + 1
  val double: IntToInt = (x: Int) => x * 2
  val subtractOne: IntToInt = (x: Int) => x - 1

  // La composición usando `andThen` permite una fácil lectura y comprensión
  val compute: IntToInt = addOne andThen double andThen subtractOne

}

object IntroTypeAliasApp extends App {

  import IntroTypeAlias._
  // Un alias de tipo es una forma de dar un nombre alternativo a un tipo existente.
  // Los alias de tipo son útiles para hacer que el código sea más legible y fácil de entender.
  // También se pueden usar para abstraer tipos complejos y hacer que el código sea más genérico.
  // Veremos más adelante las funciones implícitas y cómo se pueden usar con los alias de tipo,
  // ya que solo puede haber una función implícita por tipo.


  println(s"listOfPassengerInfo1: $listOfPassengerInfo1") // Salida: List((Alice,25), (Bob,30), (Charlie,35)
  println(s"listOfPassengerInfo2: $listOfPassengerInfo2") // Salida: List((Alice,25), (Bob,30), (Charlie,35)


  println(compute(5)) // Salida: 11
  println(compute(6)) // Salida: 13
  println(compute(7)) // Salida: 15
  println(compute(0)) // Salida:  1
  println(compute(-1)) // Salida: -1

}
