
package encoding

import org.apache.spark.sql.SaveMode

/*
Columna ID: [1, 2, 3] - Fichero01
Columna Nombre: [Alice, Bob, Carol]  - Fichero02
Columna Edad: [30, 25, 40] - Fichero03
Columna Versión: [v1, v1, v1] - Fichero04
 */

object EncodingParquetApp extends App {

  import org.apache.spark.sql.SparkSession

  val spark = SparkSession.builder()
    .appName("EncodingParquetApp")
    .master("local[*]")
    .getOrCreate()

  spark.sparkContext.setLogLevel("ERROR")

  import spark.implicits._

  val data = Seq(
    (1, "Alice", 28, "v1"),
    (2, "Bob", 25, "v1"),
    (3, "Charlie", 30, "v1")
  )

  val df = data.toDF("id", "name", "age", "version")

  df.write
    .format("parquet")
    .mode(SaveMode.Overwrite)
    .save("data/parquet/person_v1")

  val dfRead = spark.read
    .format("parquet")
    .load("data/parquet/person_v1")

  dfRead.show()

  val schemaRead = dfRead.schema
  println(schemaRead)

  val dataV2 = Seq(
    (1, "Alice", 28, "v2", "Lion"),
    (2, "Bob", 25, "v2", "Valencia"),
    (3, "Charlie", 30, "v2", "Madrid")
  )

  val dfV2 = dataV2.toDF("id", "name", "age", "version", "city")

  dfV2.write
    .format("parquet")
    .mode(SaveMode.Overwrite)
    .save("data/parquet/person_v2")

  val dfReadV2 = spark.read
    .format("parquet")
    .load("data/parquet/person_v2")

  dfReadV2.show()

  val schemaReadV2 = dfReadV2.schema
  println(schemaReadV2)

  val dfReadV1 = spark.read
    .format("parquet")
    .load("data/parquet/person_v1")

  dfReadV1.show()

  val dfReadV2_2 = spark.read
    .format("parquet")
    .load("data/parquet/person_v2")

  dfReadV2_2.show()

  // Quiero demostrar la evolución de esquemas Parquet
  // Añadimos una columna al esquema V2

  val dataV3 = Seq(
    (1, "Alice", 28, "v3", "Lion", "France"),
    (2, "Bob", 25, "v3", "Valencia", "Spain"),
    (3, "Charlie", 30, "v3", "Madrid", "Spain")
  )

  val dfV3 = dataV3.toDF("id", "name", "age", "version", "city", "country")

  dfV3.write
    .format("parquet")
    .mode(SaveMode.Overwrite)
    .save("data/parquet/person_v3")

  val dfReadV3 = spark.read
    .format("parquet")
    .load("data/parquet/person_v3")

  dfReadV3.show()

  val schemaReadV3 = dfReadV3.schema
  println(schemaReadV3)

  // Leemos los datos con el esquema V2
  val dfReadV1_2_3 = spark.read
    .option("mergeSchema", "true")
    .schema(schemaReadV3)
    .format("parquet")
    .load("data/parquet/person_v2", "data/parquet/person_v3", "data/parquet/person_v1")

  dfReadV1_2_3.show()

}
