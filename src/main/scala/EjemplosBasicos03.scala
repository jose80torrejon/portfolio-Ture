

import scala.Console.GREEN
import scala.Console.RED
import scala.Console.RESET

object EjemplosBasicos03 extends App {
  // TRY CATCH
  println("TRY CATCH")
  try {
    val resultado = 10 / 0
    println(s"resultado = $resultado")
  } catch { case e: ArithmeticException => println("Error: División por cero") }
  println()

  // TRY CATCH con múltiples excepciones
  println("TRY CATCH con múltiples excepciones")
  try {
    val resultado = 10 / 0
    println(s"resultado = $resultado")
  } catch {
    case e: ArithmeticException => println("Error: División por cero")
    case e: Exception => println("Error: Excepción genérica")
  }
  println()

  // TRY CATCH con múltiples excepciones y FINALLY
  println("TRY CATCH con múltiples excepciones y FINALLY")
  try {
    val resultado = 10 / 0
    println(s"resultado = $resultado")
  } catch {
    case e: ArithmeticException => println("Error: División por cero")
    case e: Exception => println("Error: Excepción genérica")
  } finally println("FINALLY")
  println()

  // Option
  println("Option")

  def dividir(a: Int, b: Int): Option[Int] = if (b == 0) None else Some(a / b)

  val resultado8 = dividir(10, 2)
  println(s"resultado8 = $resultado8")
  val resultado9 = dividir(10, 0)
  println(s"resultado9 = $resultado9")
  println()

  // Option con MATCH
  println("Option con MATCH")
  val resultado10 = dividir(10, 2) match {
    case Some(valor) => s"El resultado de la división es $valor"
    case None => "Error: División por cero"
  }
  println(s"resultado10 = $resultado10")
  println()

  // Option con MATCH y guardas
  println("Option con MATCH y guardas")
  val resultado11 = dividir(10, 2) match {
    case Some(valor) if valor > 0 => s"El resultado de la división es $valor"
    case Some(valor) if valor < 0 => "Error: Resultado negativo"
    case None => "Error: División por cero"
    case _ => "Error: Excepción genérica"
  }
  println(s"resultado11 = $resultado11")
  println()

  // Try
  // Try es una estructura de control que permite manejar excepciones de una forma más funcional que el tradicional try-catch.
  // Try es un contenedor que puede contener un valor o una excepción.
  // Try tiene dos subtipos: Success y Failure.
  // Success contiene un valor.
  // Failure contiene una excepción.
  // Try es una mónada, lo que significa que se puede encadenar con otras mónadas como Option, Future, etc.
  //
  println("Try")

  import scala.util.Failure
  import scala.util.Success
  import scala.util.Try

  def dividirTry(a: Int, b: Int): Try[Int] = Try(a / b)

  val resultado12 = dividirTry(10, 2)
  println(s"resultado12 = $resultado12")
  val resultado13 = dividirTry(10, 0)
  println(s"resultado13 = $resultado13")
  println()

  // Try con MATCH
  println("Try con MATCH")
  val resultado14 = dividirTry(10, 2) match {
    case Success(valor) => s"El resultado de la división es $valor"
    case Failure(excepcion) => s"Error: ${excepcion.getMessage}"
  }
  println(s"resultado14 = $resultado14")
  println()

  // Try con MATCH y guardas
  println("Try con MATCH y guardas")
  val resultado15 = dividirTry(10, 2) match {
    case Success(valor) if valor > 0 => s"El resultado de la división es $valor"
    case Success(valor) if valor < 0 => "Error: Resultado negativo"
    case Failure(excepcion) => s"Error: ${excepcion.getMessage}"
    case _ => "Error: Excepción genérica"
  }
  println(s"resultado15 = $resultado15")
  println()

  // Try con FOR
  println("Try con FOR")
  val resultado16 = for {
    a <- dividirTry(10, 2)
    b <- dividirTry(5, 0)
    c <- dividirTry(20, 4)
  } yield a + b + c
  println(s"resultado16 = $resultado16")
  if (resultado16.isSuccess) println(
    GREEN + s"-->> El resultado de la suma es ${resultado16.get}" + RESET,
  )
  else println(RED + s"-->> Error: ${resultado16.failed.get.getMessage}" + RESET)
  println()

  // Try con FOR y guardas
  println("Try con FOR y guardas")
  val resultado17 = for {
    a <- dividirTry(10, 2) if a > 0
    b <- dividirTry(5, 10) if b > 0
    c <- dividirTry(20, 4) if c > 0
  } yield a + b + c

  println(s"resultado17 = $resultado17")
  if (resultado17.isSuccess) println(
    GREEN + s"-->> El resultado de la suma es ${resultado17.get}" + RESET,
  )
  else println(RED + s"-->> Error: ${resultado17.failed.get.getMessage}" + RESET)
  println()

  println("Try con FOR y guardas")

  val defaultValue = 0

  val resultado18 = for {
    a <- dividirTry(10, 2).toOption.filter(_ > 0).orElse(Some(defaultValue))
    b <- dividirTry(5, 10).toOption.filter(_ > 0).orElse(Some(defaultValue))
    c <- dividirTry(20, 4).toOption.filter(_ > 0).orElse(Some(defaultValue))
  } yield a + b + c

  println(s"resultado18 = $resultado18")

  // isDefined es un método que devuelve true si el Try contiene un valor y false si contiene una excepción.
  if (resultado18.isDefined)
    println(s"-->> El resultado de la suma es ${resultado18.get}")
  else
    println("-->> Error: No value could be computed as all divisors are zero or the division resulted in zero.")

  println()

}
