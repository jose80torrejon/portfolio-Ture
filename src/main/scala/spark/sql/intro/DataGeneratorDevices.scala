
package spark.sql.intro

import java.time.Instant
import scala.util.Random

object DataGeneratorDevices {

  // tipos de dispositivos posibles
  val deviceTypes = List("thermostat", "moisture_sensor", "light_detector")

  // Generar un entero aleatorio entre un rango
  def generateRandomInt(start: Int, end: Int): Int = {
    start + Random.nextInt((end - start) + 1)
  }

  // Generar un dato de dispositivo de IoT
  def generateIotData(): String = {
    val deviceId = generateRandomInt(1, 1000)
    val deviceType = deviceTypes(Random.nextInt(deviceTypes.size))
    val timestamp = Instant.now.getEpochSecond
    val temperature = generateRandomInt(15, 35)
    val humidity = generateRandomInt(30, 70)
    
    s"Id del dispositivo: $deviceId, Tipo de dispositivo: $deviceType, Tiempo: $timestamp, Temperatura: $temperature, Humedad: $humidity"
  }

  def main(args: Array[String]): Unit = {
    for( _ <- 1 to 100) {
      println(generateIotData())
    }
  }
  
}
