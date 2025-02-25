
package spark.sql.datasets

import org.apache.spark.sql.DataFrame

object EjemploDatasetsApp03 extends App {
  // Ejemplos de conversiones de DataFrames a Datasets y viceversa
  // Se necesita importar las clases necesarias

  import org.apache.spark.sql.SparkSession

  // Se crea un objeto case class para representar una fila de datos
  case class Person(name: String, age: Int)

  val spark = SparkSession.builder().appName("EjemploDatasets03App")
    .master("local[2]").getOrCreate()
  spark.sparkContext.setLogLevel("ERROR")

  // Crear un DataFrame a partir de una secuencia de objetos de tipo Person
  val data = Seq(Person("Alice", 34), Person("Bob", 45), Person("Charlie", 23))
  val df: DataFrame = spark.createDataFrame(data)
  df.show(false)

  // Convertir el DataFrame a un Dataset

  import spark.implicits._

  val ds = df.as[Person]
  ds.show(false)


}
