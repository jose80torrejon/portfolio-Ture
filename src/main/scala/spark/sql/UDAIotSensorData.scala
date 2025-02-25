
package spark.sql

import org.apache.spark.sql.{Encoder, Encoders, SparkSession, TypedColumn}
import org.apache.spark.sql.expressions.Aggregator

// Un ejemplo de UDA que calcula la media de los valores de un sensor en un rango de tiempo.
// Para ello, se usa un UDA que toma como entrada un objeto que contiene el valor del sensor y la marca de tiempo, y devuelve la media de los valores en un rango de tiempo determinado.

// Se define un objeto case class para representar los datos del sensor
case class SensorData(sensorID: String, value: Double, timestamp: Long)

// Se define un objeto case class para representar el buffer de datos, que contiene la suma de los valores y el número de valores en el buffer
case class AggregateBuffer(sum: Double, count: Int, average: Double)

object IoTFunctions {
  // Aggregator: SensorData -> AggregateBuffer -> Double
  object AverageSensorValue extends Aggregator[SensorData, AggregateBuffer, Double] {

    // Inicializa el buffer
    def zero: AggregateBuffer = AggregateBuffer(0.0, 0, 0.0)

    // Reduce los datos: suma el valor y aumenta el contador en 1
    // Por ejemplo, si el buffer contiene (10.0, 2) y el dato es (5.0, 1), el buffer resultante será (15.0, 3)
    def reduce(bufferData: AggregateBuffer, data: SensorData): AggregateBuffer =
      AggregateBuffer(bufferData.sum + data.value, bufferData.count + 1, (bufferData.sum + data.value) / (bufferData.count + 1))

    // Combina dos buffers: por ejemplo, si el buffer 1 contiene (10.0, 2) y el buffer 2 contiene (5.0, 1), el buffer resultante será (15.0, 3)
    def merge(bufferData1: AggregateBuffer, bufferData2: AggregateBuffer): AggregateBuffer = {
      val newSum = bufferData1.sum + bufferData2.sum
      val newCount = bufferData1.count + bufferData2.count
      AggregateBuffer(newSum, newCount, newSum / newCount)
    }
    // Calcula la media de los valores en el buffer
    // Por ejemplo, si el buffer contiene (15.0, 3), la media será 15.0 / 3 = 5.0
    def finish(finalBufferData: AggregateBuffer): Double = finalBufferData.average

    // Codificador para el buffer de datos  (AggregateBuffer)
    def bufferEncoder: Encoder[AggregateBuffer] = Encoders.product[AggregateBuffer]

    // Codificador para el resultado de la UDA (Double)
    def outputEncoder: Encoder[Double] = Encoders.scalaDouble
  }
}

object IoTExample extends App {
  val spark = SparkSession.builder().appName("IoTExample")
    .master("local[*]").getOrCreate()
  spark.sparkContext.setLogLevel("ERROR")

  import spark.implicits._

  val data = Seq(
    SensorData("sensor-1", 21.0, 1111L),
    SensorData("sensor-1", 26.0, 1112L),
    SensorData("sensor-1", 24.0, 1113L),
    SensorData("sensor-1", 25.0, 1114L),
    SensorData("sensor-1", 302.0, 1115L),
    SensorData("sensor-2", 23.0, 1111L),
    SensorData("sensor-2", 27.0, 1112L),
    SensorData("sensor-2", 28.0, 1113L),
    SensorData("sensor-2", 29.0, 1114L),
    SensorData("sensor-2", 100.0, 1115L)
  ).toDF("sensorId", "value", "timestamp")

  data.as[SensorData].show(false)

  // Se convierte la UDA en un TypedColumn para poder usarla en un DataFrame
  // Una TypedColumn es una columna que tiene un tipo de datos asociado

  import spark.implicits._

  // Etapa 1: Calcular la media
  val averageSensorValue: TypedColumn[SensorData, Double] = IoTFunctions.AverageSensorValue.toColumn.name("averageSensorValue")
  val averageSensorValueDF = data.as[SensorData].groupByKey(_.sensorID).agg(averageSensorValue).toDF("sensorID", "averageSensorValue")

  // Etapa 2: Unir con datos originales y filtrar
  val joinedDF = data.join(averageSensorValueDF, "sensorID")

  // Filtrar los datos que sean mayores a 1.2 veces la media
  val filteredDF = joinedDF.filter($"value" <= ($"averageSensorValue"*1.2))

  // Etapa 3: Calcular nueva media con los datos filtrados
  val filteredDataDS = filteredDF.as[SensorData]
  val finalAverageSensorValueDF = filteredDataDS.groupByKey(_.sensorID).agg(averageSensorValue)
  finalAverageSensorValueDF.show()
}

// Mismo ejemplo pero con Catalyst Expression
// Se define una expresión personalizada que calcula la media de los valores de un sensor en un rango de tiempo.



// Registra la expresión personalizada en el contexto de Spark
object IoTExampleWithCE extends App {
  val spark = SparkSession.builder().appName("IoTExampleWithCE")
    .master("local[*]").getOrCreate()
  spark.sparkContext.setLogLevel("ERROR")

  import spark.implicits._

  val data = Seq(
    SensorData("sensor-1", 21.0, 1111L),
    SensorData("sensor-1", 26.0, 1112L),
    SensorData("sensor-1", 24.0, 1113L),
    SensorData("sensor-1", 25.0, 1114L),
    SensorData("sensor-1", 302.0, 1115L),
    SensorData("sensor-2", 23.0, 1111L),
    SensorData("sensor-2", 27.0, 1112L),
    SensorData("sensor-2", 28.0, 1113L),
    SensorData("sensor-2", 29.0, 1114L),
    SensorData("sensor-2", 100.0, 1115L)
  )

  // Se registra la expresión personalizada en el contexto de Spark
  //spark.sessionState.functionRegistry.registerFunction("averageSensorValue", AverageSensorValueExpression)

  // Se crea un DataFrame a partir de los datos de los sensores
  val dataDF = data.toDF("sensorID", "value", "timestamp")

}


