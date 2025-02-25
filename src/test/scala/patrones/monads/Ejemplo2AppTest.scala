package eoi.de.examples
package patrones.monads

import org.scalatest.funsuite.AnyFunSuite

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

class Ejemplo2AppTest extends AnyFunSuite {

  val userId: String = "diego"

  test("Plane booking service test") {
    val result = Await.result(Ejemplo2App.PlaneService.book((userId,"DLT1234",2)), 5 seconds)
    assert(result.isInstanceOf[Ejemplo2App.PlaneTickets])
  }

  test("Car Rental booking service test") {
    val planeResult = Await.result(Ejemplo2App.PlaneService.book((userId, "DLT1234", 2)), 5 seconds)
    val result = Await.result(Ejemplo2App.CarRentalService.book(planeResult), 5 seconds)
    assert(result.isInstanceOf[Ejemplo2App.CarRental])
  }

  test("Hotel booking service test") {
    val result = Await.result(Ejemplo2App.HotelBookingService.book((userId, new java.util.Date, 2)), 5 seconds)
    assert(result.isInstanceOf[Ejemplo2App.HotelBooking])
  }

  test("Whole trip booking service test") {
    val planeResult = Await.result(Ejemplo2App.PlaneService.book((userId, "DLT1234", 2)), 5 seconds)
    val carResult = Await.result(Ejemplo2App.CarRentalService.book(planeResult), 5 seconds)
    val result = Await.result(Ejemplo2App.HotelService.book(carResult), 5 seconds)
    assert(result.isInstanceOf[Ejemplo2App.WholeTripResult])
  }

  test("Monad example test with flatMap") {
    val result = Await.result(Ejemplo2App.MonadExample.resultFlatMap, 10 seconds)
    assert(result.isInstanceOf[Ejemplo2App.WholeTripResult])
  }

  test("Monad example test with for comprehension") {
    val result = Await.result(Ejemplo2App.MonadExample.resultForComp, 10 seconds)
    assert(result.isInstanceOf[Ejemplo2App.WholeTripResult])
  }

}
