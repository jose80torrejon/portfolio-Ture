package eoi.de.examples
package spark.sql

import org.apache.spark.sql.{DataFrame, Row, SparkSession, TypedColumn}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite

class ExampleFunctionsUDA01Test extends AnyFunSuite with SparkSessionTestWrapper {


  ignore("Test the Percentile Aggregator") {
    spark.sparkContext.setLogLevel("ERROR")
    import spark.implicits._

    val data = Seq((1.0, 2), (3.0, 3), (5.0, 4)).toDF("value", "count")
    data.as[ValueWithCount].show(false)
    val percentiles: TypedColumn[ValueWithCount, Map[Int, Double]] = ExampleFunctionsUDA01.Percentile.toColumn.name("percentiles")
    val percentilesDF: DataFrame = data.as[ValueWithCount].groupByKey(_ => true).agg(percentiles).select("percentiles")

    // Define expected result
    val expected: Map[Int, Double] = Map(1 -> 0.2, 3 -> 0.3, 5 -> 0.5)

    import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder
    import org.apache.spark.sql.catalyst.expressions.UnsafeRow

    // Collect the result to the driver
    val rows: Array[Row] = percentilesDF.collect()

    println("[DEBUG] Percentiles DataFrame: ")
    rows.foreach(println)


    // Convert the DataFrame to a Dataset[Array[Byte]]
    val percentilesDS = percentilesDF.as[Array[Byte]]
    percentilesDS.show()
    // Create a deserializer function
    val deserializer = ExampleFunctionsUDA01.Percentile.outputEncoder.asInstanceOf[ExpressionEncoder[Map[Int, Double]]].resolveAndBind().createDeserializer()

    // Use the map method to convert each Array[Byte] to Map[Int, Double]
    val mappedDS = percentilesDS.map { bytes =>
      val unsafeRow = new UnsafeRow(ExampleFunctionsUDA01.Percentile.outputEncoder.schema.length)
      println(ExampleFunctionsUDA01.Percentile.outputEncoder.schema.length)
      println(ExampleFunctionsUDA01.Percentile.outputEncoder.schema)

      println(s"[DEBUG] -- unsafeRow: $unsafeRow")
      println(s"[DEBUG] -- bytes: ${bytes.mkString("Array(", ", ", ")")}")
      unsafeRow.pointTo(bytes, bytes.length)
      println(s"[DEBUG] -- unsafeRow2: $unsafeRow")

      deserializer(unsafeRow)
    }
    mappedDS.show()
    val res = mappedDS.collect()
    println(s"[DEBUG] -- Percentiles Dataset: ${res.mkString("Array(", ", ", ")")}")
    // Collect the result to the driver
    val mapped: Array[Map[Int, Double]] = mappedDS.collect()

    // Check the result
    assert(mapped(0) == expected)
  }
}