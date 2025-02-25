
val immSet = Set(1, 2, 3, 4, 5)

val newImmSet = immSet + 6

val removedImmSet = newImmSet - 1

import scala.collection.mutable
val mutSet = mutable.Set(1, 2, 3, 4, 5)

mutSet += 6

mutSet

mutSet -= 1
