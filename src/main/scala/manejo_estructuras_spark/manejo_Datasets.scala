package manejo_estructuras_spark

import org.apache.spark.sql.DataFrame

import org.apache.spark.sql.{Dataset, Encoder, Encoders, SparkSession}

case class Person(name: String, age: Int)

object EjemploDatasetsApp01 extends App {
  // Crear un Encoder para la clase Person
  // El encoder se utiliza para serializar y deserializar objetos de tipo Person
  implicit val personEncoder: Encoder[Person] = Encoders.product[Person]

  val spark = SparkSession.builder().appName("EjemploDatasets01App").master("local[2]").getOrCreate()
  spark.sparkContext.setLogLevel("ERROR")

  import spark.implicits._

  // Crear un Dataset a partir de una secuencia de objetos de tipo Person
  // Este es el mayor cambio con respecto a los DataFrames, ya que los DataFrames no son tipados
  val data = Seq(Person("Alice", 34), Person("Bob", 45), Person("Charlie", 23))

  // SEQ -> DATASET
  // toDS() es un método implícito de SparkSession que convierte una secuencia de objetos en un Dataset
  val ds = data.toDS()
  ds.show(false)

  // Funcion estandar de Scala
  val mayoresDeVeintidosFunc: Person => Boolean = (p: Person) => p.age > 22
  val mayoresDeVeintidos: Dataset[Person] = ds.filter(mayoresDeVeintidosFunc)
  mayoresDeVeintidos.show(false)
  val ds2 = ds.map(p => Person(p.name, p.age + 1))
  ds2.show(false)
  val doblarEdad: Int => Int = (age: Int) => age * 2
  val ds3: Dataset[Person] = ds.map(p => Person(p.name, doblarEdad(p.age)))
  ds3.show(false)

  // SEQ -> DF
  val data2 = Seq(Person("Alice", 34), Person("Bob", 45), Person("Charlie", 23))
  val df: DataFrame = spark.createDataFrame(data2)
  df.show(false)

  // DF -> DATASET
  val MiDataset = df.as[Person]
  MiDataset.show(false)


  spark.stop()
}
