
package packageobjects.domain

import java.time.Instant

object UsoDomain extends App {

  val schedule = Schedule(Instant.now(), Instant.now().plusSeconds(3600))

  val train1 = Train("Talgo", 100, schedule, Some((40.4167, -3.70325)))

}
