
package rdd

import org.apache.spark.rdd.RDD

object EjemplosSparkRDD extends App {

  // Ejemplo de uso del API de RDD de Spark

  import org.apache.spark.{SparkConf, SparkContext}
  // Un RDD es una colección distribuida de elementos
  // Los RDDs se pueden crear a partir de un archivo, de una colección de datos en memoria o de una base de datos
  // Se pueden transformar y combinar entre sí para realizar operaciones más complejas,
  // pero el API es más bajo nivel que el de los DataFrames

  // Sin embargo, los RDDs son más flexibles y permiten realizar operaciones más complejas que los DataFrames
  // ya que son tipados y permiten trabajar con cualquier tipo de datos

  // Configuración de Spark
  val conf = new SparkConf().setAppName("EjemplosSparkRDD").setMaster("local[*]")
  val sc = new SparkContext(conf)
  sc.setLogLevel("ERROR")

  // Creación de un RDD a partir de una colección de datos
  // Este es un caso común en pruebas y ejemplos
  val rdd: RDD[Int] = sc.parallelize(Seq(1, 2, 3, 4, 5))
  val filtrarSiPar: Int => Boolean = (x: Int) => x % 2 == 0
  rdd.filter(filtrarSiPar).collect().foreach(println)

  // WordCount en un RDD de Strings
  val rddStrings = sc.parallelize(Seq("hola mundo", "hola", "mundo", "hola", "hola"))
  val palabrasDesdeFrases = (linea: String) => linea.split(" ")
  palabrasDesdeFrases("hola mundo de Scala ...").foreach(println)

  //
  val rddWords = rddStrings.flatMap { linea =>
    palabrasDesdeFrases(linea)
  }
  println("rddWords: ")
  rddWords.collect().foreach(println)

  val tuplaPalabra = (palabra: String) => (palabra, 1)
  val holaTupla = tuplaPalabra("hola")
  println(s"tuplaPalabra: ${holaTupla}")

  val rddWordCount = rddWords.map { palabra =>
      tuplaPalabra(palabra)
    }
    .reduceByKey(_ + _) // Suma los valores de las tuplas con la misma clave
  rddWordCount.collect().foreach(println)

  // WordCount con dataframes

  import org.apache.spark.sql.SparkSession

  val spark = SparkSession.builder().appName("EjemplosSparkRDD").master("local[*]").getOrCreate()
  import spark.implicits._

  val df = rddStrings.toDF("linea")
  df.show()

  df.selectExpr("explode(split(linea, ' ')) as palabra").show(false)

  val dfWordCount = df.selectExpr("explode(split(linea, ' ')) as palabra").groupBy("palabra").count()
  dfWordCount.show()

  // Supongamos que tenemos una función compleja que no puede ser expresada fácilmente con Spark SQL
  def unaFuncionCustomizada(x: Int): Int = x * x + 2 * x + 1

  def unaFuncionCustomizada2(x: Int): Int = x * x * x

  def funcionCompleja(x: Int): Int = {

    if (x % 2 == 0) x * x
    else if (x % 3 == 0)
      unaFuncionCustomizada(x * x * x)
    else
      unaFuncionCustomizada2(x * x + 2 * x + 1)
  }

  val rdd2 = sc.parallelize(Seq(1, 2, 3, 4, 5))
  val rddTransformado = rdd2.map(funcionCompleja)
  println("--->>> rdd Transformado usando la función compleja: ")
  rddTransformado.collect().foreach(println)
  println()

  // En ocasiones con logs con muy poco formato o que vienen en binario se pre-procesan antes con RDD y luego con Sparksql
  val log = Seq(
    "2021-01-01 10:00:00 INFO: Iniciando proceso",
    "2021-01-01 10:00:01 INFO: Proceso finalizado",
    "2021-01-01 10:00:02 ERROR: Error en el proceso",
    "2021-01-01 10:00:03 INFO: Iniciando proceso",
    "2021-01-01 10:00:04 INFO: Proceso finalizado",
    "2021-01-01 10:00:05 ERROR: Error en el proceso",
    "2021-01-01 10:00:06 INFO: Iniciando proceso",
    "2021-01-01 10:00:07 INFO: Proceso finalizado",
    "2021-01-01 10:00:08 ERROR: Error en el proceso",
    "2021-01-01 10:00:09 INFO: Iniciando proceso",
    "2021-01-01 10:00:10 INFO: Proceso finalizado",
    "2021-01-01 10:00:11 ERROR: Error en el proceso",
  )

  // Genera un binario con los logs
  def toBinary(log: Seq[String]): Array[Byte] = {
    log.mkString("\n").getBytes
  }

  def fromBinary(binary: Array[Byte]): Seq[String] = {
    new String(binary).split("\n")
  }

  val binaryLog = toBinary(log)
  println(binaryLog.mkString(","))
  println()

  val rddLog = sc.parallelize(Seq(binaryLog))

  val rddLogLines = rddLog.flatMap { binary =>
    fromBinary(binary)
  }
  rddLogLines.collect().foreach(println)

  // Desde Scala se puede llamar a funciones echas en C, C++ con JNI
  // https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/jniTOC.html
  // Por lo que se puede usar un decoder existente en C para decodificar logs binarios

  def decodeLogLineC(line: String): String = identity(line)

  def decodeLogLine(line: String): String = {
    // Decodificar la línea de log llamando a una función en C
    decodeLogLineC(line)
  }

  val rddLogLinesDecoded = rddLogLines.map { lineaBin =>
    // Decodificar la línea de log
    decodeLogLine(lineaBin)
  }

  // Un ejemplo de graphx con RDD

  import org.apache.spark.graphx.{Edge, Graph}

  val vertices = sc.parallelize(Seq(
    (1L, "Alice"),
    (2L, "Bob"),
    (3L, "Charlie"),
    (4L, "David"),
    (5L, "Ed"),
    (6L, "Fran")
  ))
  val edges = sc.parallelize(Seq(
    Edge(1L, 2L, "EsAmigo"),
    Edge(2L, 3L, "EsAmigo"),
    Edge(3L, 4L, "SonConocidos"),
    Edge(4L, 5L, "EsAmigo"),
    Edge(5L, 6L, "SonFamilia"),
    Edge(6L, 1L, "EsAmigo")
  ))

  val graph: Graph[String, String] = Graph(vertices, edges)
  graph.triplets.collect().foreach(println)

  // Transforma graphx en DataFrame
  graph.triplets.map(triplet =>
      (triplet.srcId, triplet.dstId, triplet.attr))
    .toDF("src", "dst", "relationship")
    .show()


  // Cierre de Spark
  sc.stop()
  spark.stop()


}
