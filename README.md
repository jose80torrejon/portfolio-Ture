# SparkEOI

This is a Scala project that demonstrates various concepts related to Apache Spark and functional programming patterns using the Cats library.

This repository is part of the Master in Data Engineering at EOI Escuela de Organización Industrial.



## Project Structure

The project is organized into several packages, each demonstrating a different concept:

- `spark.sql`: Contains examples related to Spark SQL and custom functions.
- `colecciones`: Contains examples related to Scala collections.
- `composicion`: Contains examples demonstrating the composition of functions.
- `errores`: Contains examples demonstrating error handling.
- `patrones`: Contains examples demonstrating functional programming patterns.
  - `applicative`: Contains examples demonstrating the Applicative pattern.
  - `functors`: Contains examples demonstrating the Functor pattern.
  - `monads`: Contains examples demonstrating the Monad pattern.
  - `monoids`: Contains examples demonstrating the Monoid pattern.
  - `semigroups`: Contains examples demonstrating the Semigroup pattern.
  - `traversables`: Contains examples demonstrating the Traversable pattern.
  - `validated`: Contains examples demonstrating the Validated pattern.
  - `writer`: Contains examples demonstrating the Writer pattern.
  - `reader`: Contains examples demonstrating the Reader pattern.
  - `state`: Contains examples demonstrating the State pattern.
  - `free`: Contains examples demonstrating the Free pattern.
  - `coproduct`: Contains examples demonstrating the Coproduct pattern.
- 

## Dependencies

The project uses the following main dependencies:

- SBT version 1.10.7
- Scala version 2.13.16
- Apache Spark version 3.5.4
- Cats Core version 2.10.0
- ScalaTest version 3.2.19

## Building and Running

The project uses sbt for building and running. You can run the project using the following command:

```bash
sbt run
```

## Testing

Tests are located in the src/test/scala directory. You can run the tests using the following command:

```bash
sbt test
```


## Añadir las siguientes Java Opts en la configuración de ejecución de la aplicación en IntelliJ IDEA 
Para que Spark funciones en Java +17, es necesario añadir las siguientes opciones de Java en la configuración de ejecución de la aplicación en IntelliJ IDEA:

```
--add-opens=java.base/java.lang=ALL-UNNAMED
--add-opens=java.base/java.util=ALL-UNNAMED
--add-opens=java.base/java.io=ALL-UNNAMED
--add-opens=java.base/java.util.concurrent=ALL-UNNAMED
--add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED
--add-opens=java.base/java.util.concurrent.locks=ALL-UNNAMED
--add-opens=java.base/java.util.regex=ALL-UNNAMED
--add-opens=java.base/java.util.stream=ALL-UNNAMED
--add-opens=java.base/java.util.function=ALL-UNNAMED
--add-opens=java.base/java.util.jar=ALL-UNNAMED
--add-opens=java.base/java.util.zip=ALL-UNNAMED
--add-opens=java.base/java.util.spi=ALL-UNNAMED
--add-opens=java.base/java.lang.invoke=ALL-UNNAMED
--add-opens=java.base/java.lang.reflect=ALL-UNNAMED
--add-opens=java.base/java.net=ALL-UNNAMED
--add-opens=java.base/java.nio=ALL-UNNAMED
--add-opens=java.base/sun.nio.ch=ALL-UNNAMED
--add-opens=java.base/sun.nio.cs=ALL-UNNAMED
--add-opens=java.base/sun.security.action=ALL-UNNAMED
--add-opens=java.base/sun.util.calendar=ALL-UNNAMED
--add-opens=java.security.jgss/sun.security.krb5=ALL-UNNAMED
```

