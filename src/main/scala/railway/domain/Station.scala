
package railway.domain

import scala.language.reflectiveCalls

/**
 * Represents the name of a Station
 *
 * @param value Name of station as a String
 */
final case class StationName(value: String) extends AnyVal

/**
 * Represent the status name
 * @param value Status of a Railway line as a String
 */
final case class StatusName(value: String) extends AnyVal

/**
 * Represents a Station
 * @param name Name of the station
 * @param sessionTime Session time spent at the station as an Int
 */
final case class Station(name: StationName, sessionTime: Int) {}

/**
 * Trait representing a Status
 * Contains a name and a probability
 */
sealed trait Status {
  def name: StatusName
  def probability: Int
}

/**
 * Case class representing status "On Time"
 * @param probability Probability of status being "On Time" as Int
 */
final case class OnTime(probability: Int) extends Status {
  override def name: StatusName = StatusName("On Time")
}

/**
 * Case class representing status "Delayed"
 * @param probability Probability of status being "Delayed" as Int
 */
final case class Delayed(probability: Int) extends Status {
  override val name: StatusName = StatusName("Delayed")
}

/**
 * Defines operations on Railway station and status.
 */
object RailwayOpsDsl {
  var stations = List.empty[Station]
  var statuses = List.empty[Status]

  class StationBuilder {
    def withName(stationName: StationName): Object {def withSessionTime(time: Int): Unit} = new {
      def withSessionTime(time: Int): Unit = {
        RailwayOpsDsl.addStation(Station(stationName, time))
      }
    }
  }

  /**
   * Adds a station to the list of stations
   */
  private def addStation(station: Station): Unit = stations = station :: stations

  /**
   * Add a status to the list of statuses
   */
  private def addStatus(status: Status): Unit = statuses = status :: statuses


  def station(name: StationName): Object {def withSessionTime(time: Int): Unit} = new {
    def withSessionTime(time: Int): Unit = {
      addStation(Station(name, time))
    }
  }

  def statusOnTime: Object {def withProbability(prob: Int): Unit} = new {
    def withProbability(prob: Int): Unit = {
      addStatus(OnTime(prob))
    }
  }

  def statusDelayed: Object {def withProbability(prob: Int): Unit} = new {
    def withProbability(prob: Int): Unit = {
      addStatus(Delayed(prob))
    }
  }
}

/**
 * Main application entry point
 * Initializes Railway stations and statuses then print them
 */
object Main extends App {
  import RailwayOpsDsl._
  def station = new StationBuilder

  station withName StationName("StationA") withSessionTime 3
  station withName StationName("StationB") withSessionTime 4
  station withName StationName("StationC") withSessionTime 5
  statusOnTime  withProbability 75
  statusDelayed withProbability 25
  statuses.foreach(status => println(s"Status: ${status.name.value}, Probability: ${status.probability}%"))
  stations.foreach(station => println(s"Station: ${station.name.value}, Session Time: ${station.sessionTime}"))

}