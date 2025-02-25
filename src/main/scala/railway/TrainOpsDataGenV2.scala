
package railway

import implicits.Implicits._
import spark.{ErrorLevel, SparkSessionWrapper}

import org.apache.spark.sql.{DataFrame, SparkSession}

import java.time.LocalTime
import scala.language.postfixOps
import scala.util.Random

/**
 * Generation of Train Operations Data
 */
object TrainOpsDataGenV2 extends SparkSessionWrapper {
  import railway.domain.monads.DataFrameMonadEnhancements.DataFrameOps


  val maxTrainId = 5000
  val maxRouteId = 5000
  val stations: Map[ITCapacityUnit, MemorySize] = Map("StationA" -> 3, "StationB" -> 4, "StationC" -> 5) // Stations with respective durations
  val rand = new Random

  /**
   * Method to create a SparkSession builder.
   *
   * @return SparkSession Builder with predefined settings.
   */
  private def createSessionBuilder(): SessionBuilder = {
    var sessionBuilder = createSparkSession withLogLevel ErrorLevel
    sessionBuilder = sessionBuilder withDriverCores 2
    sessionBuilder = sessionBuilder withDriverMemory 10.Gb
    sessionBuilder = sessionBuilder withExecutorCores 2
    sessionBuilder = sessionBuilder withExecutorMemory 10.Gb
    sessionBuilder
  }

  /**
   * Generates journey details.
   *
   * @param numRows The number of rows/Journeys.
   * @return Array of Journey data
   */
  def generateJourneyDetails(numRows: Int): Seq[(String, String, String, String, String, String)] = {
    val trainJourneyData = Range(0, numRows)
      .map(generateTrainRouteStatus)
      .flatMap{ case (trainID, routeID, status) =>
        getTrainJourney(trainID, routeID, status)
      }
    trainJourneyData
  }


  /**
   * Generates an ID based on a given prefix and limit.
   * The ID will be in the format of prefix followed by a four-digit number.
   *
   * @param prefix The prefix for the generated ID.
   * @param limit The upper bound (exclusive) for random number generation.
   * @return The ID string in the format "prefix + four-digit-number"
   */
  private[railway] def generateId(prefix: Char, limit: Int): String = {
    // TODO: Convert to just private a test using PrivateMethod
    s"$prefix${"%04d".format(rand.nextInt(limit))}"
  }


  /**
   * Generates unique trainId, routeId, and status based on provided index.
   *
   * @param idx Unique index
   * @return Tuple with trainId, routeId and status
   */
  private def generateTrainRouteStatus(idx: Int): (String, String, String) = {
    /**
     * An ID for a Train, in the format "T + four-digit-number"
     */
    val trainId = generateId('T', maxTrainId)

    /**
     * An ID for a Route, in the format "R + four-digit-number"
     */
    val routeId = generateId('R', maxRouteId)

    val status = idx % 100 match {
      case x if x <= 5 => null
      case x if x > 5 && x <= 75 => "Delayed"
      case _ => "On Time"
    }
    (trainId, routeId, status)
  }

  /**
   * Generates the train journey data.
   *
   * @param trainId Id of the train
   * @param routeId Id of the route
   * @param status  Status of the journey
   * @return List of tuples with journey details
   */
  private def getTrainJourney(trainId: String, routeId: String, status: String): List[(String, String, String, String, String, String)] = {
    var depTime = LocalTime.of(8, 0) // assuming trains start at 8:00AM

    stations.keys.toList.zipWithIndex.map { case (station, _) =>
      val sessionDurationMin = stations(station)
      val arrTime = depTime.plusMinutes(sessionDurationMin)
      depTime = arrTime.plusMinutes(30)
      (trainId, routeId, station, depTime.toString, arrTime.toString, status)
    }
  }

  /**
   * Generates DataFrame based on provided number of rows.
   *
   * @param numRows Number of rows
   * @param spark   SparkSession (implicit)
   * @return DataFrame with train journey data
   */
  def dataGen(numRows: Int)(implicit spark: SparkSession): DataFrame = {
    import spark.implicits._
    val trainJourneyData = Range(0, numRows)
      .map(generateTrainRouteStatus)
      .flatMap{ case (trainID, routeID, status) => getTrainJourney(trainID, routeID, status) }

    val trainJourneyDf = trainJourneyData.toDF("TrainID", "RouteID", "Station", "DepartureTime", "ArrivalTime", "Status")
    trainJourneyDf
  }

  def main(args: Array[String]): Unit = {
    implicit val spark: SparkSession = createSessionBuilder().build
    (0 until 100000000 by 10000).foreach { _ =>
      // perform actions in batches on df
    }

    val df100 = dataGen(100)
    val df100Mo = df100.toMonad("TrainOpsDataGen")
    df100Mo.displayAllDetails(showColumnDetails = true)
  }
}