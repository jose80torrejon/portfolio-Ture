import java.time.Instant

package object primeros_pasos {

  // Type for GPS Coordinates
  type GPS = (Double, Double)

  // Type for Passenger info
  // Usamos un typed alias para definir un tipo de dato compuesto en lugar una case class
  // porque no necesitamos métodos (de momento) para manipular los datos
  type PassengerName = String
  type PassengerAge = Int
  type PassengerInfo = (PassengerName, PassengerAge)
  type OptionalTrainV2 = Option[TrainV2]

  // Create a domain model representing trains, schedules, and stations
  // Create a case class for a Train
  final case class Train(name: TrainName, capacity: Int, schedule: Schedule, location: Option[GPS] = None)

  // Create a case class for a Schedule
  // Instant: Representa un instante en el tiempo. Se puede pensar en él como un punto en el tiempo.
  final case class Schedule(departureTime: Instant, arrivalTime: Instant)

  // Create a case class for a TrainSchedule
  final case class TrainSchedule(schedule: Schedule, stations: List[Station])

  // Create a case class for a Station
  type ListaOpcionalPasajeros = Option[List[PassengerInfo]]

  final case class Station(name: String, gpsLocation: GPS, passengers: ListaOpcionalPasajeros = None)

  // Usando AnyVal para crear un tipo de dato nuevo
  // AnyVal es un trait que se utiliza para crear tipos de datos nuevos en Scala.
  // Los tipos de datos creados con AnyVal son más eficientes que los tipos de datos creados con case class.
  // A veces se les llama "value classes" porque representan un único valor.
  final case class TrainId(value: Int) extends AnyVal

  //final case class TrainName(value: String) extends AnyVal

  final case class TrainCapacity(value: Int) extends AnyVal


  final case class TrainV2(id: TrainId, name: TrainName, capacity: TrainCapacity, schedule: Schedule, location: Option[GPS] = None) {
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

  final case class TrainBuilder(id: Option[TrainId] = None,
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

    def build(): OptionalTrainV2 = (id, name, capacity, schedule) match {
      case (Some(i), Some(n), Some(c), Some(s)) => Some(TrainV2(i, n, c, s, location))
      case _ => None // return None if any of the main fields is missing.
    }
  }

  object TrainBuilder {
    // Ponemos el new para que lo diferencia del objeto TrainBuilder
    def apply(): TrainBuilder = new TrainBuilder()
  }

  sealed trait TrainName {
    def name: String
  }

  object TrainName {
    case object Talgo extends TrainName {
      override def name: String = "Talgo"
    }

    case object AVE extends TrainName {
      override def name: String = "AVE"
    }

    case object Intercity extends TrainName {
      override def name: String = "Intercity"
    }

    // Enumerate all values
    val values: Set[TrainName] = Set(Talgo, AVE, Intercity)
  }

}
