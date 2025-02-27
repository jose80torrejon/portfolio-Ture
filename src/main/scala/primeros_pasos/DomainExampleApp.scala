package primeros_pasos

import java.time.Instant

object DomainExampleApp extends App {

  val schedule = Schedule(Instant.now(), Instant.now().plusSeconds(3600))
  println(schedule.toString)
  val madridStation = Station("Madrid", (40.4167, -3.70325))
  println(madridStation.toString)
  val talgoTrain: Train = Train(TrainName.Talgo, 100, schedule, Some((40.4167, -3.70325)))
  println(talgoTrain.toString)


  val trainBuilder: TrainBuilder = TrainBuilder()
  val talgoTrain2: OptionalTrainV2 = trainBuilder
    .withId(TrainId(1))
    .withName(TrainName.Talgo)
    .withCapacity(TrainCapacity(100))
    .withSchedule(schedule)
    .withLocation((40.4167, -3.70325))
    .build()

  println(talgoTrain2)
  assert(talgoTrain2.isDefined)

  val talgoTrain3: OptionalTrainV2 = trainBuilder
    .withId(TrainId(1))
    .withName(TrainName.Talgo)
    .withCapacity(TrainCapacity(100))
    .withSchedule(schedule)
    .build()
  println(s" ¿Son iguales?")
  println(talgoTrain2.get)
  println(talgoTrain3.get)
  println(talgoTrain3.get.equals(talgoTrain2.get))
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