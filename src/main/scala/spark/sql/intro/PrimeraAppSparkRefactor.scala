
package spark.sql.intro

import org.apache.spark.SparkConf
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.udf

object FirstAppSparkConf {
  // Local Spark Configuration
  val localSparkConf: SparkConf = new SparkConf().setAppName("FirstAppSparkConf") // Application Name
    .setMaster("local[*]") // Runtime Mode
    .set("spark.shuffle.partitions", "10") // Shuffle partitions
    .set("spark.driver.memory", "2g") // Driver Memory
    .set("spark.driver.cores", "1") // Driver Cores
    .set("spark.executor.memory", "2g") // Executor Memory
    .set("spark.executor.cores", "4") // Executor Cores
}

// Se generan los datos en el driver y luego se serializan y se envían a los executors
object DataGeneratorV0 {
  // Maximum generated age
  private val GENERATE_MAX_AGE: Int = 75

  // Function to generate name and age fields
  def generateData(numRecords: Int): Seq[(String, Int)] = {
    val data = (1 to numRecords).map { i =>
      val name = s"Name$i"
      val age = scala.util.Random.nextInt(GENERATE_MAX_AGE)
      (name, age)
    }
    data
  }
}

// Creamos los datos en los Executors
object DataGenerator {
  private val GENERATE_MAX_AGE: Int = 75
  def generateData(numRecords: Int, spark: SparkSession): DataFrame = {
    val rangeRdd = spark.sparkContext.parallelize(1 to numRecords)
    val dataRdd = rangeRdd.map { i =>
      val name = s"Name$i"
      val age = scala.util.Random.nextInt(GENERATE_MAX_AGE)
      (name, age)
    }
    spark.createDataFrame(dataRdd).toDF("name", "age")
  }
}

// Create an enum for the file format
sealed trait SupportedFileFormat
case object Parquet extends SupportedFileFormat
case object Orc extends SupportedFileFormat
case object Json extends SupportedFileFormat
case object Csv extends SupportedFileFormat

case class DataIO(spark: SparkSession) {
  import spark.implicits._

  // Minimum age for filtration
  private val MINIMUM_AGE: Int = 22
  val mode: SaveMode = SaveMode.Append

  def filterMalesOverMinimumAge(df: DataFrame): DataFrame =
    df.filter($"sex" === "Male").filter($"age" > MINIMUM_AGE)

  // Function to write males who are over the minimum age
  def writeMaleOverMinimumAge(
      df: DataFrame,
      path: String,
      mode: SaveMode,
      format: SupportedFileFormat = Parquet,
  ): Unit = {

    val malesOverMinimumAge = filterMalesOverMinimumAge(df)
    val dfw = malesOverMinimumAge.write.mode(mode)

    format match {
      case Parquet => dfw.option("compression", "SNAPPY")
          .parquet(path + ".parquet")
      case Orc => dfw.option("compression", "ZLIB").orc(path + ".orc")
      case Json => dfw.json(path + ".json")
      case Csv => dfw.option("header", "true").option("compression", "GZIP")
          .csv(path + ".csv")
      case _ => println("ERROR: Format not supported")
    }
  }
}

case class DataPrinter() {
  // Function to print DataFrame in console
  def display(df: DataFrame, displayName: String = "Dataframe:"): Unit = {
    println(String.format(
      "%s%s%s%s",
      Console.BOLD,
      Console.RED,
      "**** " + displayName + " **** ",
      Console.RESET,
    ))
    df.show(truncate = false)
  }
}

case class PeopleClassifier(spark: SparkSession) {
  import spark.implicits._

  // Constant array of sexes
  private val SEXES: Seq[String] = Seq("Undefined", "Female", "Male")

  // Helper function to classify people by name
  private def classifyByName(name: String): String =
    SEXES(name.last.toInt % SEXES.length)

  // Function to classify dataframe by adding 'sex' column
  def classify(df: DataFrame): DataFrame = {
    val classifyPeople: UserDefinedFunction = udf(classifyByName _)
    df.withColumn("sex", classifyPeople($"name"))
  }
}

// Application entry point
object FirstAppSparkRefactor extends App {

  // Instantiating SparkSession, PeopleClassifier, DataIO and DataPrinter
  val spark = SparkSession.builder().config(FirstAppSparkConf.localSparkConf)
    .getOrCreate()
  val peopleClassifier: PeopleClassifier = PeopleClassifier(spark)
  val dataIO: DataIO = DataIO(spark)
  val dataPrinter: DataPrinter = new DataPrinter

  // Setting log level
  spark.sparkContext.setLogLevel("ERROR")

  import spark.implicits._
  // Generating and Classifying data
  // val data = DataGeneratorV0.generateData(100000, spark)
  // val df = spark.createDataFrame(data).toDF("name", "age")

  val df = DataGenerator.generateData(100000000, spark)

  val withAddedSex = peopleClassifier.classify(df)

  // Calling display and writeMaleOverMinimumAge methods
  dataPrinter.display(withAddedSex, "People with withAddedSex")
  dataIO.writeMaleOverMinimumAge(
    withAddedSex,
    "malesOverMinimumAge.csv",
    SaveMode.Overwrite,
    Json,
  )

  // Stopping SparkSession
  spark.stop()
}
