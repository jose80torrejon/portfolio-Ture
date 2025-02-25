
package encoding

import org.apache.avro.Schema
import org.apache.spark.sql.SaveMode

import scala.io.Source

object EncodingAvroApp extends App {

  // Avro es un formato de serialización de datos binarios orientado a registros
  // Dispone de un esquema que permite la serialización y deserialización de datos
  // Una de las mayores ventajas de Avro es que los datos serializados contienen su esquema y pueden ser leídos por cualquier lenguaje de programación
  // Este esquema se puede evolucionar con el tiempo, añadiendo o eliminando campos, y los datos seguirán siendo legibles

  // Para trabajar con Avro en Spark necesitamos importar las siguientes librerías:
  // spark-avro: librería que permite leer y escribir datos en formato Avro

  import org.apache.spark.sql.SparkSession

  // Creamos una SparkSession
  val spark = SparkSession.builder()
    .appName("EncodingAvroApp")
    .master("local[*]")
    .getOrCreate()

  spark.sparkContext.setLogLevel("ERROR")

  import spark.implicits._

  val data = Seq(
    (1, "Alice", 28, "v1"),
    (2, "Bob", 25, "v1"),
    (3, "Charlie", 30, "v1")
  )

  // Definimos el esquema de los datos con un fichero AVSC (Avro Schema)
  // {  "type": "record",  "name": "Person",  "fields": [    { "name": "id", "type": "int" },    { "name": "name", "type": "string" },    { "name": "age", "type": "int" },    { "name": "version", "type": "string" }  ]}
  val personV1SchemaFile = "src/main/resources/avro/person_v1.avsc"

  // Read schema file
  val schemaSource = Source.fromFile(personV1SchemaFile)
  val schemaV1 = try schemaSource.mkString finally schemaSource.close()

  // Parse the schema
  val parserV1 = new Schema.Parser
  val avroSchemaV1 = parserV1.parse(schemaV1)
  val schemaStrV1 = avroSchemaV1.toString


  // Creamos un DataFrame a partir de los datos y el esquema

  val df = data.toDF("id", "name", "age", "version")

  // Escribimos el DataFrame en formato Avro
  df.write
    .format("avro")
    .mode(SaveMode.Overwrite)
    .option("avroSchema", schemaStrV1)
    .save("data/avro/person_v1")

  // Leemos los datos en formato Avro
  val dfRead = spark.read
    .format("avro")
    // Esto es opcional, porque el esquema ya está en los datos
    // .option("avroSchema", schemaStrV1)
    .load("data/avro/person_v1")

  dfRead.show()

  // Podemos leer el esquema de los datos Avro
  val schemaRead = dfRead.schema
  println(schemaRead)

  // Definimos la V2 del esquema
  // {  "type": "record",  "name": "Person",  "fields": [    { "name": "id", "type": "int" },    { "name": "name", "type": "string" },    { "name": "age", "type": "int" },    { "name": "version", "type": "string" },    { "name": "city", "type": "string" }  ]}

  val personV2SchemaFile = "src/main/resources/avro/person_v2.avsc"

  // Leemos el esquema V2
  val schemaSourceV2 = Source.fromFile(personV2SchemaFile)
  val schemaV2 = try schemaSourceV2.mkString finally schemaSourceV2.close()

  val parserV2 = new Schema.Parser

  // Parse the schema
  val avroSchemaV2 = parserV2.parse(schemaV2)
  val schemaStrV2 = avroSchemaV2.toString


  val dataV2 = Seq(
    (1, "Alice", 28, "v2", "Lion"),
    (2, "Bob", 25, "v2", "Valencia"),
    (3, "Charlie", 30, "v2", "Madrid")
  )

  val dfV2 = dataV2.toDF("id", "name", "age", "version", "city")

  // Escribimos el DataFrame en formato Avro con el esquema V2
  dfV2.write
    .format("avro")
    .mode(SaveMode.Overwrite)
    .option("avroSchema", schemaStrV2)
    .save("data/avro/person_v2")

  // Leemos los datos en formato Avro con el esquema V2
  val dfReadV2 = spark.read
    .format("avro")
    // Esto es opcional, porque el esquema ya está en los datos
    // .option("avroSchema", schemaStrV2)
    .load("data/avro/person_v2")

  dfReadV2.show()

  // Podemos leer el esquema de los datos Avro
  val schemaReadV2 = dfReadV2.schema

  println(schemaReadV2)

  // Leemos ambos ficheros Avro con el esquema V2
  val dfReadV1 = spark.read
    .format("avro")
    .option("avroSchema", schemaStrV2)
    .load("data/avro/person_v1")

  dfReadV1.show()

  val dfReadV2_2 = spark.read
    .format("avro")
    .option("avroSchema", schemaStrV2)
    .load("data/avro/person_v2")

  dfReadV2_2.show()

}
