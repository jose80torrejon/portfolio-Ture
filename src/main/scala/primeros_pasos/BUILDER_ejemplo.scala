package primeros_pasos

//package eoi.de.examples
//package packageobjects

import java.time.Instant

package object domain {

  // Type for GPS Coordinates
  type GPS = (Double, Double)

  // Type for Passenger info
  // Usamos un typed alias para definir un tipo de dato compuesto en lugar una case class
  // porque no necesitamos métodos (de momento) para manipular los datos
  type PassengerName = String
  type PassengerAge = Int
  type PassengerInfo = (PassengerName, PassengerAge)

  // Create a domain model representing trains, schedules, and stations
  // Create a case class for a Train
  case class Train(name: String, capacity: Int, schedule: Schedule, location: Option[GPS] = None)

  // Create a case class for a Schedule
  // Instant: Representa un instante en el tiempo. Se puede pensar en él como un punto en el tiempo.
  case class Schedule(departureTime: Instant, arrivalTime: Instant)

  // Create a case class for a TrainSchedule
  case class TrainSchedule(schedule: Schedule, stations: List[Station])

  // Create a case class for a Station
  type ListaOpcionalPasajeros = Option[List[PassengerInfo]]

  case class Station(name: String, gpsLocation: GPS, passengers: ListaOpcionalPasajeros = None)

  // Usando AnyVal para crear un tipo de dato nuevo
  // AnyVal es un trait que se utiliza para crear tipos de datos nuevos en Scala.
  // Los tipos de datos creados con AnyVal son más eficientes que los tipos de datos creados con case class.
  // A veces se les llama "value classes" porque representan un único valor.
  case class TrainId(value: Int) extends AnyVal

  case class TrainName(value: String) extends AnyVal

  case class TrainCapacity(value: Int) extends AnyVal


  case class TrainV2(id: TrainId, name: TrainName, capacity: TrainCapacity, schedule: Schedule, location: Option[GPS] = None) {
    override def toString: String = s"TrainV2($id, $name, $capacity, $schedule, $location)"

    override def equals(obj: Any): Boolean = obj match {
      case TrainV2(id2, name2, capacity2, schedule2, location2) =>
        (id == id2) &&
          (name == name2) &&
          (capacity == capacity2) &&
          (schedule == schedule2)
        // Omitimos la localización porque no es un campo obligatorio: (location == location2)
      case _ => false
    }
  }

  case class TrainBuilder(id: Option[TrainId] = None,
                          name: Option[TrainName] = None,
                          capacity: Option[TrainCapacity] = None,
                          schedule: Option[Schedule] = None,
                          location: Option[GPS] = None
                         ) {

    def withId(id: TrainId): TrainBuilder = this.copy(id = Some(id))

    def withName(name: TrainName): TrainBuilder = this.copy(name = Some(name))

    def withCapacity(capacity: TrainCapacity): TrainBuilder = this.copy(capacity = Some(capacity))

    def withSchedule(schedule: Schedule): TrainBuilder = this.copy(schedule = Some(schedule))

    def withLocation(location: GPS): TrainBuilder = this.copy(location = Some(location))

    def build(): Option[TrainV2] = (id, name, capacity, schedule) match {
      case (Some(i), Some(n), Some(c), Some(s)) => Some(TrainV2(i, n, c, s, location))
      case _ => None // return None if any of the main fields is missing.
    }
  }

  object TrainBuilder {
    // Ponemos el new para que lo diferencia del objeto TrainBuilder
    def apply(): TrainBuilder = new TrainBuilder()
  }

}

object DomainExample extends App {

  import domain._

  val schedule = Schedule(Instant.now(), Instant.now().plusSeconds(3600))
  println(schedule.toString)
  val station = Station("Madrid", (40.4167, -3.70325))
  println(station.toString)
  val train = Train("Talgo", 100, schedule, Some((40.4167, -3.70325)))
  println(train.toString)

  val trainBuilder: TrainBuilder = TrainBuilder()
  val train2: Option[TrainV2] = trainBuilder
    .withId(TrainId(1))
    .withName(TrainName("Talgo"))
    .withCapacity(TrainCapacity(100))
    .withSchedule(schedule)
    .withLocation((40.4167, -3.70325))
    .build()

  println(train2)
  assert(train2.isDefined)

  val train3: Option[TrainV2] = trainBuilder
    .withId(TrainId(1))
    .withName(TrainName("Talgo"))
    .withCapacity(TrainCapacity(100))
    .withSchedule(schedule)
    .build()
  println(s" ¿Son iguales?")
  println(train2.get)
  println(train3.get)
  println(train3.get.equals(train2.get))
}

/*
package object models {
  val DefaultTimeout = 5000

  def calculateTimeout(nRetries: Int): Int = {
    nRetries * DefaultTimeout
  }
}

package com.mycompany.myapp.models

class MyModel1 {
  def doSomething(): Unit = {
    println("The default timeout is " + DefaultTimeout)
    println("The timeout after 5 retries would be " + calculateTimeout(5))
  }
}

class MyModel2 {
  def doSomethingElse(): Unit = {
    println("The default timeout is " + DefaultTimeout)
    println("The timeout after 3 retries would be " + calculateTimeout(3))
  }
}
 */