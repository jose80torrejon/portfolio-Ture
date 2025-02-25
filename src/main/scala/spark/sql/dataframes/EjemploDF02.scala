
package spark.sql.dataframes

import spark.sql.dataframes.DataGen02.formatearComoCSV
import spark.sql.dataframes.Ejemplo2Config._

import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

import scala.Console.{BOLD, RESET}


object DataGen02 {
  // Generar un DataFrame con datos de ejemplo usando funciones random

  // Estructura de datos para las eventos
  case class Evento(id: Int, nombre: String, fecha: String, valor: Double, tipo: String = "Alerta1") {
    // Convertimos el evento a una lista de valores tipo csv
    def toCSV: String = s"$id,$nombre,$fecha,$valor,$tipo"

    override def toString: String = toCSV
  }

  case class EventoIncorrecto(id: Int, nombre: String, fecha: String, tipo: String) {
    // Convertimos el evento a una lista de valores tipo csv
    def toCSV: String = s"$id,$nombre,$fecha,$tipo"

    // Quitamos "" de toda la cadena
    def eliminarCaracteres(cadena: String): String = {
      cadena.replaceAll("\"", "")
    }

    override def toString: String = eliminarCaracteres(toCSV)
  }

  // Usamos el API RDD para eliminar las comillas de la cadena
  def eliminarCaracteresDF(df: DataFrame, nombreColumnaLimpia: String, cadena: String = "\""): DataFrame = {
    val rdd = df.rdd.map(row => row.getString(0).replaceAll(cadena, ""))
    // Hacemos split por la coma para que sea un array de strings
    val rdd2 = rdd.map(_.split(","))
    // Creamos un nuevo DataFrame con la cadena sin comillas
    df.sparkSession.createDataFrame(rdd2.map(Tuple1.apply)).toDF()
  }

  // Como recibimos lineas de texto con distinto rango de campos, vamos a formatearlas
  // Recibe: "100,Evento-100,2020-01-01,Alerta2"
  // Devuelve: "100","Evento-100","2020-01-01","Alerta2"
  val convertToProperCSVUDF: UserDefinedFunction = udf((row: String) => {
    val cols = row.split(",")
    // Todas las columnas llevan comillas
    cols.map { field =>
      "\"" + field + "\""
    }.mkString(",")
  })

  // Crear convertToProperCSV: String => String
  def convertToProperCSV(lineaCsv: String): String = {
    val cols = lineaCsv.split(",")
    // Todas las columnas llevan comillas
    cols.map { field =>
      """"""" + field + """""""
    }.mkString(",")
  }


  // Formatear un DataFrame como CSV

  def formatearComoCSV(csvRaw: DataFrame)(implicit spark: SparkSession): DataFrame = {
    import org.apache.spark.sql.functions._
    val res = csvRaw.withColumn("evento", convertToProperCSVUDF(col("evento")))
    println(BOLD + "Datos formateados como CSV" + RESET)
    res.show(30, truncate = false)
    res
  }

  // Generar un DataFrame con datos de ejemplo
  def generar(spark: org.apache.spark.sql.SparkSession): org.apache.spark.sql.DataFrame = {
    import spark.implicits._
    // Generar un DataFrame con datos de ejemplo
    // Cada 10 evento generamos 1 con una estructura diferente

    val eventos = (1 to 1000).map { i =>
      if (i % 10 == 0) {
        EventoIncorrecto(i, s"Evento-$i", s"2020-01-01", "Alerta2").toString
      } else {
        Evento(i, s"Evento-$i", s"2020-01-01", math.random).toString
      }
    }
    // Convertir la lista de eventos a un DataFrame
    spark.sparkContext.parallelize(eventos).toDF("evento")
  }

  def guardarCSV(df: org.apache.spark.sql.DataFrame, path: String): Unit = {
    df.write
      .mode(SaveMode.Overwrite)
      .option("header", "false")
      //.option("quote", "") // manejo de comillas
      .csv(path)
  }

  def guardar(df: org.apache.spark.sql.DataFrame, path: String): Unit = {
    df.write.mode(SaveMode.Overwrite).parquet(path)
  }


}

object Ejemplo2Config {
  // Eventos tal y como vienen de los sistemas legados
  val eventosRaw = "data/eventos"
  // Rutas para los eventos correctos e incorrectos
  val eventosCorrectos = "data/eventosCorrectos"
  val eventosIncorrectos = "data/eventosIncorrectos"

  val nombreColumnaEventos = "evento"

  // Los eventos correctos no contienen la cadena 'Alerta2'
  val alertaCorrecta = """'Alerta2'"""
  val filtroElementosCorrectos = s"not contains($nombreColumnaEventos, $alertaCorrecta)"
  // Los eventos incorrectos contienen la cadena 'Alerta2'
  val filtroElementosIncorrectos = s"contains($nombreColumnaEventos, $alertaCorrecta)"

}

object EjemploDF02 extends App {
  // Ejemplos de uso del API de DataFrames de Spark

  import org.apache.spark.sql.SparkSession

  // Crear una SparkSession
  val spark = SparkSession.builder().appName("EjemploDF02").master("local[2]").getOrCreate()


  spark.sparkContext.setLogLevel("ERROR")

  val usarDatosGenerados: Boolean = true

  val dfRaw: DataFrame = if (!usarDatosGenerados) {
    // Generar un DataFrame con datos de ejemplo
    val dfGen = DataGen02.generar(spark)
    // Generar un DataFrame con datos de ejemplo pero antes quitamos las comillas de los extremos
    val df = formatearComoCSV(dfGen)(spark)
    df.show(30, truncate = false)
    // Guardar el DataFrame en un fichero
    DataGen02.guardarCSV(df, eventosRaw)
    df
  } else {
    // Cargar los datos de eventos
    val existing: DataFrame = spark.read
      .option("header", "false")
      //.option("quote", "") // manejo de comillas
      .option("delimiter", ",")
      .csv(eventosRaw)
    existing
  }

  // Filtrar los eventos correctos
  println(BOLD + " - Eventos correctos" + RESET)
  // val dfCorrectos = df2b.filter("split(evento, ',')[3] != 'Alerta2'")
  // Los correctos no contienen la cadena 'Alerta2' por lo que usamos contains
  val dfCorrectos = dfRaw.filter(filtroElementosCorrectos)

  dfCorrectos.show(30, truncate = false)
  DataGen02.guardarCSV(dfCorrectos, eventosCorrectos)

  // Filtrar los eventos incorrectos
  println(BOLD + " - Eventos incorrectos" + RESET)
  val dfIncorrectos = dfRaw.filter(filtroElementosIncorrectos)
  dfIncorrectos.show(30, truncate = false)
  DataGen02.guardarCSV(dfIncorrectos, eventosIncorrectos)


  // Vamos a intentar inferir el esquema de los datos para que sean más manejables
  // Inferimos esquema a los datos correctos
  println(BOLD + "Inferir esquema 02" + RESET)
  val dfCorrectosConEsquema = spark.read
    .option("header", "false")
    .option("delimiter", ",")
    //.option("quote", "") // manejo de comillas: quitamos las comillas
    .option("inferSchema", "true")
    .csv(eventosCorrectos)

  dfCorrectosConEsquema.show(30, truncate = false)

  // Inferimos esquema a los datos incorrectos
  println(BOLD + "Inferir esquema 03" + RESET)
  val dfIncorrectosConEsquema = spark.read
    .option("header", "false")
    .option("delimiter", ",")
    //.option("quote", "'") // manejo de comillas
    .option("inferSchema", "true")
    .csv(eventosIncorrectos)

  dfIncorrectosConEsquema.show(30, truncate = false)
  // "533","Evento-533","2020-01-01","0.9117341677618456","Alerta1"
  val nombreColumnasCorrectos = Seq("id", "nombre", "fecha", "valor", "tipo")
  val nombreColumnasIncorrectos = Seq("id", "nombre", "fecha", "tipo")

  val schemaCorrectos = StructType(
    nombreColumnasCorrectos.map { col =>
      StructField(col, StringType, nullable = true)
    }
  )

  val schemaIncorrectos = StructType(
    nombreColumnasIncorrectos.map { col =>
      StructField(col, StringType, nullable = true)
    }
  )

  // Aplicamos el esquema a los DataFrames
  val dfCorrectosConEsquema2 = spark
    .read
    .schema(schemaCorrectos)
    .option("header", "false")
    .option("delimiter", ",")
    .csv(eventosCorrectos)

  dfCorrectosConEsquema2.show(30, truncate = false)

  val dfIncorrectosConEsquema2 = spark
    .read
    .schema(schemaIncorrectos)
    .option("header", "false")
    .option("delimiter", ",")
    .csv(eventosIncorrectos)
  dfIncorrectosConEsquema2.show(30, truncate = false)

  System.exit(0)
  // Ahora deberíamos poder mergear los dos DataFrames
  println(BOLD + "Merge de DataFrames" + RESET)
  val dfMerged = dfCorrectosConEsquema.union(dfIncorrectosConEsquema)
  dfMerged.printSchema()
  dfMerged.show(30, truncate = false)


}
