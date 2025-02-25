
package railway.domain.monads

import org.apache.spark.sql.{DataFrame, Dataset, Encoder, SparkSession}
import org.apache.spark.sql.functions.{col, sum}

/**
 *
 * Case class to hold DataFrame and a message
 * This case class is used to create a Monad for DataFrame
 * The Monad is created using the DataFrameWithContext case class
 */
final case class DataFrameWithContext(df: DataFrame, message: String)

/**
 *
 * Monad for DataFrame
* This Monad is used to chain operations on DataFrames
* It also provides utility functions to display DataFrame statistics, debug information, etc.
* The Monad is created using the DataFrameWithContext case class
* The DataFrameWithContext case class contains the DataFrame and a message
* The message is used to identify the DataFrame in the logs
* The DataFrameMonad provides map and flatMap functions to chain operations on DataFrames
 */
final case class DataFrameMonad(dfwc: DataFrameWithContext) {
  def map(f: DataFrame => DataFrame): DataFrameMonad =
    DataFrameMonad(DataFrameWithContext(f(dfwc.df), dfwc.message))

  def flatMap(f: DataFrame => DataFrameMonad): DataFrameMonad =
    f(dfwc.df)

  private def printContext(newMessage: String = dfwc.message): Unit = {
    val delimiterChar : String = "*"
    val delimiterLength  : Int = 60
    val indentationSpaces: Int = 5
    println(Console.BLUE + (delimiterChar * delimiterLength))
    println(" " * indentationSpaces + newMessage)
    println(Console.BLUE + (delimiterChar * delimiterLength) + Console.RESET)
  }

  private def displayWith(printFunction: DataFrame => Unit, newMessage: String = dfwc.message): Unit = {
    printContext(newMessage)
    printFunction(dfwc.df)
  }

  def displayWithStatistics(): Unit = displayWith(df => df.describe().show(), s"${dfwc.message} [Statistics]")
  def displayWithDebug(): Unit = displayWith(df => df.explain(), s"${dfwc.message} [Debug]")

  def displayAllDetails(showColumnDetails: Boolean = false, asMarkdown: Boolean = true): Unit = {
    displayWith(df => df.show())
    displayWithStatistics()
    displayWithDebug()
    displayWithCount()
    displayNullCounts()
    if (showColumnDetails) {
      dfwc.df.columns.foreach { column =>
        displayWithColumnStatistics(column)
        displayDistinctValues(column)
      }
    }
    if (asMarkdown) {
      println(toMarkdown(dfwc.df.limit(10)))
    }
  }

  def displayWithCount(): Unit = displayWith(df => println(df.count()), s"${dfwc.message} [Count]")

  def displayWithColumnStatistics(columnName: String): Unit =
    displayWith(df => df.describe(columnName).show(), s"${dfwc.message} [Column Statistics for $columnName]")

  def displayDistinctValues(columnName: String): Unit =
    displayWith(df => df.select(columnName).distinct().show(), s"${dfwc.message} [Distinct Values for $columnName]")

  def displayNullCounts(): Unit =
    displayWith(df => df.select(df.columns.map(c => sum(col(c).isNull.cast("int")).alias(c)): _*).show(),
      s"${dfwc.message} [Null Counts]")

  def displayWithCustom(printFunction: DataFrame => Unit, newMessage: String = dfwc.message): Unit =
    displayWith(printFunction, newMessage)

  def getDF: DataFrame = dfwc.df
  def getMessage: String = dfwc.message

  def toDS[T: Encoder]: Dataset[T] = dfwc.df.as[T]

  def toDF: DataFrame = dfwc.df

  def toMonad(newMessage: String): DataFrameMonad = DataFrameMonad(DataFrameWithContext(dfwc.df, newMessage))

  def toMonad: DataFrameMonad = DataFrameMonad(dfwc)

  def mapToDF(f: DataFrame => DataFrame): DataFrameMonad = DataFrameMonad(DataFrameWithContext(f(dfwc.df), dfwc.message))

  def flatMapToDF(f: DataFrame => DataFrameMonad): DataFrameMonad = f(dfwc.df).toMonad(dfwc.message)

  def flatMapToMonad(f: DataFrame => DataFrameMonad): DataFrameMonad = f(dfwc.df)

  def flatMapToMonad(newMessage: String)(f: DataFrame => DataFrameMonad): DataFrameMonad = f(dfwc.df).toMonad(newMessage)

  def flatMapToDF(newMessage: String)(f: DataFrame => DataFrameMonad): DataFrameMonad = f(dfwc.df).toMonad(newMessage)

  def toMarkdown(dataFrame: DataFrame): String = {
    val sb = new StringBuilder
    sb.append("|")
    sb.append(dataFrame.columns.mkString("|"))
    sb.append("|\n")
    sb.append("|")
    sb.append(dataFrame.columns.map(_ => "---").mkString("|"))
    sb.append("|\n")
    dataFrame.collect().foreach { row =>
      sb.append("|")
      sb.append(row.toSeq.mkString("|"))
      sb.append("|\n")
    }
    sb.toString()
  }

}

object DataFrameMonadEnhancements {
  implicit class DataFrameOps(df: DataFrame) {
    def toMonad(message: String): DataFrameMonad = DataFrameMonad(DataFrameWithContext(df, message))
  }
}

// Create App to test the DataFrameMonad
object DataFrameMonadApp extends App {
  import DataFrameMonadEnhancements._

  val spark: SparkSession = SparkSession.builder()
    .appName("DataFrameMonadApp")
    .master("local[*]")
    .getOrCreate()

  spark.sparkContext.setLogLevel("ERROR")


  import spark.implicits._

  val df: DataFrame = Seq(
    (1, "John", 30),
    (2, "Martin", 25),
    (3, "Linda", 35)
  ).toDF("id", "name", "age")

  private val dfMonad: DataFrameMonad = df.toMonad("People DataFrame")

  dfMonad.displayAllDetails(showColumnDetails = true)

  spark.stop()
}