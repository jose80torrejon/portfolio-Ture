package eoi.de.examples
package spark.sql

import org.apache.spark.sql.{Row, SparkSession}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite

class UDFExample01Test extends AnyFunSuite with SparkSessionTestWrapper {

  test("wordCount UDF test") {
    import spark.implicits._
    val data = Seq(("1", "Hello world"), ("2", "I love Spark"))

    val df = spark.createDataset(spark.sparkContext.parallelize(data)).toDF("id", "text")

    import UDFExample01Functions._
    val resultDf = applyWordCount(df)
    val resultData = resultDf.collect()

    // validate the results
    val expectedData1 = Map("hello" -> 1, "world" -> 1).map { case (k, v) => k.toLowerCase -> v }
    val expectedData2 = Map("i" -> 1, "love" -> 1, "spark" -> 1).map { case (k, v) => k.toLowerCase -> v }

    assert(resultData.contains(Row("1", "Hello world", expectedData1)))
    assert(resultData.contains(Row("2", "I love Spark", expectedData2)))
  }

}