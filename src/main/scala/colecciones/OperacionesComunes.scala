
package colecciones

import scala.Console.{BOLD, GREEN, RESET}

object OperacionesComunes extends App {

  // Operaciones de transformación (map, flatMap, filter, etc.)
  Console.println(BOLD + "Operaciones de transformación (map, flatMap, filter, etc.)" + RESET)
  val lista = List(1, 2, 3, 4, 5)
  val listaDobleString: Seq[String] = lista.map((e: Int) => (e * 2).toString)
  val listaDoble: Seq[Int] = lista.map(_ * 2)
  println(s"Int: $listaDoble") // Salida: List(2, 4, 6, 8, 10)
  println(s"String: $listaDobleString")

  val listaPares: Seq[Int] = lista.filter(_ % 2 == 0)
  val condicion: Int => Boolean = (e: Int) => e % 2 == 0
  val listaPares2 = lista.filter(condicion)
  println(listaPares) // Salida: List(2, 4)

  val listaParesDoble = lista.filter(_ % 2 == 0).map(_ * 2)
  println(listaParesDoble) // Salida: List(4, 8)
  println()
  // flatMap
  val listaDeListas = List(List(1, 2), List(3, 4), List(5, 6), List(1, 3, 5), List(1, 2))

  // Ejemplo de identity
  // La función identity devuelve el mismo valor que recibe.
  val listaIdentity = listaDeListas.map(identity)
  println(BOLD + "Lista identity" + RESET)
  println(listaIdentity) // Salida: List(List(1, 2), List(3, 4), List(5, 6), List(1, 3, 5))

  // flatmap aplica una función a cada elemento de la lista y luego aplana el resultado en una sola lista.
  val listaPlana = listaDeListas.flatMap(identity) // flatten en Scala 2.13

  println(BOLD + "Lista plana" + RESET)
  println(listaPlana) // Salida: List(1, 2, 3, 4, 5, 6, 1, 3, 5)
  println(listaDeListas.flatten)
  println()

  // forEach: Aplica una función a cada elemento de la lista.
  println(BOLD + "Recorriendo la lista con foreach" + RESET)
  val resForEach: Unit = lista.foreach(println)
  println(GREEN + s" --> resForEach: $resForEach" + RESET) // Salida: ()
  println()
  println(BOLD + "Recorriendo la lista con map" + RESET)
  val resMap: Seq[Unit] = lista.map(println)
  println(GREEN + s" --> resMap: $resMap" + RESET) // Salida: List((), (), (), (), ())
  println()

  // La diferencia entre foreach y map es que map devuelve una nueva colección con los resultados de aplicar la función a cada elemento, mientras que foreach simplemente aplica la función a cada elemento sin devolver nada.

  // Operaciones de reducción (fold, reduce, etc.)
  Console.println(BOLD + "Operaciones de reducción (fold, reduce, etc.)" + RESET)
  val sumaReduce1 = lista.reduce(_ + _)
  val sumaReduce1b = lista.reduce((a, b) => a + b)
  println(s"sumaReduce1 es: $sumaReduce1") // Salida: 15
  println(s"sumaReduce1b es: $sumaReduce1b") // Salida: 15

  val sumaReduce2 = lista.sum
  println(s"sumaReduce2 es: $sumaReduce2") // Salida: 15

  // reduceLeft y reduceRight:
  // reduceLeft aplica la función a los elementos de la lista de izquierda a derecha.
  // reduceRight aplica la función a los elementos de la lista de derecha a izquierda.
  // En este caso, la suma es conmutativa, por lo que el resultado es el mismo.
  val sumaReduce3 = lista.reduceLeft(_ + _)
  println(s"sumaReduce3 es: $sumaReduce3") // Salida: 15
  val sumaReduce4 = lista.reduceRight(_ + _)
  println(s"sumaReduce4 es: $sumaReduce4") // Salida: 15

  // fold y foldLeft:
  // foldLeft aplica la función a los elementos de la lista de izquierda a derecha, con un valor inicial.
  // foldRight aplica la función a los elementos de la lista de derecha a izquierda, con un valor inicial.
  val sumaReduce5 = lista.fold(10)(_ + _)
  println(s"sumaReduce5 es: $sumaReduce5") // Salida: 15

  println(s" ---- Fold con lista vacía: ${List.empty[Int].fold(10)(_ + _)}")
  val sumaReduce6 = lista.foldLeft(2)(_ + _)
  println(s"sumaReduce6 es: $sumaReduce6") // Salida: 15
  val sumaReduce7 = lista.fold(0)(_ + _)
  println(s"sumaReduce7 es: $sumaReduce7") // Salida: 15

  val listaNoConmutativa = List(1, 2, 3, 4, 5) // Declaramos una lista

  val restaReduce = listaNoConmutativa.reduce(_ - _)
  println(s"restaReduce es: $restaReduce") // Salida: -13
  // ReduceLeft y reduceRight:
  // reduceLeft aplica la función a los elementos de la lista de izquierda a derecha.
  // reduceRight aplica la función a los elementos de la lista de derecha a izquierda.
  // En este caso, la resta no es conmutativa, por lo que el resultado es diferente.
  val restaReduceLeft = listaNoConmutativa.reduceLeft(_ - _)
  println(s"restaReduceLeft es: $restaReduceLeft") // Salida: -13
  val restaReduceRight = listaNoConmutativa.reduceRight(_ - _)
  println(s"restaReduceRight es: $restaReduceRight") // Salida: 3

  // fold y foldLeft:
  // foldLeft aplica la función a los elementos de la lista de izquierda a derecha, con un valor inicial.
  // foldRight aplica la función a los elementos de la lista de derecha a izquierda, con un valor inicial.
  val restaFold = listaNoConmutativa.fold(10)(_ - _)
  println(s"restaFold es: $restaFold") // Salida: -5
  val restaFoldLeft = listaNoConmutativa.foldLeft(2)(_ - _)
  println(s"restaFoldLeft es: $restaFoldLeft") // Salida: -13
  val restaFoldRight = listaNoConmutativa.foldRight(0)(_ - _)
  println(s"restaFoldRight es: $restaFoldRight") // Salida: 3
  println()

  // Operaciones de agrupación (groupBy, partition, etc.)
  // val lista = List(1, 2, 3, 4, 5)
  Console.println(BOLD + "Operaciones de agrupación (groupBy, partition, etc.)" + RESET)
  val listaParesImpares: (List[Int], List[Int]) = lista.partition(_ % 2 == 0)
  val listaPares3 = listaParesImpares._1
  val listaImpares3 = listaParesImpares._2
  // O también se puede hacer así:
  val (listaParesNueva, listaImparesNueva) = lista.partition(_ % 2 == 0)

  Console.println("listaPares3: " + listaPares3) // Salida: List(2, 4)
  Console.println("listaImpares3: " + listaImpares3) // Salida: List(1, 3, 5)


  println("listaParesImpares: " + listaParesImpares) // Salida: (List(2, 4),List(1, 3, 5))
  println()
  // Agrupar sin son par o impar
  // groupBy agrupa los elementos de la lista en un mapa según el resultado de aplicar una función.
  val listaAgrupada = lista.groupBy(_ % 2)
  println("listaAgrupada: " + listaAgrupada) // Salida: Map(1 -> List(1, 3, 5), 0 -> List(2, 4))
  println()
  //System.exit(0)
  // Operaciones de ordenación (sorted, sortBy, etc.)
  Console.println(BOLD + "Operaciones de ordenación (sorted, sortBy, etc.)" + RESET)
  val listaDesordenada = List(3, 1, 5, 2, 4)
  val listaOrdenada = listaDesordenada.sorted
  println("listaOrdenada: " + listaOrdenada) // Salida: List(1, 2, 3, 4, 5)

  val listaOrdenadaDesc = listaDesordenada.sorted(Ordering[Int].reverse)
  println("listaOrdenadaDesc: " + listaOrdenadaDesc) // Salida: List(5, 4, 3, 2, 1)

  val listaOrdenadaPorLongitud = List("hola", "adios", "buenos días", "buenas noches")

  // sortBy ordena la lista según el resultado de aplicar una función.
  val listaOrdenadaPorLongitudAsc = listaOrdenadaPorLongitud.sortBy(_.length)
  println("listaOrdenadaPorLongitudAsc: " + listaOrdenadaPorLongitudAsc) // Salida: List(hola, adios, buenas noches, buenos días)

  val listaOrdenadaPorLongitudDesc = listaOrdenadaPorLongitud
    .sortBy(_.length)(Ordering[Int].reverse)
  println("listaOrdenadaPorLongitudDesc: " + listaOrdenadaPorLongitudDesc) // Salida: List(buenos días, buenas noches, adios, hola)
  println()

  // Operaciones de combinación (zip, zipWithIndex, etc.)
  // val lista = List(1, 2, 3, 4, 5)
  Console.println(BOLD + "Operaciones de combinación (zip, zipWithIndex, etc.)" + RESET)
  val lista1 = List(1, 2, 3)
  val lista2 = List("a", "b", "c")
  // zip combina dos listas en una lista de tuplas.
  val listaCombinada = lista1.zip(lista2)
  println("listaCombinada: " + listaCombinada) // Salida: List((1,a), (2,b), (3,c))

  // zipWithIndex combina los elementos de la lista con sus índices.
  val listaConIndice = lista.zipWithIndex
  // val lista = List(1, 2, 3, 4, 5)
  println("listaConIndice: " + listaConIndice) // Salida: List((1,0), (2,1), (3,2), (4,3), (5,4))
  println()

  // Operaciones de conversión (toArray, toList, etc.)
  Console.println(BOLD + "Operaciones de conversión (toArray, toList, etc.)" + RESET)
  val array = lista.toArray
  println("array: " + array.mkString(", ")) // Salida: 1, 2, 3, 4, 5

  val listaDeArray = array.toList
  println("listaDeArray: " + listaDeArray) // Salida: List(1, 2, 3, 4, 5)
  println()

  // Operaciones de búsqueda (find, exists, etc.)
  Console.println(BOLD + "Operaciones de búsqueda (find, exists, etc.)" + RESET)
  val listaDeNombres = List("Juan", "Pedro", "Luis", "Ana", "Maria")
  // find devuelve el primer elemento que cumple una condición.
  val nombreEncontrado = listaDeNombres.find(_.startsWith("A"))
  println("nombreEncontrado: " + nombreEncontrado) // Salida: Some(Ana)


  val nombreExiste = listaDeNombres.exists(_.startsWith("P"))
  println("nombreExiste: " + nombreExiste) // Salida: true
  println()

  // Operaciones de filtrado (drop, dropWhile, etc.)
  Console.println(BOLD + "Operaciones de filtrado (drop, dropWhile, etc.)" + RESET)
  val listaFiltrada = lista.drop(2)
  println("listaFiltrada: " + listaFiltrada) // Salida: List(3, 4, 5)

  val listaFiltradaWhile = lista.dropWhile(_ < 3)
  println("listaFiltradaWhile: " + listaFiltradaWhile) // Salida: List(3, 4, 5)
  println()

  // Operaciones de partición (splitAt, span, etc.)
  Console.println(BOLD + "Operaciones de partición (splitAt, span, etc.)" + RESET)
  // splitAt divide la lista en dos partes en la posición especificada.
  val (primeraParte, segundaParte) = lista.splitAt(3)
  println("primeraParte: " + primeraParte) // Salida: List(1, 2, 3)
  println("segundaParte: " + segundaParte) // Salida: List(4, 5)

  // span divide la lista en dos partes según una condición.
  val (parte1, parte2) = lista.span(_ < 3)
  println("parte1: " + parte1) // Salida: List(1, 2)
  println("parte2: " + parte2) // Salida: List(3, 4, 5)
  println()

  // Operaciones de extracción (head, tail, etc.)
  Console.println(BOLD + "Operaciones de extracción (head, tail, etc.)" + RESET)
  val primerElemento = lista.head
  println("primerElemento: " + primerElemento) // Salida: 1

  val restoElementos = lista.tail
  println("restoElementos: " + restoElementos) // Salida: List(2, 3, 4, 5)
  println()

  // Distinct
  Console.println(BOLD + "Operaciones de eliminación de duplicados (distinct)" + RESET)
  val listaRepetida = List(1, 2, 3, 1, 2, 3, 4, 5)
  val listaSinRepetidos = listaRepetida.distinct
  println("listaSinRepetidos: " + listaSinRepetidos) // Salida: List(1, 2, 3, 4, 5)
  println()

  // Operaciones de comparación (max, min, etc.)
  Console.println(BOLD + "Operaciones de comparación (max, min, etc.)" + RESET)
  val maximo = lista.max
  println("maximo: " + maximo) // Salida: 5

  val minimo = lista.min
  println("minimo: " + minimo) // Salida: 1
  println()

  // Transformaciones de colecciones: transform
  Console.println(BOLD + "Transformaciones de colecciones: transform" + RESET)
  val funcionTransformacion1 = (x: Int) => x * 2
  val funcionTransformacion2 = (x: Int) => x + 1
  val listaTransformada = lista.map(funcionTransformacion1)
    .map(funcionTransformacion2)
  println("listaTransformada: " + listaTransformada) // Salida: List(3, 5, 7, 9, 11)
  println()
  val listaFinal = lista.map(funcionTransformacion1 andThen funcionTransformacion2)
  println("listaFinal: " + listaFinal) // Salida: List(3, 5, 7, 9, 11)
}

