
package encoding

import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.{SaveMode, SparkSession}

object EncodingDeltaApp extends App {

  // --conf spark.sql.extensions=io.delta.sql.DeltaSparkSessionExtension
  // --conf spark.sql.catalog.spark_catalog=org.apache.spark.sql.delta.catalog.DeltaCatalog
  val spark = SparkSession.builder()
    .appName("EncodingDeltaApp")
    .config("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension")
    .config("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.delta.catalog.DeltaCatalog")
    // Para evitar problemas con la retención de datos: https://docs.delta.io/latest/delta-retention.html
    .config("spark.databricks.delta.retentionDurationCheck.enabled", "false")
    .master("local[*]")
    .getOrCreate()

  spark.sparkContext.setLogLevel("ERROR")

  import spark.implicits._

  val personV1Path = "/tmp/data/delta/person_v1"
  val personV2Path = "/tmp/data/delta/person_v2"
  val personV3Path = "/tmp/data/delta/person_v3"

  val dataV1 = Seq(
    (1, "Alice", 28, "v1"),
    (2, "Bob", 25, "v1"),
    (3, "Charlie", 30, "v1")
  )

  val dfV1 = dataV1.toDF("id", "name", "age", "version")

  dfV1.write
    .format("delta")
    .mode(SaveMode.Overwrite)
    .save(personV1Path)

  val dfReadV1 = spark.read
    .format("delta")
    .option("mergeSchema", "true")
    .load(personV1Path)

  dfReadV1.show()

  val schemaReadV1 = dfReadV1.schema
  println(schemaReadV1)

  val dataV2 = Seq(
    (1, "Alice", 28, "v2", "Lion"),
    (2, "Bob", 25, "v2", "Valencia"),
    (3, "Charlie", 30, "v2", "Madrid")
  )

  val dfV2 = dataV2.toDF("id", "name", "age", "version", "city")

  dfV2.write
    .format("delta")
    .mode(SaveMode.Overwrite)
    .save(personV2Path)

  val dfReadV2 = spark.read
    .format("delta")
    .option("mergeSchema", "true")
    .load(personV2Path)

  dfReadV2.show()

  val schemaReadV2 = dfReadV2.schema
  println(schemaReadV2)

  val dfReadV1_2 = dfReadV1
    .withColumn("city", lit("Unknown"))
    .union(dfReadV2)


  dfReadV1_2.show()

  val dataV3 = Seq(
    (1, "Alice", 28, "v3", "Lion", "Spain"),
    (2, "Bob", 25, "v3", "Valencia", "Spain"),
    (3, "Charlie", 30, "v3", "Madrid", "Spain")
  )

  val dfV3 = dataV3.toDF("id", "name", "age", "version", "city", "country")

  dfV3.write
    .format("delta")
    .mode(SaveMode.Overwrite)
    .save(personV3Path)

  val dfReadV3 = spark.read
    .format("delta")
    .option("mergeSchema", "true")
    .load(personV3Path)

  dfReadV3.show()

  val schemaReadV3 = dfReadV3.schema
  println(schemaReadV3)

  val dfReadV1_2_3 = dfReadV1_2
    .withColumn("country", lit("Unknown"))
    .union(dfReadV3)

  dfReadV1_2_3.show()

}