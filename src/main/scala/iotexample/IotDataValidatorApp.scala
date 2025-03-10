
package iotexample

import iotexample.IotDataValidations.SensorDataValidations._
import iotexample.IotDomain._

import scala.Console.{BOLD, GREEN, RED, RESET}
import scala.collection.parallel.CollectionConverters.ImmutableIterableIsParallelizable
import scala.collection.parallel.immutable.ParIterable


object IotDataValidatorApp extends App {

  // Ejemplo de validación datos de IoT usando forAll y otras que sean necesarias
  // Datos de ejemplo
  val sensorDataBuilder = new SensorDataBuilder()
  // Temperature
  val data1 = sensorDataBuilder.withEventTime("2021-01-01T12:01:00Z").withSensorId("sensor-1").withValid(Some(false)).withValue(22.21).withSensorType(Temperature).build()
  val data2 = sensorDataBuilder.withEventTime("2021-01-01T12:02:00Z").withSensorId("sensor-2").withValue(22.22).withSensorType(Temperature).build()
  val data3 = sensorDataBuilder.withEventTime("2021-01-01T12:03:00Z").withSensorId("sensor-3").withValue(22.23).withSensorType(Temperature).build()
  val data4 = sensorDataBuilder.withEventTime("2021-01-01T12:04:00Z").withValid(Some(false)).withSensorId("sensor-4").withValue(22.24).withSensorType(Temperature).build()

  // Humidity
  val data5 = sensorDataBuilder.withEventTime("2021-01-01T12:05:00Z").withSensorId("sensor-5").withValue(122.25).withSensorType(Humidity).build()
  val data6 = sensorDataBuilder.withEventTime("2021-01-01T12:06:00Z").withSensorId("sensor-6").withValue(92.26).withSensorType(Humidity).build()
  val data7 = sensorDataBuilder.withEventTime("2021-01-01T12:07:00Z").withSensorId("sensor-7").withValue(29.27).withSensorType(Humidity).build()
  // Pressure
  val data8 = sensorDataBuilder.withEventTime("2021-01-01T12:08:00Z").withSensorId("sensor-8").withValue(122.28).withSensorType(Pressure).build()
  val data9 = sensorDataBuilder.withEventTime("2021-01-01T12:09:00Z").withSensorId("sensor-9").withValue(92.29).withSensorType(Pressure).build()
  val data10 = sensorDataBuilder.withEventTime("2021-01-01T12:10:00Z").withSensorId("sensor-10").withValue(29.20).withSensorType(Pressure).build()

  val numData = 2000000
  val dataset: Seq[SensorData] = List.fill(numData)(data1) ++ List.fill(numData)(data2) ++
    List.fill(numData)(data3) ++ List.fill(numData)(data4) ++ List.fill(numData)(data5) ++
    List.fill(numData)(data6) ++ List.fill(numData)(data7) ++ List.fill(numData)(data8) ++
    List.fill(numData)(data9) ++ List.fill(numData)(data10)

  val parDataset = dataset.par
  Console.println(BOLD + "dataset.size: " + (dataset.size / 1000000) + " M" + RESET)
  val numCores = Runtime.getRuntime.availableProcessors()
  Console.println(BOLD + "numCores: " + numCores + RESET)

  // Se valida si todos los datos cumplen las condiciones.
  lazy val allDataValid = parDataset.forall { data =>
    validateSensorId(data.sensorId) &&
      validateEventTime(data.eventTime) &&
      validateSensorType(data) &&
      validateContaintsData(data)
  }

  // Tiempo de ejecución
  val startTime = System.currentTimeMillis()
  // Ejemplo del uso de partition para separar los datos válidos de los inválidos
  lazy val (validData, invalidData) = parDataset.partition { data =>
    validateSensorId(data.sensorId) &&
      validateEventTime(data.eventTime) &&
      validateSensorType(data) &&
      validateContaintsData(data)
  }
  Console.println(s"allDataValid: $allDataValid")

  println()

  // Ejemplo de validación de datos inválidos

  private def invalidDataReason = invalidData.map { data =>
    val sensorIdReason: Option[InvalidData] = validateSensorIdReason(data.sensorId)
    val eventTimeReason: Option[InvalidData] = validateEventTimeReason(data.eventTime)
    val sensorTypeReason: Option[InvalidData] = validateSensorTypeReason(data)
    val temperatureReason: Option[InvalidData] = validateTemperatureReason(data)
    val humidityReason: Option[InvalidData] = validateHumidityReason(data)
    val pressureReason: Option[InvalidData] = validatePressureReason(data)
    val containsTemperatureReason: Option[InvalidData] = validateContainsTemperatureReason(data)
    val containsHumidityReason: Option[InvalidData] = validateContainsHumidityReason(data)
    val containsPressureReason: Option[InvalidData] = validateContainsPressureReason(data)
    val containsDataReason: Option[InvalidData] = validateContaintsDataReason(data)

    (data, sensorIdReason, eventTimeReason, sensorTypeReason, temperatureReason, humidityReason, pressureReason, containsTemperatureReason, containsHumidityReason, containsPressureReason, containsDataReason)
  }

  invalidDataReason.take(10).foreach(invalidData => println(RED + s"invalidData: $invalidData" + RESET))

  // Crear tres colecciones distintas con los datos de temperatura, humedad y presión que han pasado la validación
  val filtroTemperatura: SensorData => Boolean = (data: SensorData) => data.sensorType == Temperature.typeName
  val filtroHumedad: SensorData => Boolean = (data: SensorData) => data.sensorType == Humidity.typeName
  val filtroPresion: SensorData => Boolean = (data: SensorData) => data.sensorType == Pressure.typeName

  val validTemperatureData: ParIterable[SensorData] = validData.filter(filtroTemperatura).take(10)
  val validHumidityData: ParIterable[SensorData] = validData.filter(filtroHumedad).take(10)
  val validPressureData: ParIterable[SensorData] = validData.filter(filtroPresion).take(10)


  Console.println(GREEN + s"validTemperatureData: $validTemperatureData" + RESET)
  Console.println(GREEN + s"validHumidityData: $validHumidityData" + RESET)
  Console.println(GREEN + s"validPressureData: $validPressureData" + RESET)

  val endTime = System.currentTimeMillis()
  Console.println(BOLD + s"Tiempo de ejecución: ${endTime - startTime} ms" + RESET)

  System.exit(0)
}
