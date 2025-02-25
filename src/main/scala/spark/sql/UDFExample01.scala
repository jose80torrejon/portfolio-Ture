
package spark.sql

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.explode
import org.apache.spark.sql.functions.split
import org.apache.spark.sql.functions.udf

trait SparkSessionWrapper {
  implicit lazy val spark: SparkSession = {
    val conf = new org.apache.spark.SparkConf().setAppName("UDFExample01")
      .setMaster("local[*]")

    val sparkSession = SparkSession.builder().config(conf).getOrCreate()
    sparkSession.sparkContext.setLogLevel("ERROR")
    sparkSession
  }
}

object UDFExample01Functions extends SparkSessionWrapper {
  // Definir la UDF

  val pattern: String = """[\p{Punct}]"""
  val splitPattern: String = "\\s+"

  def removePunctuation(text: String): String = text.toLowerCase.replaceAll(pattern, "")
  def splitBySpaces(text: String): Array[String] = text.split(splitPattern)
  def groupByWords(words: Array[String]): Map[String, Int] = words.groupBy(e => e).map(e => (e._1, e._2.length))

  val wordsCount: UserDefinedFunction = udf { (textContent: String) =>
    val textWithoutPunctuation = removePunctuation(textContent)
    val words = splitBySpaces(textWithoutPunctuation)
    groupByWords(words)
  }


  def createDataFrame(data: Seq[(String, String)]): DataFrame = {
    import spark.implicits._
    data.toDF("id", "text")
  }

  def applyWordCount(df: DataFrame): DataFrame = {
    import spark.implicits._
    df.withColumn("wordCount", wordsCount($"text"))
  }

  def explodeWordCount(df: DataFrame): DataFrame = {
    import spark.implicits._
    df.select($"id", explode($"wordCount"))
      .groupBy("key").sum("value")
  }

}

object UDFExample01 extends App with SparkSessionWrapper {
  import UDFExample01Functions._
  // Crear el DataFrame
  val data = Seq(("1", "Hello world"), ("2", "I love Spark"), ("3", "I used to love Hi Spark world"))
  val df = createDataFrame(data)

  // Aplicar la UDF para contar las palabras
  val wordCountDF = applyWordCount(df)
  wordCountDF.show(false)

  // Expander el mapa para tener una columna para cada palabra
  val explodedWCDF = explodeWordCount(wordCountDF)
  explodedWCDF.show()



}
