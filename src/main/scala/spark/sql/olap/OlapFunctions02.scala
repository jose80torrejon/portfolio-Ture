
package spark.sql.olap

import spark.SparkSessionWrapper

import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions
import org.apache.spark.sql.functions.{lag, row_number}

import scala.Console.{BOLD, RESET}


trait PrintUtils {
  def printTitle(title: String): Unit = {
    println(BOLD + s" *** $title ***" + RESET)
  }
}

object  OlapFunctions02 extends App with SparkSessionWrapper with PrintUtils {

  spark.sparkContext.setLogLevel("ERROR")
  import spark.implicits._

  // Creamos un dataset de ejemplo para hacer operaciones con ventanas de salarios por departamento
  val data = Seq(
    ("Sales", 1, 1000),
    ("Sales", 2, 2000),
    ("Sales", 3, 3000),
    ("Sales", 4, 4000),
    ("Sales", 5, 5000),
    ("Sales", 6, 6000),
    ("Sales", 7, 7000),
    ("Sales", 8, 8000),
    ("Sales", 9, 9000),
    ("Sales", 10, 10000),
    ("IT", 1, 2000),
    ("IT", 2, 4000),
    ("IT", 3, 6000),
    ("IT", 4, 8000),
    ("IT", 5, 10000),
    ("IT", 6, 12000),
    ("IT", 7, 14000),
    ("IT", 8, 16000),
    ("IT", 9, 18000),
    ("IT", 10, 20000)
  )

  val partitionColumn = "Department"
  val orderByColumn   = "Salary"
  val df = data.toDF(partitionColumn, "Month", orderByColumn)

  df.show(truncate = false)

  // Creamos una ventana por departamento y ordenamos por salario
  val windowSpec = Window
    // Particionamos por departamento
    .partitionBy(partitionColumn)
    // Ordenamos por salario
    .orderBy(orderByColumn)

  // Creamos una columna con el salario anterior
  // lag: devuelve el valor de la columna en la fila anterior
  // lead: devuelve el valor de la columna en la siguiente fila, equivalente a lag con un valor negativo
  val df2 = df.withColumn("PreviousSalary", lag(orderByColumn, 1).over(windowSpec))
  printTitle("Salario anterior")
  df2.show(truncate = false)

  // Creamos una columna con el salario siguiente
  val df3 = df.withColumn("NextSalary", lag(orderByColumn, -1).over(windowSpec))
  //println(BOLD + " *** Salario siguiente:" + RESET)
  printTitle("Salario siguiente")
  df3.show(truncate = false)

  // Creamos una columna con el salario anterior y siguiente
  val df4 = df.withColumn("PreviousSalary", lag(orderByColumn, 1).over(windowSpec))
    .withColumn("NextSalary", lag(orderByColumn, -1).over(windowSpec))

  //println(BOLD + " *** Salario anterior y siguiente:" + RESET)
  printTitle("Salario anterior y siguiente")
  df4.show(truncate = false)

  // Creamos una columna con el salario anterior y siguiente y el salario actual
  val df5 = df.withColumn("PreviousSalary", lag(orderByColumn, 1).over(windowSpec))
    .withColumn("NextSalary", lag(orderByColumn, -1).over(windowSpec))
    .withColumn("CurrentSalary", $"Salary")

  printTitle("Salario anterior, siguiente y actual")
  df5.show(truncate = false)

  // lead y lag con valores por defecto
  val df6 = df.withColumn("PreviousSalary", lag(orderByColumn, 1, 0).over(windowSpec))
    .withColumn("NextSalary", lag(orderByColumn, -1, 0).over(windowSpec))
    .withColumn("CurrentSalary", $"Salary")

  printTitle("Salario anterior, siguiente y actual con valores por defecto")
  df6.show(truncate = false)

  // Uso de row_number
  // row_number: devuelve el número de fila de la fila actual
  val df7 = df.withColumn("RowNumber", row_number().over(windowSpec))
  printTitle("Número de fila")
  df7.show(truncate = false)

  // Por ejemplo podemos obtener los id de las 3 personas con mayor salario por departamento
  val df8 = df.withColumn("RowNumber", row_number().over(windowSpec))
    .filter($"RowNumber" <= 3)

  printTitle("Top 3 salarios por departamento")
  df8.show(truncate = false)

  // Otros tipos de ventanas
  // - rolling window: ventana deslizante con tamaño fijo
  // - sliding window: ventana deslizante con tamaño variable
  // - tumbling window: ventana fija de tamaño fijo
  // - hopping window: ventana fija de tamaño variable
  // - session window: ventana de sesión. Es más compleja y se usa en procesamiento de eventos.
  // Se usa en procesamiento de eventos para agrupar eventos en sesiones.

  // rolling window
  // Creamos una ventana deslizante de 3 filas de tamaño fijo
  val rollingWindowSpec = Window
    .partitionBy(partitionColumn)
    .orderBy(orderByColumn)
    // En este caso la ventana se desliza entre la fila actual y las 2 filas siguientes
    .rowsBetween(-2, 0)

  val df9 = df.withColumn("RollingWindow", functions.sum(orderByColumn).over(rollingWindowSpec))
  printTitle("Ventana deslizante")
  df9.show(truncate = false)

  /*
   *** Ventana deslizante ***
+----------+-----+------+-------------+
|Department|Month|Salary|RollingWindow|
+----------+-----+------+-------------+
|IT        |1    |2000  |2000         |
|IT        |2    |4000  |6000         |
|IT        |3    |6000  |12000        |
|IT        |4    |8000  |18000        |
|IT        |5    |10000 |24000        |
|IT        |6    |12000 |30000        |
|IT        |7    |14000 |36000        |
|IT        |8    |16000 |42000        |
|IT        |9    |18000 |48000        |
|IT        |10   |20000 |54000        |
|Sales     |1    |1000  |1000         |
|Sales     |2    |2000  |3000         |
|Sales     |3    |3000  |6000         |
|Sales     |4    |4000  |9000         |
|Sales     |5    |5000  |12000        |
|Sales     |6    |6000  |15000        |
|Sales     |7    |7000  |18000        |
|Sales     |8    |8000  |21000        |
|Sales     |9    |9000  |24000        |
|Sales     |10   |10000 |27000        |
+----------+-----+------+-------------+
* Este ejemplo muestra una ventana deslizante de 3 filas de tamaño fijo
* En este caso la ventana se desliza entre la fila actual y las 2 filas siguientes
* Así:
* |IT        |4    |8000  |18000        | = 8000 + 10000 + 12000 = 30000

*
   */

  spark.stop()

}
