package primeros_pasos



object EjemplosDeFunciones extends App {

  // Una función es un bloque de código que se puede ejecutar varias veces
  // Se definen como lambda cuando no tienen nombre y se asignan a una variable
  // Se definen con la palabra reservada def cuando tienen nombre
   val a: Int = 4
  // Ejemplos avanzados de funciones
  // Funciones anónimas (Lambda)
  val sumaAnonima: (Int, Int) => Int = (a: Int, b: Int) => a + b
  val resultado1: Int = sumaAnonima(2, 3)
  println(s"[sumaAnonima] El resultado de la suma es $resultado1")
  println()


  def myFuncion1(a: Int, b: Int): Int = a + b

  // HOF
  def myFuncion2(a: Int, b: Int, function: (Int, Int) => Int): Int = function(a * b, b)

  val resultado = myFuncion2(2, 3, myFuncion1) // 9: (2*3) + 3

  println(s"[myFuncion2] El resultado de la suma es $resultado")

  // Funciones de orden superior (Higher Order Functions o HOF)
  // Función que recibe otra función como parámetro
  def operacion(a: Int, b: Int, f: (Int, Int) => Int): Int = f(a, b)

  val resultado2 = operacion(2, 3, sumaAnonima)
  println(s"[operacion] El resultado de la operación es $resultado2")
  println()

  // Funciones de orden superior (Higher Order Functions o HOF)
  // Función que devuelve otra función
  def multiplicarPor(n: Int): Int => Int = (a: Int) => a * n

  val multiplicarPor5 = multiplicarPor(5)
  val resultado3 = multiplicarPor5(3)
  println(s"[multiplicarPor5] El resultado de la multiplicación es $resultado3")
  println()

  // Funciones parcialmente aplicadas (Partial Application) o Currying
  // Son muy útiles para reutilizar funciones
  // Función que recibe dos parámetros y devuelve una función
  def sumaParcial(a: Int)(b: Int): Int = a + b

  val sumaParcialCon2 = sumaParcial(2) _
  val resultado4 = sumaParcialCon2(3)
  println(s"[sumaParcialCon2] El resultado de la suma es $resultado4")
  println()

  // Función que recibe una lista de números y un número
  // y devuelve una lista con los números multiplicados por el número
  def multiplicarLista(lista: List[Int])(n: Int): List[Int] = lista.map(_ * n)

  val listaMultiplicadaPor2 = multiplicarLista(List(1, 2, 3, 4, 5))(2)
  println(
    s"[multiplicarLista] La lista multiplicada por 2 es $listaMultiplicadaPor2",
  )
  println()

  // Funciones con múltiples listas
  // Función que recibe dos listas de números y devuelve una lista con la suma de los elementos
  def sumarListas(lista1: List[Int], lista2: List[Int]): List[Int] = {
    // zip combina dos listas en una lista de tuplas
    lista1.zip(lista2) // Devuelve una lista con la suma de los elementos de las tuplas
    // Por ejemplo, si lista1 = List(1, 2, 3) y lista2 = List(4, 5, 6)
    // zip devuelve List((1, 4), (2, 5), (3, 6))

    lista1.zip(lista2).map { case (a, b) => a + b }
  }

  val listaSumada = sumarListas(List(1, 2, 3), List(4, 5, 6))
  println(s"[sumarListas] La lista sumada es $listaSumada") // List(5, 7, 9)
  println()

  // Ejempplo de validacion de un email con currying
  def validarEmail(email: String)(validar: String => Boolean): Boolean = validar(email)

  val email = "mrenau@me.com"
  val emailValido = validarEmail(email)(email => email.contains("@"))
  println(s"[validarEmail] El email es válido: $emailValido")
  println()

  // Validar un email con una función anónima
  val emailValido2 =
    validarEmail(email)(email => email.contains("@") && email.endsWith(".com"))
  println(s"[validarEmail] El email es válido: $emailValido2")
  println()

  // Validar un email sin una función anónima, con varias funciones de validación
  def contieneArroba(email: String): Boolean = email.contains("@")

  def terminaCom(email: String): Boolean = email.endsWith(".com")

  val emailValido3 = validarEmail(email)(contieneArroba)
  println(s"[validarEmail] El email es válido: $emailValido3")
  println()
  val emailValido4 = validarEmail(email)(contieneArroba)
  println(s"[validarEmail] El email es válido: $emailValido4")
  println()
  val contieneArrobaCheck: Boolean = validarEmail(email)(contieneArroba)
  val terminaComCheck: Boolean = validarEmail(email)(terminaCom)

  val emailValido5 = contieneArrobaCheck && terminaComCheck
  println(s"[validarEmail] El email es válido: $emailValido5")
  println()

  // Ejemplo de validación de un email con funciones de validación
  def validarEmail2(email: String): Boolean = contieneArroba(email) &&
    terminaCom(email)

  val emailValido6 = validarEmail2(email)
  println(s"[validarEmail2] El email es válido: $emailValido6")
  println()

  // Ejemplo de validación de un email con funciones de validación y currying
  // tipo def validarEmail(funcion1...)(funcion2...)
  def validarEmail3(email: String)(funcion1: String => Boolean)(
    funcion2: String => Boolean,
  ): Boolean = funcion1(email) && funcion2(email)

  val emailValido7 = validarEmail3(email)(contieneArroba)(terminaCom)
  println(s"[validarEmail3] El email es válido: $emailValido7")
  println()

  // Ejemplo de validación de un email con funciones de validación y currying con varargs
  def validarEmail4(email: String)(funciones: (String => Boolean)*): Boolean =
    funciones.forall(funcion => funcion(email))

  def contienePunto(email: String): Boolean = email.contains(".")

  val emailValido8 =
    validarEmail4(email)(contieneArroba, contienePunto, terminaCom)
  println(s"[validarEmail4] El email es válido: $emailValido8")
  println()

}

// extraemos funciones de validacion para poder reutilizarlas y testearlas
object Validaciones {

  /** Valida si un email contiene una arroba
   *
   * @param email
   * String con el email a validar
   * @return
   * Boolean si el email contiene una arroba
   */
  def contieneArroba(email: String): Boolean = email.contains("@")

  /** @param email
   * String con el email a validar
   * @return
   * Boolean si el email termina con .com
   */
  def terminaCom(email: String): Boolean = email.endsWith(".com")

  /** Valida si un email contiene un punto
   *
   * @param email
   * String con el email a validar
   * @return
   * Boolean si el email contiene un punto
   */
  def contienePunto(email: String): Boolean = email.contains(".")

  def contieneEspacios(email: String): Boolean = ! email.contains(" ")

  /** Valida un email con varias funciones de validación
   *
   * @param email
   * String con el email a validar
   * @param funciones
   * Lista de funciones de validación
   * @return
   * Boolean si todas las funciones de validación son verdaderas
   */
  def validarEmail(email: String)(funciones: (String => Boolean)*): Boolean =
    // forall devuelve true si todas las funciones son verdaderas
    funciones.forall(funcion => funcion(email))

  def validarEmailConRegex(email: String): Boolean = {
    val emailRegex = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$".r
    emailRegex.findFirstIn(email).isDefined
  }
}
