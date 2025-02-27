package manejo_estructuras_spark

import org.apache.spark.sql.functions
import manejo_estructuras_spark.SparkSessionWrapper

object manejo_GroupingSets extends App with SparkSessionWrapper {

  spark.sparkContext.setLogLevel("ERROR")
  // Desactivamos AQE
  spark.conf.set("spark.sql.adaptive.enabled", "false")
  // Desactivamos el broadcast join
  spark.conf.set("spark.sql.autoBroadcastJoinThreshold", "-1")

  import spark.implicits._

  val data = Seq(
    ("Banana", "Fruit", 1000, 1),
    ("Carrot", "Vegetable", 1000, 1),
    ("Bean", "Vegetable", 2000, 2),
    ("Orange", "Fruit", 2000, 2),
    ("Banana", "Fruit", 4000, 3),
    ("Carrot", "Vegetable", 4000, 3),
    ("Bean", "Vegetable", 3000, 0 ),
    ("Orange", "Fruit", 3000, 0)
  )
  val df = data.toDF("Name", "Category", "Sales", "Quantity")
  df.createOrReplaceTempView("sales_data")
  val result = spark.sql("SELECT Name, Category, sum(Sales) FROM sales_data GROUP BY Name, Category GROUPING SETS((Name, Category))")
  // Similares
  val resultDf = df.groupBy("Name", "Category").agg(functions.sum("Sales")).rollup("Name", "Category").count()

  // PLANES DE EJECUCIÓN
  result.explain(true)
  resultDf.explain(true)

  resultDf.show(false)
  result.show(false)
}

