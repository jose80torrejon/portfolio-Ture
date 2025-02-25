
package encoding

import org.apache.spark.sql.{SaveMode, SparkSession}

object EncodingOrcApp extends App {


  val spark = SparkSession.builder()
    .appName("EncodingOrcApp")
    .master("local[*]")
    .getOrCreate()

  spark.sparkContext.setLogLevel("ERROR")

  import spark.implicits._

  val dataV1 = Seq(
    (1, "Alice", 28, "v1"),
    (2, "Bob", 25, "v1"),
    (3, "Charlie", 30, "v1")
  )

  val dfV1 = dataV1.toDF("id", "name", "age", "version")

  dfV1.write
    .format("orc")
    .mode(SaveMode.Overwrite)
    .option("compression", "zlib")
    .save("data/orc/person_v1")

  val dfReadV1 = spark.read
    .format("orc")
    .load("data/orc/person_v1")

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
    .format("orc")
    .mode(SaveMode.Overwrite)
    .option("compression", "zlib")
    .save("data/orc/person_v2")

  val dfReadV2 = spark.read
    .format("orc")
    .load("data/orc/person_v2")

  dfReadV2.show()

  val schemaReadV2 = dfReadV2.schema
  println(schemaReadV2)

  val dfReadV1_2 = spark.read
    .format("orc")
    .load("data/orc/person_v1", "data/orc/person_v2")

  dfReadV1_2.show()

  val dataV3 = Seq(
    (1, "Alice", 28, "v3", "Lion", "Spain"),
    (2, "Bob", 25, "v3", "Valencia", "Spain"),
    (3, "Charlie", 30, "v3", "Madrid", "Spain")
  )

  val dfV3 = dataV3.toDF("id", "name", "age", "version", "city", "country")

  dfV3.write
    .format("orc")
    .mode(SaveMode.Overwrite)
    .option("compression", "zlib")
    .save("data/orc/person_v3")

  val dfReadV3 = spark.read
    .format("orc")
    .load("data/orc/person_v3")

  dfReadV3.show()

  val schemaReadV3 = dfReadV3.schema
  println(schemaReadV3)

  val dfReadV1_2_3 = spark.read
    .option("mergeSchema", "true")
    .format("orc")
    .load("data/orc/person_v1", "data/orc/person_v2", "data/orc/person_v3")

  dfReadV1_2_3.show()

  // Si city es nulo, lo rellenamos con "Unknown"
  // Si country es nulo, lo rellenamos con "Unknown"
  val dfReadV1_2_3Filled = dfReadV1_2_3
    .na.fill("Unknown", Seq("city", "country"))


  dfReadV1_2_3Filled.show(false)


  dfReadV1_2_3Filled
    .write
    .format("orc")
    .mode(SaveMode.Overwrite)
    .option("compression", "zlib")
    .save("data/orc/person_v1_2_3")


}

