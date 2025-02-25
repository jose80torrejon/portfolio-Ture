package eoi.de.examples
package columns

import com.github.mrpowers.spark.fast.tests.{ColumnComparer, DataFrameComparer}
import org.scalatest.funsuite.AnyFunSuite

class ColumnFunctionsTest extends AnyFunSuite with DataFrameComparer with ColumnComparer with SparkSessionTestWrapper {

  import spark.implicits._

  test("test lowercaseWithoutWhitespace") {
    val sourceDF = Seq(
      ("WHITE Space", "whitespace"),
      ("test", "test"),
      ("TesT Case", "testcase"),
    ).toDF("input", "expected")

    val resultDF = sourceDF.withColumn("result", ColumnFunctions.lowercaseWithoutWhitespace($"input"))

    assertColumnEquality(resultDF, "result", "expected")
  }

  test("test uppercaseWithoutWhitespace") {
    val sourceDF = Seq(
      ("WHITE Space", "WHITESPACE"),
      ("test", "TEST"),
      ("TesT Case", "TESTCASE"),
    ).toDF("input", "expected")
    val resultDF = sourceDF.withColumn("result", ColumnFunctions.uppercaseWithoutWhitespace($"input"))
    assertColumnEquality(resultDF, "result", "expected")
  }

  test("test removeSpecialCharacters") {
    val sourceDF = Seq(
      ("WHITE/Spa&ce", "WHITESpace"),
      ("t#e&s@t", "test"),
      ("T%e#s^T@ C&a*s(e", "TesTCase"),
    ).toDF("input", "expected")

    val resultDF = sourceDF.withColumn("result", ColumnFunctions.removeSpecialCharacters($"input"))

    assertColumnEquality(resultDF, "result", "expected")
  }
}
