
package spark.sql.dataframes

import org.apache.spark.sql.{Column, SaveMode}

object EjemploDF01 extends App {
  // Ejemplos de uso del API de DataFrames de Spark

  import org.apache.spark.sql.SparkSession
  import org.apache.spark.sql.functions._

  // Crear una SparkSession
  val spark = SparkSession.builder().appName("EjemploDF01").master("local[2]").getOrCreate()

  spark.sparkContext.setLogLevel("ERROR")
  // Crear un DataFrame a partir de una secuencia de tuplas
  val data = Seq(("Alice", 34), ("Bob", 45), ("Charlie", 23))
  // toDF() es un método implícito de SparkSession que convierte una secuencia de tuplas en un DataFrame
  // El método toDF() recibe un parámetro de tipo varargs que se utiliza para nombrar las columnas del DataFrame
  val df = spark.createDataFrame(data).toDF("name", "age")

  // Mostrar el contenido del DataFrame
  df.show()
  df.show(truncate = false)
  df.show(numRows = 2, truncate = false)

  // Mostrar el esquema del DataFrame
  df.printSchema()

  // Filtrar el DataFrame
  val mayoresDeVeintidos = df.filter(col("age") > 22)
  mayoresDeVeintidos.show(false)
  val criterioEdad: Column = col("age") > 22
  val mayoresDeVeintidos2 = df.filter(criterioEdad)
  mayoresDeVeintidos2.show(false)

  // Crear una nueva columna
  val columnaAdicional = (col("age") + 1).alias("age2")
  val df2 = df.withColumn("age2", columnaAdicional)
  df2.show(false)

  // Crear una nueva columna con una función definida por el usuario
  val doblarEdad = udf((age: Int) => age * 2)
  val columnaDobleEdad = doblarEdad(col("age")).alias("ageDoble")
  val df3 = df.withColumn("ageDoble", columnaDobleEdad)
  df3.show(false)

  // Sin usar udf
  val columnaDobleEdad2 = col("age") * 2
  val df4 = df.withColumn("ageDoble", columnaDobleEdad2)
  df4.show(false)

  // Salvar el DataFrame en formato CSV
  df.write.mode("overwrite").csv("out/ejemploDF01.csv")

  // Salvar el DataFrame en formato Parquet
  df.write.mode(SaveMode.Append).parquet("out/ejemploDF01.parquet")

  // Salvar el DataFrame en formato JSON
  //df.write.mode(SaveMode.ErrorIfExists).json("out/ejemploDF01.json")

  df.coalesce(1)
    .write
    .mode(SaveMode.Append)
    .parquet("out/ejemploDF01.parquet")

  // Si no se especifica el formato, Spark asume que es Parquet
  df.coalesce(1)
    .write
    .mode(SaveMode.Append)
    .save("out/ejemploDF01.parquet")

  // Mas cosas interesantes sobre write
  // https://spark.apache.org/docs/latest/api/scala/org/apache/spark/sql/DataFrameWriter.html

  // Lectura de un DataFrame a partir archivos
  val csvDisco = spark.read
    .option("sep", ",")
    .option("inferSchema", "true")
    .option("lazyQuotes", "true") // Para que no falle con comillas en los campos
    .option("header", "true")
    .csv("out/ejemploDF01.csv")
  csvDisco.show(false)

  val parquetDisco = spark
    .read
    .parquet("out/ejemploDF01.parquet")
  parquetDisco.show(false)

  val jsonDisco = spark
    .read
    .option("multiLine", "true")
    .option("allowComments", "true") // Permite comentarios en el JSON
    // mode: PERMISSIVE, DROPMALFORMED, FAILFAST
    // PERMISSIVE: Carga los datos que puede y los errores los pone en null
    // DROPMALFORMED: Elimina las filas con errores
    // FAILFAST: Falla si hay errores
    .option("mode", "PERMISSIVE")
    // Permite leer archivos con errores
    .option("inferSchema", "true")
    // Opción de sampling para leer solo una muestra de los datos
    .option("samplingRatio", "0.1")
    // Lee solo el 10% de los datos
    //.json("out/ejemploDF01.json")

  //jsonDisco.show(false)


}
