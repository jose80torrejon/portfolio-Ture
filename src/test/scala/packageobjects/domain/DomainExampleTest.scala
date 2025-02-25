package eoi.de.examples
package packageobjects.domain

import org.scalatest.funsuite.AnyFunSuite

import java.time.Instant

class DomainExampleTest extends AnyFunSuite {
  test("train builder should build a train v2") {
    println("DomainExampleTest.train builder should build a train v2")
    val schedule = Schedule(Instant.now(), Instant.now().plusSeconds(3600))
    println(schedule)
    val trainBuilder: TrainBuilder = TrainBuilder()
    println(trainBuilder)
    val train2: Option[TrainV2] = trainBuilder
      .withId(TrainId(1))
      .withName(TrainName("Talgo"))
      .withCapacity(TrainCapacity(100))
      .withSchedule(schedule)
      .withLocation((40.4167, -3.70325))
      .build()

    println()
    assert(train2.isDefined)
    assert(train2.get.id == TrainId(1))
    assert(train2.get.name == TrainName("Talgo"))
    assert(train2.get.capacity == TrainCapacity(100))
    assert(train2.get.schedule == schedule)
    assert(train2.get.location.contains((40.4167, -3.70325)))
  }

  test("TrainBuilder.build should return None if any of the main fields is missing") {
    val trainBuilder: TrainBuilder = TrainBuilder()
    val train2: Option[TrainV2] = trainBuilder.build()

    assert(train2.isEmpty)
  }
}