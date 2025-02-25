
package railway.domain

import railway.domain.monads.DataFrameMonad
import spark.SparkSessionWrapper

import org.apache.spark.sql.DataFrame

import scala.language.postfixOps

object TrainOperations extends App with SparkSessionWrapper {


  import spark.implicits._

  spark.sparkContext.setLogLevel("ERROR")

  // Sample data for demonstration
  val trainSchedule = Seq(
    ("T001", "R001", "08:00", "10:00", "On Time"),
    ("T002", "R002", "09:00", "11:00", "Delayed")
  ).toDF("TrainID", "RouteID", "DepartureTime", "ArrivalTime", "Status")

  import monads.DataFrameMonadEnhancements._

  private def cubeDemo(trainSchedule: DataFrame): DataFrameMonad = {
    val cubeResult: DataFrame = trainSchedule.cube("TrainID", "RouteID").count()
    println("*" * 50)
    println("**** Cube operation **** ")
    println("*" * 50)
    cubeResult.show(truncate = false)
    cubeResult.toMonad("Cube operation")
  }

  private def rollupDemo(trainSchedule: DataFrame): DataFrameMonad = {
    val rollupResult = trainSchedule.rollup("TrainID", "RouteID").count()
    println("*" * 50)
    println("**** Rollup operation ****")
    println("*" * 50)
    rollupResult.show(truncate = false)
    rollupResult.toMonad("Rollup operation")
  }


  def runCubeDemo(): Unit = {
    val cubeResultMo = cubeDemo(trainSchedule)
    cubeResultMo.displayWithStatistics()

    cubeResultMo.displayWithDebug()
  }


  // Rollup operation
  private def runRollupDemo(): Unit = {
    val rollupResultMo = rollupDemo(trainSchedule)
    rollupResultMo.displayAllDetails(showColumnDetails = true)
  }

  runCubeDemo()

  runRollupDemo()

  spark.stop()
}
