

import iotexample.IotDomain.{Humidity, Pressure, SensorData, Temperature}

import scala.util.matching.Regex

package object iotexample {

  // Representa el codigo de error por el que un dato no es válido.
  sealed trait InvalidDataReason

  sealed trait CommonIdReason extends InvalidDataReason

  sealed trait TemperatureIdReason extends InvalidDataReason

  sealed trait HumidityIdReason extends InvalidDataReason

  sealed trait PressureIdReason extends InvalidDataReason


  case object SensorId extends InvalidDataReason with CommonIdReason

  case object EventTime extends InvalidDataReason with CommonIdReason

  case object SensorType extends InvalidDataReason with CommonIdReason

  case object TemperatureRange extends InvalidDataReason with TemperatureIdReason

  case object HumidityRange extends InvalidDataReason with HumidityIdReason

  case object PressureRange extends InvalidDataReason with PressureIdReason

  case object ContainsTemperature extends InvalidDataReason with TemperatureIdReason

  case object ContainsHumidity extends InvalidDataReason with HumidityIdReason

  case object ContainsPressure extends InvalidDataReason with PressureIdReason

  case object ContainsData extends InvalidDataReason with CommonIdReason

  // Representa el motivo por el que un dato no es válido.
  sealed trait Reason {
    def message: String
  }

  case class SensorIdReason(message: String) extends Reason

  case class EventTimeReason(message: String) extends Reason

  case class SensorTypeReason(message: String) extends Reason

  case class TemperatureReason(message: String) extends Reason

  case class HumidityReason(message: String) extends Reason

  case class PressureReason(message: String) extends Reason

  case class ContainsTemperatureReason(message: String) extends Reason

  case class ContainsHumidityReason(message: String) extends Reason

  case class ContainsPressureReason(message: String) extends Reason

  case class ContainsDataReason(message: String) extends Reason

  // Necesitamos una estructura para tener el código de error y el motivo por el que no es válido un dato.
  // Usamos un Map para almacenar el código de error y el motivo.
  case class InvalidData(invalidDataId: InvalidDataReason, invalidDataReason: Reason)

  object IotDomain {

    sealed trait SensorType {
      val min: Double
      val max: Double

      def typeName: String
    }

    case object Temperature extends SensorType {
      override val min: Double = -10
      override val max: Double = 50
      override val typeName: String = "validTemperature-device"
    }

    case object Humidity extends SensorType {
      override val min: Double = 0
      override val max: Double = 100
      override val typeName: String = "validHumidity-device"
    }


    case object Pressure extends SensorType {
      override val min: Double = 800
      override val max: Double = 1200
      override val typeName: String = "validPressure-device"
    }

    // Definición de un caso de clase para representar los datos de un dispositivo IoT.
    case class SensorData(eventTime: String,
                          sensorId: String,
                          value: Double,
                          valid: Option[Boolean] = Some(false),
                          invalidReason: Option[InvalidDataReason] = None,
                          sensorType: String)

    class SensorDataBuilder {
      private var eventTime: String = ""
      private var sensorId: String = ""
      private var value: Double = 0.0
      private var valid: Option[Boolean] = Some(false)
      private var invalidReason: Option[InvalidDataReason] = None
      private var sensorType: String = Temperature.typeName

      def withEventTime(eventTime: String): SensorDataBuilder = {
        this.eventTime = eventTime
        this
      }

      def withSensorId(sensorId: String): SensorDataBuilder = {
        this.sensorId = sensorId
        this
      }

      def withValue(value: Double): SensorDataBuilder = {
        this.value = value
        this
      }

      def withValid(valid: Option[Boolean]): SensorDataBuilder = {
        this.valid = valid
        this
      }

      def withInvalidReason(invalidReason: Option[InvalidDataReason]): SensorDataBuilder = {
        this.invalidReason = invalidReason
        this
      }

      def withSensorType(sensorType: SensorType): SensorDataBuilder = {
        this.sensorType = sensorType.typeName
        this
      }

      def build(): SensorData = SensorData(eventTime, sensorId, value, valid, invalidReason, sensorType)
    }
  }

  object IotDataValidations {
    // Validar para obtener el mensaje de error

    // Se desea validar que todos los datos cumplen las siguientes condiciones:
    // 1. La temperatura debe estar entre -10 y 50 grados Celsius.
    // 2. La humedad debe estar entre 0 y 100 %.
    // 3. La presión debe estar entre 800 y 1200 hPa.
    // 4. El tipo de sensor debe ser uno de los siguientes: "validTemperature-device", "validHumidity-device", "validPressure-device".
    // 5. El sensorId debe tener un formato válido (por ejemplo, "sensor-1", "sensor-2", etc.).
    // 6. El eventTime debe tener un formato válido (por ejemplo, "2021-01-01T12:00:00Z").
    // Se desea obtener un mensaje de error si algún dato no cumple alguna de las condiciones anteriores.
    // Si el sensor es de tipo "validTemperature-device", debe contener la temperatura y esta ser valida.
    // Si el sensor es de tipo "validHumidity-device", debe contener la humedad y esta ser valida.
    // Si el sensor es de tipo "validPressure-device", debe contener la presión y esta ser valida.

    object SensorDataValidations {
      // Función que valida si un sensorId tiene un formato válido
      // El formato válido es "sensor-n" donde n es un número entero mayor o igual a 1.
      // \\d+ es una expresión regular que significa "uno o más dígitos".
      val sensorIdPattern: Regex = "sensor-\\d+".r

      def validateSensorId(sensorId: String): Boolean = sensorId match {
        case sensorIdPattern() => true
        case _ => false
      }

      def validateEventTime(eventTime: String): Boolean = {
        // Se utiliza un bloque try-catch para capturar la excepción si el formato no es válido.
        try {
          // Se intenta parsear la fecha con el formato "yyyy-MM-dd'T'HH:mm:ss'Z'".
          java.time.OffsetDateTime.parse(eventTime)
          true
        } catch {
          case _: java.time.format.DateTimeParseException => false
        }
      }

      // Usando currying
      private def validateMeassure(sensorData: SensorData): Boolean = sensorData.value >= sensorData.sensorType.min && sensorData.value <= sensorData.sensorType.max

      def validateSensorType(sensorData: SensorData): Boolean = Seq(Temperature.typeName, Pressure.typeName, Humidity.typeName).contains(sensorData.sensorType)

      private def validateContainsTemperature(sensorData: SensorData): Boolean = sensorData.sensorType == Temperature.typeName && validateMeassure(sensorData)

      private def validateContainsHumidity(sensorData: SensorData): Boolean = sensorData.sensorType == Humidity.typeName && validateMeassure(sensorData)

      private def validateContainsPressure(sensorData: SensorData): Boolean = sensorData.sensorType == Pressure.typeName && validateMeassure(sensorData)

      def validateContaintsData(sensorData: SensorData): Boolean = validateContainsTemperature(sensorData) || validateContainsHumidity(sensorData) || validateContainsPressure(sensorData)

      // Modificar las funciones anteriores para que nos digan porque no es válido un registro
      def validateSensorIdReason(sensorId: String): Option[InvalidData] = sensorId match {
        case sensorIdPattern() => None
        case _ => Some(InvalidData(SensorId, SensorIdReason("El sensorId no tiene un formato válido")))
      }

      def validateEventTimeReason(eventTime: String): Option[InvalidData] = {
        try {
          java.time.OffsetDateTime.parse(eventTime)
          None
        } catch {
          case _: java.time.format.DateTimeParseException => Some(InvalidData(EventTime, EventTimeReason("El eventTime no tiene un formato válido")))
        }
      }

      def validateTemperatureReason(data: SensorData): Option[InvalidData] = {
        if (validateMeassure(data)) None
        else Some(InvalidData(TemperatureRange, TemperatureReason("La temperatura no está en el rango válido")))
      }

      def validateHumidityReason(data: SensorData): Option[InvalidData] = {
        if (validateMeassure(data)) None
        else Some(InvalidData(HumidityRange, HumidityReason("La humedad no está en el rango válido")))
      }

      def validatePressureReason(data: SensorData): Option[InvalidData] = {
        if (validateMeassure(data)) None
        else Some(InvalidData(PressureRange, PressureReason("La presión no está en el rango válido")))
      }

      def validateSensorTypeReason(data: SensorData): Option[InvalidData] = {
        if (validateSensorType(data)) None
        else Some(InvalidData(SensorType, SensorTypeReason("El tipo de sensor no es válido")))
      }

      def validateContainsTemperatureReason(data: SensorData): Option[InvalidData] = {
        if (validateContainsTemperature(data)) None
        else Some(InvalidData(ContainsTemperature, ContainsTemperatureReason("El sensor no contiene la temperatura o esta no es válida")))
      }

      def validateContainsHumidityReason(data: SensorData): Option[InvalidData] = {
        if (validateContainsHumidity(data)) None
        else Some(InvalidData(ContainsHumidity, ContainsHumidityReason("El sensor no contiene la humedad o esta no es válida")))
      }

      def validateContainsPressureReason(data: SensorData): Option[InvalidData] = {
        if (validateContainsPressure(data)) None
        else Some(InvalidData(ContainsPressure, ContainsPressureReason("El sensor no contiene la presión o esta no es válida")))
      }

      def validateContaintsDataReason(data: SensorData): Option[InvalidData] = {
        if (validateContainsTemperature(data) || validateContainsHumidity(data) || validateContainsPressure(data)) None
        else Some(InvalidData(ContainsData, ContainsDataReason("El sensor no contiene los datos necesarios o estos no son válidos")))
      }

    }
  }

}
