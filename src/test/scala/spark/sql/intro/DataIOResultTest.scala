package eoi.de.examples
package spark.sql.intro

import org.apache.spark.sql.functions.col
import org.scalatest.funsuite.AnyFunSuite

class DataIOResultTest extends AnyFunSuite with SparkSessionTestWrapper {
  
  test("verify filterMalesOverMinimumAge method") {
    import spark.implicits._

    val data = Seq(("Male",23),("Female",23),("Male",21))
    val df = spark.createDataFrame(data).toDF("sex","age")
    val dataIO = DataIO(spark)
    val result = dataIO.filterMalesOverMinimumAge(df)
    assert(result.count() == 1)
    assert(result.filter(col("sex") === "Male").count() == 1)
  }

  test("verify classify method") {
    import spark.implicits._

    val data = Seq(("Name1",23),("Name2",24),("Name3",25))
    val df = spark.createDataFrame(data).toDF("name","age")
    val peopleClassifier = PeopleClassifier(spark)
    val withAddedSex = peopleClassifier.classify(df)
    assert(withAddedSex.columns.contains("sex"))
  }
}