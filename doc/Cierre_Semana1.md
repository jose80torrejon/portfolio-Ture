

# Gestión de errores en Scala

A modo de resumen de las diferentes formas de gestionar errores en Scala, se puede decir que existen tres formas principales de gestionar errores en Scala:

## Errores que no se pueden prever

En muchos casos, los errores no se pueden prever, por lo que se pueden utilizar las siguientes formas de gestionarlos:

### Excepciones

- Se utilizan para manejar errores que no se pueden prever. 
- Se lanzan con la palabra clave `throw` y se capturan con la palabra clave `try-catch-finally`.
  - En el bloque `try` se coloca el código que puede lanzar una excepción.
  - En el bloque `catch` se coloca el código que maneja la excepción o excepciones.
  - En el bloque `finally` se coloca el código que se ejecuta siempre, independientemente de si se lanza una excepción o no.
- Se pueden lanzar excepciones de cualquier tipo, pero es recomendable lanzar excepciones de tipo `Throwable`, ya que es la clase base de todas las excepciones.

Un típico ejemplo de este tipo de excepciones es cuando usamos bibliotecas de Java que lanzan excepciones, como un driver de base de datos.

```scala
import java.io.IOException

def readFromFile(fileName: String): String = {
  try {
    val source = scala.io.Source.fromFile(fileName)
    val lines = try source.getLines.mkString("\n") finally source.close()
    lines
  } catch {
    case e: IOException => s"Error reading file: ${e.getMessage}"
  }
}
```

### Try

- Se utilizan para manejar errores que no se pueden prever.
- Se utilizan para representar un valor que puede ser nulo o un error.
  - Si el valor es nulo, se devuelve un `Failure` con un `Throwable`.
  - Si el valor no es nulo, se devuelve un `Success` con el valor.

```scala
import scala.util.Try

def divide(a: Int, b: Int): Try[Int] = Try(a / b)

val result = divide(10, 0)

result match {
  case scala.util.Success(value) => println(s"Result: $value")
  case scala.util.Failure(exception) => println(s"Error: ${exception.getMessage}")
}
```


## Errores que se pueden prever

Cuando se pueden prever los errores, se pueden utilizar las siguientes formas de gestionarlos:


- **Option**: Se utilizan para manejar errores que se pueden prever. Se utilizan para representar un valor que puede ser nulo.
- **Either**: Se utilizan para manejar errores que se pueden prever. Se utilizan para representar un valor que puede ser nulo o un error, pero con más información que `Try`.
- **Future**: Se utilizan para manejar errores que se pueden prever. Se utilizan para representar un valor que puede ser nulo o un error, pero con la posibilidad de ser asincrónico.
- **Validated**: Se utilizan para manejar errores que se pueden prever. Se utilizan para representar un valor que puede ser nulo o un error, pero con la posibilidad de acumular errores.
- **IO**: Se utilizan para manejar errores que se pueden prever. Se utilizan para representar un valor que puede ser nulo o un error, pero con la posibilidad de ser asincrónico y con la posibilidad de manejar efectos secundarios.
- **Monad Transformers**: Se utilizan para manejar errores que se pueden prever. Se utilizan para representar un valor que puede ser nulo o un error, pero con la posibilidad de combinar varios tipos de datos.


## Conclusiones
- Las excepciones se utilizan para manejar errores que no se pueden prever.
- `Try`, `Option`, `Either`, `Future`, `Validated`, `IO` y `Monad Transformers` se utilizan para manejar errores que se pueden prever.
- Cada una de estas formas de gestionar errores tiene sus propias características y se pueden utilizar en diferentes situaciones.
- Es importante que elijas una forma de gstionar los errores que:
- Sea sencilla de entender. 
  - Por ejemplo, no utilices `Monad Transformers` si no sabes lo que son)
  - Por ejemplo, no utilices `IO` si no necesitas manejar efectos secundarios.
  - Por ejemplo, no utilices `Validated` si necesitas acumular errores.
  - Por ejemplo, no utilices `Future` si no necesitas manejar asincronía.
  - Por ejemplo, no utilices `Either` si no necesitas más información que `Try`.
  - Por ejemplo, no utilices `Option` si no necesitas manejar valores nulos.
  - Por ejemplo, no utilices excepciones si no necesitas manejar errores que no se pueden prever.
  - Ejemplo de una estructura compleja y que no se entiende: `Either[Throwable, Option[Future[Validated[IO, Int]]]]`
- Sea sencilla de utilizar.
- Sea sencilla de depurar.
- Sea segura, y por ejemplo, no permita errores de nulos o devuelva valores nulos.
- Sea eficiente, y por ejemplo, no genere excepciones innecesarias.
- Sea informativa, y por ejemplo, devuelva mensajes de error claros y concisos.

En términos generales, la gestión de errores en Scala se puede abordar de varias maneras, dependiendo de la naturaleza del error y de las necesidades específicas de tu aplicación. Como ingeniero de datos, es probable que te encuentres con situaciones en las que necesites validar datos y realizar transformaciones concatenadas. 

Te dejo algunas estrategias que yo suelo considerar:  
- Uso de Option: Option es una forma de manejar errores que se pueden prever y que se utilizan para representar un valor que puede ser nulo. Si una función puede no devolver un valor, puedes usar Option para representar esto. Option tiene dos subtipos: Some y None.  
- Uso de Either: Either es otra forma de manejar errores que se pueden prever. Se utiliza para representar un valor que puede ser un resultado correcto o un error. Either tiene dos subtipos: Left y Right. Por convención, Left se utiliza para errores y Right para el resultado correcto.  
- Uso de Try: Try es una forma de manejar errores que no se pueden prever. Se utiliza para representar un valor que puede ser un resultado correcto o una excepción. Try tiene dos subtipos: Success y Failure.  
- Uso de Validated: Validated es una forma de manejar errores que se pueden prever. Se utiliza para representar un valor que puede ser un resultado correcto o un error, pero con la posibilidad de acumular errores. Esto puede ser útil cuando necesitas realizar varias validaciones y quieres recoger todos los errores, no solo el primero que encuentres.  
- Manejo de excepciones: Aunque el manejo de excepciones en Scala es similar al de Java, se considera una mala práctica usarlo para el control de flujo. Deberías usarlo solo para errores que no puedes prever o manejar.  

`En nuestro caso como Ingenieros de Datos, es probable que Option, Either y Validated sean las más útiles, ya que permiten manejar errores de una manera más funcional y segura. Además, Validated es especialmente útil para la validación de datos, ya que permite acumular errores.`

# Estructuras de clases en Scala

## Clases y objetos en Scala
- class: Se utiliza para definir una clase en Scala y se puede instanciar para crear objetos.
- case class: Se utiliza para definir una clase en Scala y se puede instanciar para crear objetos. Además, se utiliza para crear clases inmutables y para proporcionar una implementación predeterminada de los métodos `equals`, `hashCode` y `toString`.
- abstract class: Se utiliza para definir una clase abstracta en Scala y no se puede instanciar para crear objetos.
- object: Se utiliza para definir un objeto en Scala y se puede utilizar para crear objetos sin necesidad de instanciar una clase.
- case object: Se utiliza para definir un objeto en Scala y se puede utilizar para crear objetos sin necesidad de instanciar una clase. Además, se utiliza para crear objetos inmutables y para proporcionar una implementación predeterminada de los métodos `equals`, `hashCode` y `toString`.

## Traits en Scala
- trait: Se utiliza para definir un trait en Scala y se puede mezclar con una clase o un objeto para proporcionar funcionalidad adicional.
- sealed trait: Se utiliza para definir un trait sellado en Scala y se puede mezclar con una clase o un objeto para proporcionar funcionalidad adicional. Además, se utiliza para restringir la herencia de un trait a un conjunto específico de clases u objetos.
- abstract trait: Se utiliza para definir un trait abstracto en Scala y no se puede mezclar con una clase o un objeto para proporcionar funcionalidad adicional.

## Herencia en Scala
- extends: Se utiliza para extender una clase en Scala y heredar sus métodos y atributos.
- with: Se utiliza para mezclar un trait en Scala y proporcionar funcionalidad adicional.

# Introducción a TypeSafe Config

TypeSafe Config es una biblioteca de configuración para Scala y Java que proporciona una forma sencilla de leer y escribir archivos de configuración. La biblioteca es fácil de usar y proporciona una API sencilla para leer y escribir archivos de configuración en diferentes formatos, como JSON, YAML y HOCON (Human-Optimized Config Object Notation).

- ConfigFactory: Se utiliza para cargar un archivo de configuración en Scala y Java.
- Config: Se utiliza para acceder a las propiedades de un archivo de configuración en Scala y Java.
- ConfigValue: Se utiliza para acceder a los valores de un archivo de configuración en Scala y Java.


# Introducción a Scala Generics

Scala Generics es una característica que permite definir clases, traits y funciones que pueden trabajar con diferentes tipos de datos. Esto permite reutilizar el código y escribir programas más genéricos y flexibles.

- Clases genéricas: Se utilizan para definir una clase que puede trabajar con diferentes tipos de datos.
- Traits genéricos: Se utilizan para definir un trait que puede trabajar con diferentes tipos de datos.
- Funciones genéricas: Se utilizan para definir una función que puede trabajar con diferentes tipos de datos.
- Variance: Se utiliza para definir la relación entre dos tipos genéricos en Scala.
- Bounds: Se utiliza para restringir los tipos genéricos en Scala.
- Upper Bounds: Se utiliza para restringir los tipos genéricos en Scala.
- Lower Bounds: Se utiliza para restringir los tipos genéricos en Scala.
- Context Bounds: Se utiliza para restringir los tipos genéricos en Scala y proporcionar una implementación predeterminada de un trait.


# JMH en Scala (Java Microbenchmark Harness)

JMH es una herramienta de benchmarking de Java que se utiliza para medir el rendimiento de las aplicaciones Java. JMH proporciona una forma sencilla de escribir y ejecutar benchmarks en Java y Scala.

- @Benchmark: Se utiliza para marcar un método como un benchmark en JMH.
- @State: Se utiliza para marcar una clase como un estado en JMH.
- @Setup: Se utiliza para marcar un método como un método de configuración en JMH.
- @TearDown: Se utiliza para marcar un método como un método de limpieza en JMH.
- @Fork: Se utiliza para especificar el número de forks en JMH.
- @Warmup: Se utiliza para especificar el número de iteraciones de calentamiento en JMH.
- @Measurement: Se utiliza para especificar el número de iteraciones de medición en JMH.

