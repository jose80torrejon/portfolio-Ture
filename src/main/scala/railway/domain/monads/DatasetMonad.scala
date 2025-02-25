
package railway.domain.monads

import implicits.Implicits._
import spark.{ErrorLevel, SparkSessionWrapper}

import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql._

/**
 * Case class as wrapper for DataFrame related operations.
 * @param df A DataFrame
 */
final case class DataFrameMonadV2(df: DataFrame) {

  /**
   * Transform data frame by applying the given function.
   * @param f A function that transforms a DataFrame to another DataFrame
   * @return Returns a new DataFrameMonadV2 instance with the transformed DataFrame
   */
  def transform(f: DataFrame => DataFrame): DataFrameMonadV2 = DataFrameMonadV2(f(df))

  /**
   * Convert DataFrame to Dataset of a given type.
   * @param spark Implicit Spark session
   * @tparam T The type of elements in Dataset
   * @return Returns a DatasetMonad instance with new Dataset
   */
  def asDataset[T : Encoder](implicit spark: SparkSession): DatasetMonad[T] = DatasetMonad(df.as[T])
}

/**
 * Case class as wrapper for Dataset related operations.
 * @param ds A Dataset
 * @tparam T The type of elements in Dataset
 */
final case class DatasetMonad[T](ds: Dataset[T]) {

  /**
   * Transform Dataset by applying the given function.
   * @param f A function that transforms a Dataset to another Dataset
   * @return Returns a new DatasetMonad instance with the transformed Dataset
   */
  def transform(f: Dataset[T] => Dataset[T]): DatasetMonad[T] = DatasetMonad(f(ds))

  /**
   * Convert Dataset to DataFrame.
   * @param spark Implicit Spark session
   * @return Returns a DataFrameMonadV2 instance with new DataFrame
   */
  def asDataFrame(implicit spark: SparkSession): DataFrameMonadV2 = DataFrameMonadV2(ds.toDF())
}

/**
 * Represents data with 'id', 'value', and 'timestamp'
 * @param id Unique identifier
 * @param value Numerical value
 * @param timestamp Timestamp string
 */
final case class Data(id: Long, value: Double, timestamp: String)

/**
 * Main application to demonstrate data transformations and conversions.
 */
object DataFrameToDataset extends App with SparkSessionWrapper {
  // implicit val spark: SparkSession = SparkSession.builder().appName("DataFrameToDataset").getOrCreate()
  private var sessionBuilder = createSparkSession withName "DataFrameToDataset"
  sessionBuilder = sessionBuilder withLogLevel ErrorLevel
  sessionBuilder = sessionBuilder withDriverCores 2
  sessionBuilder = sessionBuilder withDriverMemory 10.Gb
  sessionBuilder = sessionBuilder withExecutorCores 2
  sessionBuilder = sessionBuilder withExecutorMemory 10.Gb

  override implicit val spark: SparkSession = sessionBuilder.build
  import spark.implicits._

  // Generate a random DataFrame
  val df: DataFrame = generateRandomDataFrame(spark, 100)

  // Transform DataFrame
  val transformedDF: DataFrameMonadV2 = DataFrameMonadV2(df).transform(df => df.filter(col("value") > 1000))

  // Convert DataFrame to Dataset
  val ds: DatasetMonad[Data] = transformedDF.asDataset[Data]

  // Transform Dataset
  val transformedDS: DatasetMonad[Data] = ds.transform(ds => ds.filter(_.value > 1000))

  // Convert back to DataFrame
  val backToDF: DataFrameMonadV2 = transformedDS.asDataFrame

  // Show DataFrame
  backToDF.df.show()

  /**
   * Function to generate a random DataFrame with 'id', 'value', and 'timestamp' columns.
   * @param spark Implicit Spark session
   * @param numRows Number of rows in the DataFrame
   * @return Return DataFrame with random values
   */
  def generateRandomDataFrame(spark: SparkSession, numRows: Int): DataFrame = {
    val r = scala.util.Random
    val schema = List(
      StructField("id", LongType, nullable = false),
      StructField("value", DoubleType, nullable = false),
      StructField("timestamp", StringType, nullable = false)
    )
    val data: Seq[Row] = Seq.fill(numRows){
      Row(r.nextLong(), r.nextDouble() * 2000, java.time.LocalDateTime.now().toString)
    }
    val rdd = spark.sparkContext.parallelize(data)
    spark.createDataFrame(rdd, StructType(schema))
  }
}