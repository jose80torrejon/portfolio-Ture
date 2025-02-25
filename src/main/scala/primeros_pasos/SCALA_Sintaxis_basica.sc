//******************************************
// VARIABLES
//******************************************
// Constante
val nombre = "Jose"

// Variable
var direccion = "Madrid"
direccion = "Alcalá"
println(direccion)
// Podemos definir el tipo de la variable de esta forma
val variable: Int = 4

//******************************************
//ESTRUCTURAS DE CONTROL
//******************************************
//IF
val numero = -3
val mensaje =
  if (numero > 0) "El número es positivo"
  else if (numero < 0) "El número es negativo"
  else "El número es cero"
println(s"mensaje = $mensaje")

// MATCH
val dia = 7
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

//******************************************
//BUCLES
//******************************************
// WHILE
var i = 0
while (i < 5) {
  println(s"i = $i")
  i += 1
}
// FOR con un rango
for (l <- 0 to 5) println(s"l = $l")

//******************************************
//FUNCIONES
//******************************************
// Funciones clásicas
def suma(a: Int, b: Int): Int = a + b
val resultado1 = suma(35, 3)
println(s"[suma]El resultado de la suma es $resultado1")

// Funciones anónimas (Lambda)
val sumaAnonima: (Int, Int) => Int = (a: Int, b: Int) => a + b
val resultado1: Int = sumaAnonima(2, 3)

// Funciones HOF = función que llama a otra a su vez
//Ejemplo1
def myFuncion1(a: Int, b: Int): Int = a + b
def myFuncion2(a: Int, b: Int, function: (Int, Int) => Int): Int = function(a * b, b)
val resultado = myFuncion2(2, 3, myFuncion1) // 9: (2*3) + 3
//Ejemplo2
def multiplicarPor(n: Int): Int => Int = (a: Int) => a * n
val multiplicarPor5 = multiplicarPor(5)
val resultado3 = multiplicarPor5(3)
println(s"[multiplicarPor5] El resultado de la multiplicación es $resultado3")

// Función PARCIAL o CURRYNG: Función que recibe dos parámetros y devuelve una función
def sumaParcial(a: Int)(b: Int): Int = a + b
val sumaParcialCon2 = sumaParcial(2) _
val resultado4 = sumaParcialCon2(3)
println(s"[sumaParcialCon2] El resultado de la suma es $resultado4")

//******************************************
// MANEJO EXCEPCIONES
//******************************************
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

//******************************************
// OPTIONS
//******************************************
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


//******************************************
// ALIAS DE TIPO
//******************************************
type PassengerName = String
type PassengerAge = Int
type ListOfPassengerInfo = List[(PassengerName, PassengerAge)] // Usamos un alias de tipo para una lista de tuplas de String e Int

val listOfPassengerInfo1: List[(String, Int)] = List(("Alice", 25), ("Bob", 30), ("Charlie", 35))
val listOfPassengerInfo2: ListOfPassengerInfo = List(("Alice", 25), ("Bob", 30), ("Charlie", 35))
println(listOfPassengerInfo1)
println(listOfPassengerInfo2)



