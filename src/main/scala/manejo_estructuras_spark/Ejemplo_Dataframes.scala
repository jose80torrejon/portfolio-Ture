package manejo_estructuras_spark

import org.apache.spark.sql.{Column, SaveMode}

object Ejemplo_Dataframes extends App {
  // Ejemplos de uso del API de DataFrames de Spark
  import org.apache.spark.sql.SparkSession
  import org.apache.spark.sql.functions._

  // Creamos la SparkSession
  val spark = SparkSession.builder().appName("Ejemplo_Dataframes").master("local[*]").getOrCreate()
  spark.sparkContext.setLogLevel("ERROR")

  // Creamos un DataFrame de forma manual
  val data = Seq( ("Pepe", 34, "Madrid"), ("Juan", 45, "Barcelona"),
                  ("Ana", 23, "Madrid"), ("Jose", 44, "Cuenca"),
                  ("Cristina", 16, "Sevilla"), ("Lucia", 11, "Sevilla"))
  val df = spark.createDataFrame(data).toDF("name", "age", "ciudad")
  df.show()
  df.printSchema()

  // FILTRADO DE COLUMNAS
  // SELECT SCALA
  val mayoresDieciocho =  df.filter(col("age") > 18)
  mayoresDieciocho.select(s"name").show()
  mayoresDieciocho.show()

  // SQL
  // Creamos bbdd interna (opcional)
  if (!spark.catalog.databaseExists("alumnos")) {
    spark.sql("CREATE DATABASE alumnos")
  }
  spark.catalog.setCurrentDatabase("alumnos")  //establecemos que alumnos es mi bbdd actual

  // QuerySql - simplemente crear una vista del Df y atacarlo directamente con sql
  mayoresDieciocho.createOrReplaceTempView("mayoresDieciochoView")
  spark.sql("SELECT * FROM mayoresDieciochoView").show()

  spark.catalog.listDatabases().show(truncate = false)  //listamos mis bbdd
  spark.catalog.listTables().show(truncate = false)     //listamos las tablas disponibles
  println(s"La BD actual es: ${spark.catalog.currentDatabase}")
  println()
  // Borramos la bbdd creada anteriormente
  spark.sql("DROP DATABASE alumnos CASCADE")

  System.exit(0)

  // CREACIÓN NUEVA COLUMNA
  val columnaAdicional = (col("age") + 1).alias("age2")
  val df2 = df.withColumn("age2", columnaAdicional)
  df2.show(false)
  System.exit(0)

  // GENERACIÓN FICHEROS DE SALIDA A PARTIR DEL DF
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
