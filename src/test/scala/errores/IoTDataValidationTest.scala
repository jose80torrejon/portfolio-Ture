package eoi.de.examples
package errores

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import ScalaValidated._

class IoTDataValidationTest extends AnyFunSuite with Matchers {

  object MockIoTDataValidationConfig {
    val temperatureThreshold: Double = 20.0
    val errorMessage: String = "Temperature threshold exceeded"
  }

  object IoTDataValidation {
    def validateIoTData[T](data: InputData[T]): MyValidated[String, OutputData[T]] = {
      if (data.value == MockIoTDataValidationConfig.temperatureThreshold)
        MyInvalid(s"${data.description} ${MockIoTDataValidationConfig.errorMessage}")
      else
        MyValid(OutputData(data.value, data.description))
    }
  }

  test("validateIoTData validates data correctly when temperature is over the threshold") {
    val input = IoTMeasurement(21.0, "test")
    val result = IoTDataValidation.validateIoTData(input)

    //result shouldEqual MyInvalid("test Temperature threshold exceeded")
    result shouldEqual MyValid(OutputData(21.0, "test"))
  }

  test("validateIoTData validates data correctly when temperature is under the threshold") {
    var input = IoTMeasurement(19.0, "test")
    var result = IoTDataValidation.validateIoTData(input)

    result shouldEqual MyValid(OutputData(19.0, "test"))

    input = IoTMeasurement(-1, "test")
    result = IoTDataValidation.validateIoTData(input)
    result shouldEqual MyValid(OutputData(-1, "test"))
  }

  test("validateIoTData validates data correctly when temperature is equal to the threshold") {
    val input = IoTMeasurement(20.0, "test")
    val result = IoTDataValidation.validateIoTData(input)

    result shouldEqual MyInvalid("test Temperature threshold exceeded")
  }
}