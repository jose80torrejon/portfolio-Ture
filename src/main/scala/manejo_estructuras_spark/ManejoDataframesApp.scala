package manejo_estructuras_spark

import org.apache.spark.sql.{Column, DataFrame, Dataset, SaveMode}
import org.apache.spark.sql.functions.col

object ManejoDataframes {
  val OuptutPathForCsv = "out/ejemploDF01.csv"
  val OuptutPathForParquet = "out/ejemploDF01.parquet"
  val OuptutPathForJson = "out/ejemploDF01.json"
  val AlumnosDbName = "alumnos"

  val mayoresDeEdad: Column = col("age") > 18
  val mayoresDeEdadViewName: String = "mayoresDeEdad"

}


trait Displayable {
  implicit class DatasetDisplay[T](ds: Dataset[T]) {
    def display(numRows: Int = 20): Unit = {
      ds.show(numRows= numRows, truncate = false)
    }
  }
}

object ManejoDataframesApp extends App with SparkSessionWrapper with Displayable {
  import ManejoDataframes._
  // Ejemplos de uso del API de DataFrames de Spark

  import org.apache.spark.sql.functions._

  // Creamos la SparkSession
  implicit val appName: String = "Ejemplo_Dataframes"

  // Creamos un DataFrame de forma manual
  val data = Seq(("Pepe", 34, "Madrid"), ("Juan", 45, "Barcelona"),
    ("Ana", 23, "Madrid"), ("Jose", 44, "Cuenca"),
    ("Cristina", 16, "Sevilla"), ("Lucia", 11, "Sevilla"))
  val df = spark.createDataFrame(data).toDF("name", "age", "ciudad")
  df.display()
  df.printSchema()

  // FILTRADO DE COLUMNAS
  // SCALA
  val mayoresDieciocho = df.filter(mayoresDeEdad)
  mayoresDieciocho.select(s"name").display()
  mayoresDieciocho.display()

  // SQL
  // Creamos bbdd interna
  if (!spark.catalog.databaseExists(AlumnosDbName)) {
    spark.sql(s"CREATE DATABASE $AlumnosDbName")
  }
  // Establecemos que alumnos es mi bbdd actual
  spark.catalog.setCurrentDatabase(AlumnosDbName)
  // QuerySql - simplemente crear una vista del Df y atacarlo directamente con sql
  mayoresDieciocho.createOrReplaceTempView(mayoresDeEdadViewName)
  spark.sql(s"SELECT * FROM $mayoresDeEdadViewName").display()
  // Listamos mis bbdd
  spark.catalog.listDatabases().display()
  // Listamos las tablas disponibles
  spark.catalog.listTables().display()
  println(s"La BD actual es: ${spark.catalog.currentDatabase}")
  println()
  // Borramos la bbdd creada anteriormente
  spark.sql(s"DROP DATABASE $AlumnosDbName CASCADE")

  // OPERACIONES sobre el DataFrame
  println("Nombres de los alumnos")
  df.select("name").distinct().count()
  df.select("name").distinct().display()

  println("Número de alumnos por ciudad")
  df.groupBy("ciudad").count().display()

  // Agregaciones
  println("Media de edad")
  df.agg("age" -> "avg").display()

  // Crear nueva columna
  private val ColAdicionalName: String = "age2"
  val columnaAdicional = (col("age") + 1).alias(ColAdicionalName)
  val df2 = df.withColumn(ColAdicionalName, columnaAdicional)
  df2.display()

  // Escritura en ficheros desde el DF
  // CSV
  df.write.mode("overwrite").csv(OuptutPathForCsv)
  // Parquet
  df.write.mode(SaveMode.Append).parquet(OuptutPathForParquet)
  // Salvar el DataFrame en formato JSON
  //df.write.mode(SaveMode.ErrorIfExists).json("out/ejemploDF01.json")

  // Lectura de un DataFrame a partir archivos
  // CSV
  val csvDisco = spark.read.option("sep", ",").option("inferSchema", "true")
    .option("lazyQuotes", "true") // Para que no falle con comillas en los campos
    .option("header", "true")
    .csv(OuptutPathForCsv)
  csvDisco.display()

  //Parquet
  val parquetDisco = spark.read.parquet(OuptutPathForParquet)
  parquetDisco.display()

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
  //.json(ouptutPathForJson)

  //jsonDisco.show(false)

  //System.exit(0)


}
