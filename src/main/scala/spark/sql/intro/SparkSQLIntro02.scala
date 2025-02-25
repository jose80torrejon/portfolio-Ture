
package spark.sql.intro

import org.apache.spark.sql.{Column, DataFrame, SparkSession}
import org.apache.spark.sql.functions.expr

import scala.Console._

object SparkSQLIntro02 extends App {

  import org.apache.spark.sql.SparkSession

  // Crear un SparkSession
  implicit val spark: SparkSession  = SparkSession.builder()
    // Nombre de la aplicación tal y como aparecerá en la Spark UI
    .appName("SparkSQLIntro02")
    .master("local[*]")
    .getOrCreate()

  // - setLogLevel: Establece el nivel de log de Spark
  spark.sparkContext.setLogLevel("ERROR")

  // - version: Devuelve la versión de Spark
  println(s"Spark version: ${spark.version}")
  println()
  println("Creada la sesión de Spark con la siguiente configuración:")
  val maxKeyLength = spark.conf.getAll.map(_._1.length).max
  spark.conf.getAll.foreach {
    case (k, v) =>
      println(s"${k.padTo(maxKeyLength, ' ')}: $v")
  }
  println()

  // Crear un DataFrame a partir de los Jsons del IotDataGenerator
  val df = spark.read
    .option("inferSchema", "true")
    .json(IotDataGenerator.IotJsonDataPath)

  df.printSchema()
  df.show(truncate = false)
  //System.exit(0)


  /*
  root
  |-- deviceId: long (nullable = true)
  |-- deviceType: string (nullable = true)
  |-- humidity: double (nullable = true)
  |-- temperature: double (nullable = true)
  |-- timestamp: long (nullable = true)
   */

  // Operaciones sobre el DataFrame

  println(df.count())

  df.isEmpty

  import scala.language.postfixOps

  println(df count)

  // Se puede cachear un DataFrame para mejorar el rendimiento
  // Pero esto solo lo haremos si vamos a reutilizar el DataFrame como en este caso que vamos a hacer varias operaciones

  df cache // df.cache() es lo mismo

  // Operaciones sobre el DataFrame
  println("Operaciones sobre el DataFrame")
  println("-------------------------------")

  println("Número de dispositivos distintos")
  df.select("deviceId").distinct().count()
  df.select("deviceId").distinct().show()
  println()
  //System.exit(0)
  println("Número de dispositivos por tipo")
  df.groupBy("deviceType").count().show()
  //System.exit(0)
  // Filtro
  println("Dispositivos con temperatura mayor que 5")

  import org.apache.spark.sql.functions.col
  df.filter(col("temperature") > 5).show()

  import spark.implicits._

  df.filter($"temperature" > 5).show()
  df.filter(df("temperature") > 5).show() // Equivalente
  df.filter("temperature > 5").show()     // Equivalente

  val condicionTemperatura: Column = col("temperature") > 5
  df.filter(condicionTemperatura).show()
  println()

  val condicionHumedad = col("humidity") > 50
  println("Dispositivos con humedad mayor que 50")
  df.filter(condicionHumedad).show()
  println()

  val condiciones = condicionTemperatura && condicionHumedad
  println(BOLD + BLUE + "Dispositivos con temperatura mayor que 5 y humedad mayor que 50" + RESET)
  df.filter(condiciones).show()
  println()

  //System.exit(0)

  // Otras Operaciones sobre el DataFrame
  println("Otras Operaciones sobre el DataFrame")
  println("-------------------------------------")

  // Agregaciones
  println("Media de la temperatura")
  df.agg("temperature" -> "avg").show()
  df.agg("temperature" -> "avg", "humidity" -> "avg").show()
  println()

  // Agregaciones con alias

  import org.apache.spark.sql.functions.avg

  println("Media de la temperatura con alias")
  df.agg(avg("temperature").alias("media_temperatura")).show()
  println()

  // Agregaciones con alias y expresiones
  println("Media de la temperatura con alias y expresiones")
  df.agg(avg("temperature").alias("media_temperatura"), avg("humidity").alias("media_humidity")).show()
  println()

  // Agregaciones con expresiones
  println("Media de la temperatura con expresiones")
  df.agg(avg("temperature")).show()
  println()

  // Agregaciones con expresiones y alias
  println("Media de la temperatura con expresiones y alias")
  df.agg(avg("temperature") as "media_temperatura").show()
  println()

  // Agregaciones con expresiones y alias
  println("Media de la temperatura y humedad con expresiones y alias")
  df.agg(avg("temperature") as "media_temperatura", avg("humidity") as "media_humedad").show()
  println()

  // Operaciones sobre columnas con expresiones, funciones y withColumn / withColumnRenamed
  println("Operaciones sobre columnas con expresiones, funciones y withColumn / withColumnRenamed")
  println("------------------------------------------------------------------------------------")

  import org.apache.spark.sql.functions._

  println("Columnas con expresiones")
  df.withColumn("temperatura_x2", col("temperature") * 2).show()
  println()
  println("Columnas con funciones")
  df.withColumn("temperatura_x2", expr("temperature * 2")).show()
  println()
  println("Columnas con funciones y alias")
  df.withColumn("temperatura_x2", expr("temperature * 2") as "temperatura_x2").show()
  println()

  println("Columnas con funciones y alias")
  df.withColumn("temperatura_x2", FuncionesTemperatura.dobleDeLaTemperaturaConAlias).show()
  println()

  println("Columnas con funciones y alias")
  df.withColumn("temperatura_x2", FuncionesTemperatura.dobleDeLaTemperatura).withColumnRenamed("humidity", "humedad").show()
  println()

  val dfConHumedad: DataFrame => DataFrame = (df: DataFrame) => df.withColumnRenamed("humidity", "humedad")
  val nuevoDF = dfConHumedad(df)
  nuevoDF.show()
  println()

  // Drop
  println(BOLD + RED + "----- Drop -----" + RESET)
  val dfSinTemperatura: DataFrame = df.drop("temperature")
  dfSinTemperatura.show()
  println()

  // Cols
  println(BOLD + RED + "----- Cols -----" + RESET)
  df.drop("temperature", "humidity").show(truncate = false)

  // utilizamos el operador _* para convertir la secuencia en argumentos separados.
  val dfNueva: DataFrame = df.drop(FuncionesTemperaturaHumedad.columnasAEliminar: _*)
  dfNueva.show(truncate = false)

  spark.stop()
  System.exit(0)
}

object FuncionesTemperatura {

  import org.apache.spark.sql.Column

  val TemperaturaPorDosAlias: String = "temperatura_x2"
  val dobleDeLaTemperatura: Column = expr("temperature * 2")
  val dobleDeLaTemperaturaConAlias: Column = dobleDeLaTemperatura as TemperaturaPorDosAlias
}

object FuncionesHumedad {

  import org.apache.spark.sql.Column

  val HumedadPorDosAlias = "humedad_x2"
  val dobleDeLaHumedad: Column = expr("humidity * 2")
  val dobleDeLaHumedadConAlias: Column = dobleDeLaHumedad as HumedadPorDosAlias
}

object FuncionesTemperaturaHumedad {
  val columnasAEliminar: Seq[String] = Seq("temperature", "humidity")

}
