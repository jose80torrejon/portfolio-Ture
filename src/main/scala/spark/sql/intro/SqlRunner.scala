
package spark.sql.intro

import org.apache.spark.sql.{DataFrame, SparkSession}

object SqlRunner {

  def runSqlFromString( query: String, sqlCount: Int, withDebug: Boolean = true, componentName: String = " ")(spark: SparkSession): DataFrame = {
    spark.conf.set("spark.sql.execution.debug", withDebug)
    spark.sparkContext.setLocalProperty("callSite.short", "SQL_" + sqlCount.toString)
    spark.sparkContext.setLocalProperty("callSite.long", "SQL -> " + query)
    spark.sparkContext.setJobDescription(s"$componentName $sqlCount: $query")
    spark.sql(query)
  }
}

import org.apache.spark.sql.{SparkSession, DataFrame}

object TestSqlRunner extends App {
  val spark: SparkSession = SparkSession.builder()
    .appName("SqlRunnerTest")
    .master("local[*]")
    .getOrCreate()
  import spark.implicits._

  // Creating dummy DataFrame to register as temp table
  val df: DataFrame = Seq(
    (1, "John"),
    (2, "Martin"),
    (3, "Linda")
  ).toDF("id", "name")

  df.createOrReplaceTempView("People")

  // Example query
  private val query: String = "SELECT * FROM People"
  private val query2: String = "SELECT id FROM People where name='Martin'"
  private val componentName: String = "TestSqlRunner"

  SqlRunner.runSqlFromString(query = query, sqlCount = 1,  componentName = componentName)(spark)
    .show(truncate = false)

  SqlRunner.runSqlFromString(query2, 2,  componentName = componentName)(spark)
    .show(truncate = false)

  Thread.sleep(200000)
  // Remember to stop the Spark session
  spark.stop()
}
