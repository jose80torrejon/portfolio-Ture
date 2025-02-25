

import scala.annotation.tailrec

object EjemplosBasicos04 extends App {

  // Ejemplos de cosas que hacemos con var y que podemos hacer con val
  // Ejemplo 1: Cambiar el valor de una variable
  var numero = 5
  numero = 10
  println(s"Ejemplo 1: numero = $numero")
  println()

  // Ejemplo 2: Cambiar el valor de un atributo de un objeto
  class Persona(var nombre: String)

  val persona = new Persona("Juan")
  persona.nombre = "Pedro"
  println(s"Ejemplo 2: persona.nombre = ${persona.nombre}")
  println()

  // Ejemplo 3: Cambiar el valor de un atributo de un objeto inmutable
  class Persona2(val nombre: String)

  val persona2 = new Persona2("Juan")
  // persona2.nombre = "Pedro" // Error: Reassignment to val
  println()

  // Ejemplo 4: Cambiar el valor de un atributo de un objeto inmutable con un nuevo objeto
  class Persona3(val nombre: String)

  val persona3 = new Persona3("Juan")
  val persona4 = new Persona3("Pedro")
  println(s"Ejemplo 4: persona3.nombre = ${persona3.nombre}")
  println(s"Ejemplo 4: persona4.nombre = ${persona4.nombre}")
  println()

  // Ejemplo 5: Cambiar el valor de un atributo de un objeto inmutable con un nuevo objeto y copiar los valores
  class Persona5(val nombre: String, val edad: Int)

  val persona5 = new Persona5("Juan", 30)
  val persona6 = new Persona5("Pedro", persona5.edad)
  println(s"Ejemplo 5: persona5.nombre = ${persona5.nombre}, persona5.edad = ${persona5.edad}")
  println(s"Ejemplo 5: persona6.nombre = ${persona6.nombre}, persona6.edad = ${persona6.edad}")
  println()

  // Ejemplo 6: Cambiar el valor de un atributo de un objeto inmutable con un nuevo objeto y copiar los valores con copy
  // case class nos permite copiar objetos con el método copy y cambiar los valores de los atributos entre otras cosas
  case class Persona6(nombre: String, edad: Int)

  val persona7 = Persona6("Juan", 30)
  val persona8 = persona7.copy(nombre = "Pedro")
  println(s"Ejemplo 6: persona7.nombre = ${persona7.nombre}, persona7.edad = ${persona7.edad}")
  println(s"Ejemplo 6: persona8.nombre = ${persona8.nombre}, persona8.edad = ${persona8.edad}")
  println()

  // Ejemplo 7: Cambiar el valor de un atributo de un objeto inmutable con un nuevo objeto y copiar los valores con copy y cambiar varios atributos
  case class Persona7(nombre: String, edad: Int)

  val persona9 = Persona7("Juan", 30)
  val persona10 = persona9.copy(nombre = "Pedro", edad = 40)
  println(s"Ejemplo 7: persona9.nombre = ${persona9.nombre}, persona9.edad = ${persona9.edad}")
  println(s"Ejemplo 7: persona10.nombre = ${persona10.nombre}, persona10.edad = ${persona10.edad}")
  println()

  // Ejemplo 8: Cambiar el valor de un atributo de un objeto inmutable con un nuevo objeto y copiar los valores con copy y cambiar varios atributos con el mismo valor
  case class Persona8(nombre: String, edad: Int)

  val persona11 = Persona8("Juan", 30)
  val persona12 = persona11.copy(nombre = "Pedro", edad = persona11.edad)
  println(s"Ejemplo 8: persona11.nombre = ${persona11.nombre}, persona11.edad = ${persona11.edad}")
  println(s"Ejemplo 8: persona12.nombre = ${persona12.nombre}, persona12.edad = ${persona12.edad}")
  println()

  // Ejemplo de funciones con var que podemos hacer con val
  // Ejemplo 1: Función que suma dos números
  def suma(a: Int, b: Int): Int = {
    var resultado = 0
    if (a > 0 && b > 0) resultado = a + b
    resultado
  }

  val resultado1 = suma(2, 3)
  println(s"Ejemplo 1: El resultado de la suma es $resultado1")
  println()

  def sumaConValorPorDefecto(a: Int, b: Int): Int =
    // sin var ni val
    if (a > 0 && b > 0) a + b else 0

  val resultado2 = sumaConValorPorDefecto(5, 3)
  println(s"Ejemplo 2: El resultado de la suma es $resultado2")
  println()

  // Ejemplo 2: Función que suma una lista de números
  def sumaLista(lista: List[Int]): Int = {
    var resultado = 0
    for (elemento <- lista) resultado += elemento
    resultado
  }

  val resultado3 = sumaLista(List(1, 2, 3, 4, 5))
  println(s"Ejemplo 2: El resultado de la suma es $resultado3")
  println()

  // Ejemplo 3: Función que suma una lista de números sin var
  def sumaListaAvanzada(lista: List[Int]): Int = {
    @tailrec
    def sumaRecursiva(lista: List[Int], acumulador: Int): Int = lista match {
      case Nil => acumulador
      case cabeza :: cola => sumaRecursiva(cola, acumulador + cabeza)
    }

    sumaRecursiva(lista, 0)
  }

  val resultado4 = sumaListaAvanzada(List(1, 2, 3, 4, 5))
  println(s"Ejemplo 3: El resultado de la suma es $resultado4")
  println()

  // Ejemplo 4: Función que suma una lista de números con foldLeft
  def sumaListaConFoldLeft(lista: List[Int]): Int =
    // Solo es un ejemplo, no es necesario usar foldLeft para sumar una lista de números list.sum
    // Esto es equivalente a lista.foldLeft(0){ (acumulador, elemento) => acumulador + elemento}
    lista.foldLeft(0)(_ + _)

  val resultado5 = sumaListaConFoldLeft(List(1, 2, 3, 4, 5))
  println(s"Ejemplo 4: El resultado de la suma es $resultado5")
  println()
}
