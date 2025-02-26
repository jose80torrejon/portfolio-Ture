package manejo_estructuras_spark

import org.apache.spark.sql.{Column, SaveMode}

object manejo_Dataframes extends App {
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
  // SCALA
  val mayoresDieciocho =  df.filter(col("age") > 18)
  mayoresDieciocho.select(s"name").show()
  mayoresDieciocho.show()

  // SQL
  // Creamos bbdd interna
  if (!spark.catalog.databaseExists("alumnos")) {spark.sql("CREATE DATABASE alumnos")}
  // Establecemos que alumnos es mi bbdd actual
  spark.catalog.setCurrentDatabase("alumnos")
  // QuerySql - simplemente crear una vista del Df y atacarlo directamente con sql
  mayoresDieciocho.createOrReplaceTempView("mayoresDieciochoView")
  spark.sql("SELECT * FROM mayoresDieciochoView").show()
  // Listamos mis bbdd
  spark.catalog.listDatabases().show(truncate = false)
  // Listamos las tablas disponibles
  spark.catalog.listTables().show(truncate = false)
  println(s"La BD actual es: ${spark.catalog.currentDatabase}")
  println()
  // Borramos la bbdd creada anteriormente
  spark.sql("DROP DATABASE alumnos CASCADE")

  // OPERACIONES sobre el DataFrame
  println("Nombres de los alumnos")
  df.select("name").distinct().count()
  df.select("name").distinct().show()

  println("Número de alumnos por ciudad")
  df.groupBy("ciudad").count().show()

  // Agregaciones
  println("Media de edad")
  df.agg("age" -> "avg").show()

  // Crear nueva columna
  val columnaAdicional = (col("age") + 1).alias("age2")
  val df2 = df.withColumn("age2", columnaAdicional)
  df2.show(false)

  // Escritura en ficheros desde el DF
  // CSV
  df.write.mode("overwrite").csv("out/ejemploDF01.csv")
  // Parquet
  df.write.mode(SaveMode.Append).parquet("out/ejemploDF01.parquet")
  // Salvar el DataFrame en formato JSON
  //df.write.mode(SaveMode.ErrorIfExists).json("out/ejemploDF01.json")

  // Lectura de un DataFrame a partir archivos
  // CSV
  val csvDisco = spark.read.option("sep", ",").option("inferSchema", "true")
    .option("lazyQuotes", "true") // Para que no falle con comillas en los campos
    .option("header", "true")
    .csv("out/ejemploDF01.csv")
  csvDisco.show(false)

  //Parquet
  val parquetDisco = spark.read.parquet("out/ejemploDF01.parquet")
  parquetDisco.show(false)

  //JSON
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

  //System.exit(0)




}
