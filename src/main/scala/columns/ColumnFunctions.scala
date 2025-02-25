
package columns

import org.apache.spark.sql.Column
import org.apache.spark.sql.functions._

import scala.Console.{BLUE, BOLD, GREEN, RESET}

object ColumnFunctions {

  /**
   *
   * @param col Columna a la que se le va a aplicar la función
   * @return Columna con el texto en minúsculas y sin espacios
   */
  def lowercaseWithoutWhitespace(col: Column): Column = {
    lower(regexp_replace(col, "\\s+", ""))
  }

  /**
   *
   * @param col Columna a la que se le va a aplicar la función
   * @return Columna con el texto en mayúsculas y sin espacios
   */
  def uppercaseWithoutWhitespace(col: Column): Column = {
    upper(regexp_replace(col, "\\s+", ""))
  }

  /**
   *
   * @param col Columna a la que se le va a aplicar la función
   * @return Columna con el texto sin caracteres especiales
   */
  def removeSpecialCharacters(col: Column): Column = {
    // [^a-zA-Z0-9] significa que se va a reemplazar todo lo que no sea una letra o un número
    regexp_replace(col, "[^a-zA-Z0-9]", "")
  }

  /**
   *
   * @param col Columna a la que se le va a aplicar la función
   * @return Columna con el texto en minúsculas, sin caracteres especiales y sin espacios
   */
  def removeSpecialCharactersAndWhitespaceInLower(col: Column): Column = {
    lowercaseWithoutWhitespace(removeSpecialCharacters(col))
  }

  /**
   *
   * @param col Columna a la que se le va a aplicar la función
   * @return Columna con el texto en mayúsculas, sin caracteres especiales y sin espacios
   */
  def removeSpecialCharactersAndWhitespaceInUpper(col: Column): Column = {
    uppercaseWithoutWhitespace(removeSpecialCharacters(col))
  }


}

object ColumnFunctionsApp extends App {

  import org.apache.spark.sql.SparkSession

  val spark = SparkSession.builder()
    .appName("ColumnFunctionsApp")
    .master("local[*]")
    .getOrCreate()

  spark.sparkContext.setLogLevel("ERROR")

  import spark.implicits._

  spark.sparkContext.setLogLevel("ERROR")

  val df = Seq(
    ("Messi", 100, 50, "America", "Argentina"),
    (" Ronaldo!!", 90, 40, "Europe", "Portugal"),
    ("Neymar", 80, 30, "America", "Brazil"),
    ("Mbappe_ ", 70, 20, "Europe", "France"),
  ).toDF("name", "goals", "assists", "continent", "country")

  df.show()

  val resDF = df
    .withColumn("name_no_whitespace",
      ColumnFunctions.lowercaseWithoutWhitespace(col("name")))
    .withColumn("name_no_special_characters", ColumnFunctions.removeSpecialCharacters(col("name_no_whitespace")))

  resDF.show()
  // O concatenado funciones
  val resDF2 = df
    .withColumn("nombre_normalizado",
      ColumnFunctions.lowercaseWithoutWhitespace(
        ColumnFunctions.removeSpecialCharacters(
          ColumnFunctions.uppercaseWithoutWhitespace(col("name")))
      )
    )
  resDF2.show(truncate = false)


  println("Explicación de la ejecución de los dataframes")
  println(BOLD + BLUE + "resDF: Sin concatenar funciones" + RESET)
  resDF.explain(extended = true)
  println()
  println(BOLD + GREEN + "resDF2: Concatenando funciones" + RESET)
  resDF2.explain(extended = true)

  spark.stop()
}
