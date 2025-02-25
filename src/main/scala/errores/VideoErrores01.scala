
package errores

object VideoErrores01 extends App {

  // Esta construcción es usada para manejar excepciones.
  try {
    val result = 10 / 0 // Aquí se generará una excepción
  } catch {
    case npe: NullPointerException => println(
        s"Se ha producido una excepción de tipo NullPointerException: $npe",
      )
    case ex: Exception => println(s"Se ha producido una excepción: $ex")

  } finally
    println("Este mensaje se mostrará siempre, independientemente de si ocurrió una excepción o no.")

  // Es un tipo de datos que puede tener dos valores posibles: "Some" o "None".
  def divideOpt(a: Int, b: Int): Option[Int] = if (b == 0) None else Some(a / b)

  val result1 = divideOpt(10, 2)

  println(s"result1 es: ${result1.get}")
  // resultado: Some(5)
  val result2 = divideOpt(10, 0)
  println(s"result2 es: $result2")
  // resultado: None En este ejemplo, la función "divide" devuelve un Option[Int], el cual devuelve "Some" si b es diferente de cero y "None" si es cero.

  // Esta es una estructura de datos que puede contener un valor de dos tipos posibles, una "izquierda" o una "derecha".
  def divideEither(a: Int, b: Int): Either[String, Int] =
    if (b == 0) Left("Error: No es posible dividir por cero.") else Right(a / b)

  val result3 = divideEither(10, 2)
  println(s"result3 es: $result3")
  // resultado: Right(5)

  val result4 = divideEither(10, 0)
  println(s"result4 es: $result4")
  // resultado: Left("Error: No es posible dividir por cero.")
  // En este ejemplo, la función "divide" devuelve un Either[String, Int], donde el primer valor (izquierdo) es un String que indica un error y el segundo valor (derecho) es el resultado de la operación.

}
