package manejo_estructuras_spark

import org.apache.spark.sql.functions
import manejo_estructuras_spark.SparkSessionWrapper

object manejo_Particionado_Bucketizado extends App with SparkSessionWrapper {

  spark.sparkContext.setLogLevel("ERROR")
  // Desactivamos AQE
  spark.conf.set("spark.sql.adaptive.enabled", "false")
  // Desactivamos el broadcast join
  spark.conf.set("spark.sql.autoBroadcastJoinThreshold", "-1")
  // Particiones de spark.sql.shuffle.partitions
  spark.conf.set("spark.sql.shuffle.partitions", "10")

  import spark.implicits._

  val data = Seq(
    ("Banana", "Fruit", 1000, 1),
    ("Carrot", "Vegetable", 1000, 1),
    ("Bean", "Vegetable", 2000, 2),
    ("Orange", "Fruit", 2000, 2),
    ("Banana", "Fruit", 4000, 3),
    ("Carrot", "Vegetable", 4000, 3),
    ("Bean", "Vegetable", 3000, 0),
    ("Orange", "Fruit", 3000, 0)
  )

  // Generamos usa collección de datos enorme a partir de la colección de datos anterior
  // La función flatMap aplica la función que recibe a cada elemento de la colección y devuelve una colección de elementos
  val data2 = (0 until 1000000).flatMap { _ => data }

  val usarExistentes = true
  val BucketSize = 4
  val BucketColumn = "Name"
  // Seguimos el critrio de los NoSQL y usamos el nombre de la tabla como el nombre de la columna de bucketing
  val TablaPorNombre = "sales_data_by_name"
  // Tabla con bucketing y particionada por Category
  val TablaPorNombreYCategoria = "sales_data_by_name_and_category"

  if (!usarExistentes) {
    val df = data2.toDF(BucketColumn, "Category", "Sales", "Quantity")
    // Vamos a usar bucketing para mejorar el rendimiento
    df.write.bucketBy(BucketSize, BucketColumn).saveAsTable(TablaPorNombre)
    df.write.partitionBy("Category").bucketBy(BucketSize, BucketColumn).saveAsTable(TablaPorNombreYCategoria)
  } else {
    println(s"Usando tablas existentes $TablaPorNombre y $TablaPorNombreYCategoria")
    spark.read.load("./spark-warehouse/" + TablaPorNombre).createOrReplaceTempView(TablaPorNombre)
    spark.read.load("./spark-warehouse/" + TablaPorNombreYCategoria).createOrReplaceTempView(TablaPorNombreYCategoria)
  }


  val dfConBucketing = spark.table(TablaPorNombre)
  println(s"Tabla $TablaPorNombre contiene: ${dfConBucketing.count()} filas")

  val result = spark.sql(s"SELECT $BucketColumn, Category, sum(Sales) FROM $TablaPorNombre GROUP BY $BucketColumn, Category GROUPING SETS(($BucketColumn, Category))")

  // Idem. pero con el método groupingSets
  // Usamos rollup para obtener el mismo resultado que con grouping sets
  val resultDf = dfConBucketing.groupBy(BucketColumn, "Category").agg(functions.sum("Sales")).rollup(BucketColumn, "Category").count()

  result.explain(true)
  resultDf.explain(true)

  result.show()


  val dfConBucketingYParticionado = spark.table(TablaPorNombreYCategoria)

  val result2 = spark.sql(s"SELECT $BucketColumn, Category, sum(Sales) FROM $TablaPorNombreYCategoria GROUP BY $BucketColumn, Category GROUPING SETS(($BucketColumn, Category))")

  // Idem. pero con el método groupingSets
  // Usamos rollup para obtener el mismo resultado que con grouping sets
  val resultDf2 = dfConBucketingYParticionado.groupBy(BucketColumn, "Category").agg(functions.sum("Sales")).rollup(BucketColumn, "Category").count()

  result2.explain(true)
  resultDf2.explain(true)

  result2.show(truncate = false)
  resultDf2.show(truncate = false)

}

