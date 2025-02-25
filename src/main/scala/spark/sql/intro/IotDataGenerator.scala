
package spark.sql.intro

import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

import java.time.Instant
import scala.util.Random

object IotDataGenerator {
  val IotJsonDataPath = "data/datagen/iot_data.json"

  val ValorMinimoTemperatura: Double = -20
  val ValorMaximoTemperatura: Double = 50
  val ValorMinimoHumedad: Double = 0
  val ValorMaximoHumedad: Double = 100

  case class IotData(deviceId: Int, deviceType: String, timestamp: Long, temperature: Double, humidity: Double)

  val deviceTypes = List("thermostat", "moisture_sensor", "light_detector")

  // Genera un entero aleatorio entre un rango
  def generateRandomInt(start: Int, end: Int): Int = {
    start + Random.nextInt((end - start) + 1)
  }

  // Genera un dato de tipo Double aleatorio entre un rango
  private def generateRandomDouble(start: Double, end: Double): Double = {
    start + Random.nextDouble() * (end - start)
  }

  // Genera un dato de dispositivo de IoT usando la case class IotData y la función generación de enteros aleatorios
  def generateIotData(): IotData = {
    val deviceId = generateRandomInt(1, 1000)
    val deviceType = deviceTypes(Random.nextInt(deviceTypes.size))
    val timestamp = Instant.now.getEpochSecond
    val temperature = generateRandomDouble(ValorMinimoTemperatura, ValorMaximoTemperatura)
    val humidity    = generateRandomDouble(ValorMinimoHumedad, ValorMaximoHumedad)

    IotData(deviceId, deviceType, timestamp, temperature, humidity)
  }

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("IoT Data Generator")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    import spark.implicits._

    val iotDataSeq: Seq[IotData] = (1 to 10000).map(_ => generateIotData())
    val iotDataDF: DataFrame = iotDataSeq.toDF()
    iotDataDF
      // Coalesce reduce el número de particiones a 2
      .coalesce(2)
      .write
      // Guarda los datos en formato JSON en la carpeta data/datagen/iot_data.json
      // Si la carpeta ya existe, sobreescribe los datos, si no existe la crea
      // - SaveMode.Overwrite: Sobreescribe los datos
      // - SaveMode.Append: Añade los datos
      // - SaveMode.ErrorIfExists: Error si ya existe
      .mode(SaveMode.Overwrite)
      .json(IotJsonDataPath)

    val dfJson = spark.read.json(IotJsonDataPath)
    dfJson.show()
    dfJson.printSchema()
    println(dfJson.count())
    spark.stop()

  }
}