
package railway

import implicits.Implicits._
import spark.{ErrorLevel, SparkSessionWrapper}

import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.language.postfixOps

object TrainOpsDataGen extends App with SparkSessionWrapper {
  private var sessionBuilder = createSparkSession withLogLevel ErrorLevel
  sessionBuilder = sessionBuilder withDriverCores 2
  sessionBuilder = sessionBuilder withDriverMemory 10.Gb
  sessionBuilder = sessionBuilder withExecutorCores 2
  sessionBuilder = sessionBuilder withExecutorMemory 10.Gb

  override implicit val spark: SparkSession = sessionBuilder.build

  import spark.implicits._

  val rand = new scala.util.Random
  val maxTrainId = 5000 // Set this to the desired number of unique TrainIDs
  val maxRouteId = 5000 // Set this to the desired number of RouteIDs
  val stations = Map("StationA" -> 30, "StationB" -> 40, "StationC" -> 50) // Define your station names and their corresponding durations

  private def dataGen(numRows: Int)(implicit spark: SparkSession): DataFrame = {
    val data = Range(0, numRows)

    val trainJourneyData = data.map { idx =>
      val trainId = s"T${"%04d".format(rand.nextInt(maxTrainId))}"
      val routeId = s"R${"%04d".format(rand.nextInt(maxRouteId))}"
      val status = idx % 100 match {
        case x if x <= 5 => null
        case x if x > 5 && x <= 75 => "Delayed"
        case _ => "On Time"
      }
      (trainId, routeId, status)
    }.flatMap { case (trainId, routeId, status) =>
      stations.keys.toList.zipWithIndex.map { case (station, idx) =>
        val sessionDurationMin = stations(station);  // use the session duration specific to the current station
        val depTime = f"${8 + idx}:${if (idx % 2 == 0) "00" else "30"}"
        val arrTime = f"${8 + idx + sessionDurationMin/60}:${if (idx % 2 == 0) "00" else "30"}"
        (trainId, routeId, station, depTime, arrTime, status)
      }
    }
    val trainJourneyDf = trainJourneyData.toDF("TrainID", "RouteID", "Station", "DepartureTime", "ArrivalTime", "Status")
    trainJourneyDf
  }

  (0 until 100000000 by 10000).foreach { batch =>
    //val df = dataGen(batch)
    // perform actions on df
  }

  import railway.domain.monads.DataFrameMonadEnhancements.DataFrameOps
  val df100 = dataGen(100)
  val df100Mo = df100.toMonad("TrainOpsDataGen")
  df100Mo.getDF cache

  df100Mo displayAllDetails(true, asMarkdown = true)
}

