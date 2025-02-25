package eoi.de.examples
package spark.sql

import org.apache.spark.sql.catalyst.InternalRow
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class CEExample02Test extends AnyFunSuite with Matchers {

  test("toLower method should convert string to lower case") {
    // We  need to send an InternalRow, but it doesn't have an impact on the result
    val data = InternalRow.empty

    val result = ExampleFunctions.toLower.eval(data).toString

    // Check the result
    result shouldEqual "hello world"
  }
}