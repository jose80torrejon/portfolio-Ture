# Assembly de Spark

Un assembly es un archivo JAR que contiene las clases de una aplicación y todas sus dependencias. En el caso de Spark, un assembly JAR es un archivo JAR que contiene su código, las dependencias de Spark y las dependencias de tu aplicación. Los assembly JAR son necesarios para ejecutar aplicaciones Spark en un clúster.

## Crear un assembly JAR

Para crear un assembly JAR, puedes usar la herramienta `sbt-assembly`. Para hacerlo, agrega el siguiente fragmento de código a tu archivo `build.sbt`:

```scala
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.6")
```

Luego, ejecuta el siguiente comando para crear el assembly JAR:

```bash 
sbt assembly
```

```bash 
sbt publicar-docker
```