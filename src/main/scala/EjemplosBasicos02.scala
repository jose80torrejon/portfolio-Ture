

object EjemplosBasicos02 extends App {

  // Ejemplos de estructuras de control básicas y avanzadas
  // Estructuras de control básicas
  // IF
  val numero = 5
  if (numero > 0) Console.println("El número es positivo")
  else println("El número es negativo")

  // IF en una sola línea
  val mensaje =
    if (numero > 0) "El número es positivo" else "El número es negativo"
  println(s"mensaje = $mensaje")

  // IF con múltiples condiciones
  val mensaje2 =
    if (numero > 0) "El número es positivo"
    else if (numero < 0) "El número es negativo"
    else "El número es cero"
  println(s"mensaje2 = $mensaje2")

  // WHILE
  var i = 0
  while (i < 5) {
    println(s"i = $i")
    i += 1
  }

  // DO WHILE
  var j = 0
  do {
    println(s"j = $j")
    j += 1
  } while (j < 5)

  // Lo mismo sin var
  for (j <- 0 until 5) {
    println(s"j = $j")
  }

  // FOR
  for (k <- 0 until 5) println(s"k = $k")

  // FOR con un rango
  for (l <- 0 to 5) println(s"l = $l")

  // FOR con un rango y un paso
  for (m <- 0 to 10 by 2) println(s"m = $m")

  // FOR con un rango y un paso inverso
  for (n <- 10 to 0 by -2) println(s"n = $n")

  // FOR con condición de salida
  def break(): Unit = for (o <- 0 to 10) {
    if (o == 5) {
      println("Salimos del bucle")
      return // break
    }
    println(s"o = $o")
  }

  break()

  // FOR anidado
  println("FOR anidado")
  for (p <- 0 to 2) for (q <- 0 to 2) println(s"p = $p, q = $q")
  println()

  // FOR con guardas
  println("FOR con guardas")
  for (r <- 0 to 10 if r % 2 == 0) println(s"r = $r")
  println()

  // FOR con guardas y múltiples condiciones
  println("FOR con guardas y múltiples condiciones")
  for (
    s <- 0 to 10 if s % 2 == 0
    if s > 5
  ) println(s"s = $s")
  println()

  // FOR comprehension
  println("FOR comprehension")
  val lista = for (t <- 0 to 10) yield t * 2
  println(s"lista = $lista")
  println()

  // FOR comprehension con guardas
  println("FOR comprehension con guardas")
  val lista2 = for (u <- 0 to 10 if u % 2 == 0) yield u
  println(s"lista2 = $lista2")
  println()

  // FOR comprehension con guardas y múltiples condiciones
  println("FOR comprehension con guardas y múltiples condiciones")
  val lista3 = for (
    v <- 0 to 10 if v % 2 == 0
    if v > 5
  ) yield v
  println(s"lista3 = $lista3")
  println()

  // FOR comprehension anidado
  // Devuelve un vector de tuplas porque se está utilizando yield
  // Si no se utiliza yield, se obtiene un vector de vectores
  println("FOR comprehension anidado")
  val lista4 = for {
    w <- 0 to 2
    x <- 10 to 12
  } yield (w, x)
  println(s"lista4 = $lista4")
  println()

  // FOR comprehension anidado que devuelve un vector de vectores
  println("FOR comprehension anidado que devuelve un vector de vectores")
  val lista5 = for { w <- 0 to 2 } yield for { x <- 10 to 12 } yield (w, x)
  println(s"lista5 = $lista5")
  println()

  // Mismo ejemplo anterior pero con FOR
  println("FOR anidado")
  for (w <- 0 to 2) for (x <- 10 to 12) println(s"w = $w, x = $x")
  println()

// Estructuras de control avanzadas
  // MATCH
  val dia = 1
  val nombreDia = dia match {
    case 1 => "Lunes"
    case 2 => "Martes"
    case 3 => "Miércoles"
    case 4 => "Jueves"
    case 5 => "Viernes"
    case 6 => "Sábado"
    case 7 => "Domingo"
    case _ => "Día no válido"
  }
  println(s"nombreDia = $nombreDia")

  // MATCH con guardas
  val numero2 = 5
  val mensaje3 = numero2 match {
    case n if n > 0 => "El número es positivo"
    case n if n < 0 => "El número es negativo"
    case _ => "El número es cero"
  }
  println(s"mensaje3 = $mensaje3")

  // MATCH con múltiples condiciones
  val mensaje4 = numero2 match {
    case n if n > 0 => "El número es positivo"
    case n if n < 0 => "El número es negativo"
    case n if n == 0 => "El número es cero"
  }
  println(s"mensaje4 = $mensaje4")

  // MATCH con múltiples condiciones y guardas
  val mensaje5 = numero2 match {
    case n if n > 0 => "El número es positivo"
    case n if n < 0 => "El número es negativo"
    case n if n == 0 => "El número es cero"
    case _ => "Número no válido"
  }
  println(s"mensaje5 = $mensaje5")

  // MATCH con múltiples condiciones y guardas
  println("MATCH 6 con múltiples condiciones y guardas")

  val mensaje6 = numero2 match {
    case n if n > 0 => "El número es positivo"
    case n if n < 0 => "El número es negativo"
    case n if n == 0 => "El número es cero"
    case _ => "Número no válido"
  }
  println(s"mensaje6 = $mensaje6")

  // MATCH con múltiples condiciones y guardas
  println("MATCH 7 con múltiples condiciones y guardas")
  val mensaje7 = numero2 match {
    case n if n > 0 => "El número es positivo"
    case n if n < 0 => "El número es negativo"
    case n if n == 0 => "El número es cero"
    case _ => "Número no válido"
  }
  println(s"mensaje7 = $mensaje7")

}
