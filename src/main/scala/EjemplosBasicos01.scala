

import scala.Console.BLACK
import scala.Console.GREEN
import scala.Console.RED
import scala.Console.WHITE
import scala.Console.YELLOW
// import scala.Console._
// En Scala3: scala.Console.*

object EjemplosBasicos01 extends App {
  /*def main(args: Array[String]): Unit = {
    println("Hola mundo")
  }*/

  val mensaje = "Hola mundo"
  // mensaje = "Hola mundo 2"

  var mensajeMutable = "Hola mundo"
  mensajeMutable = "Hola mundo 2"
  println(mensajeMutable)

  // Ejemplo de una función que suma dos números
  def suma(a: Int, b: Int): Int = a + b
  val resultado1 = suma(2, 3)
  println(s"[suma] El resultado de la suma es $resultado1")

  def sumaConImpresion(a: Int, b: Int): Int = {
    val resultado = suma(a, b)
    println(s"La suma de $a y $b es $resultado")
    resultado
  }
  val resultado2 = sumaConImpresion(2, 3)
  println(s"[sumaConImpresion] El resultado de la suma es $resultado2")

  def sumaConValorPorDefecto(a: Int, b: Int = 0): Int = a + b

  // Especificamos el tipo de la variable para
  val resultado3: Int = sumaConValorPorDefecto(5)
  println(s"[sumaConValorPorDefecto] El resultado de la suma es $resultado3")

  // Ejemplo de una función que suma una lista de números
  def sumaLista(lista: List[Int]): Int = lista.sum
  val resultado4 = sumaLista(List(1, 2, 3, 4, 5))
  println(s"[sumaLista] El resultado de la suma es $resultado4")

  // Función que no devuelve nada
  def imprimirMensaje(mensaje: String): Unit = println(mensaje)
  imprimirMensaje("Hola mundo")

  // Imprimir mensaje con colores usando IF
  def imprimirMensajeConColorIf(mensaje: String, color: String): Unit = {
    val colorSeleccionado = if (color == "rojo") RED else BLACK
    println(s"$colorSeleccionado$mensaje\u001b[0m")
  }
  imprimirMensajeConColorIf("Hola mundo en rojo", "rojo")

  def imprimirMensajeConColorIfElse(mensaje: String, color: String): Unit = {
    val colorSeleccionado =
      if (color == "rojo") RED else if (color == "verde") GREEN else BLACK
    println(s"$colorSeleccionado$mensaje\u001b[0m")
    println()
  }
  imprimirMensajeConColorIfElse("Hola mundo en verde", "verde")
  imprimirMensajeConColorIfElse("Hola mundo en azul", "azul")
  // Imprimir mensaje con colores
  def imprimirMensajeConColor(mensaje: String, color: String): Unit = {

    val colores =
      Map("rojo" -> RED, "verde" -> GREEN, "amarillo" -> YELLOW, "azul" -> WHITE)
    val colorSeleccionado = colores.getOrElse(color, BLACK)
    println(s"$colorSeleccionado$mensaje\u001b[0m")

  }
  var color = "rojo"
  imprimirMensajeConColor(s"Hola mundo en $color", color)
  color = "azul clarito"
  imprimirMensajeConColor(s"Hola mundo en $color", color)

  // Imprimir mensaje con colores usando pattern matching
  def imprimirMensajeConColorPatternMatching(
      mensaje: String,
      color: String,
  ): Unit = {

    val colorSeleccionado = color match {
      case "rojo" => RED
      case "verde" => GREEN
      case "amarillo" => YELLOW
      case "azul" => WHITE
      case _ => BLACK
    }
    println(s"$colorSeleccionado$mensaje\u001b[0m")
  }

  def funcionSinImplementar(): Unit = ???
  // funcionSinImplementar()

}
