
package spark.sql.datasets

import org.apache.spark.sql.{Dataset, Encoder, Encoders, SparkSession}

import scala.language.implicitConversions

object EjemploDatasetsApp02 extends App {

  implicit val spark: SparkSession = SparkSession.builder()
    .appName("EjemploDatasets02App").master("local[2]").getOrCreate()

  // Ejemplos de uso de Datasets avanzados con estructuras de datos complejas
  // Datasets anidados
  case class Person(name: String, age: Int, address: Address)

  case class Address(city: String, state: String)

  val address1 = Address("Los Angeles", "California")
  val address2 = Address("New York", "New York")
  val address3 = Address("Chicago", "Illinois")

  val data = Seq(
    Person("Alice", 34, address1),
    Person("Bob", 45, address2),
    Person("Charlie", 23, address3),
  )

  // Crear un Encoder para la clase Person
  // El encoder se utiliza para serializar y deserializar objetos de tipo Person
  implicit val personEncoder: Encoder[Person] = Encoders.product[Person]

  // Crear un Dataset a partir de una secuencia de objetos de tipo Person
  // Este es el mayor cambio con respecto a los DataFrames, ya que los DataFrames no son tipados
  // toDS() es un método implícito de SparkSession que convierte una secuencia de objetos en un Dataset

  import spark.implicits._

  val ds = data.toDS()
  ds.show(false)

  // Datasets de tuplas
  case class PersonTuple(name: String, age: Int, address: (String, String))

  val dataTuple = Seq(
    PersonTuple("Alice", 34, ("Los Angeles", "California")),
    PersonTuple("Bob", 45, ("New York", "New York")),
    PersonTuple("Charlie", 23, ("Chicago", "Illinois")),
  )

  // Crear un Encoder para la clase PersonTuple
  // El encoder se utiliza para serializar y deserializar objetos de tipo PersonTuple
  implicit val personTupleEncoder: Encoder[PersonTuple] = Encoders
    .product[PersonTuple]

  val dsTuple = dataTuple.toDS()
  dsTuple.show(false)

  // Datasets de tuplas anidadas
  case class PersonNested(name: String, age: Int, address: Address)

  val dataNested = Seq(
    PersonNested("Alice", 34, address1),
    PersonNested("Bob", 45, address2),
    PersonNested("Charlie", 23, address3),
  )

  // Crear un Encoder para la clase PersonNested
  implicit val personNestedEncoder: Encoder[PersonNested] = Encoders
    .product[PersonNested]

  // Datasets de listas

  val personList1: Seq[Person] =
    List(Person("Alice", 34, address1), Person("Bob", 45, address2))
  val personList2: Seq[Person] = List(Person("Charlie", 23, address3))
  val dataLists: Seq[Seq[Person]] = List(personList1, personList2)

  // Crear un Encoder para la clase List[Person]
  // implicit val personListEncoder: Encoder[Seq[Person]] = Encoders.product[Seq[Person]]
  implicit val personListEncoder: Encoder[Seq[Person]] = Encoders
    .kryo[Seq[Person]]
  // Kryo usa serialización binaria, que es más eficiente que la serialización Java predeterminada pero no es compatible con todos los tipos de datos.
  // Tampoco se puede leer directamente por humanos porque es binario.
  val dsLists = dataLists.toDS()
  dsLists.show(false)
  dsLists.printSchema()

  val listaDs1 = personList1.toDS()
  listaDs1.show(false)
  listaDs1.printSchema()
  val listaDs2 = personList2.toDS()
  listaDs2.show(false)
  listaDs2.printSchema()

  // Datasets de mapas

  val dataMaps = Seq(
    Map("name" -> "Alice", "age" -> 34, "address" -> address1),
    Map("name" -> "Bob", "age" -> 45, "address" -> address2),
    Map("name" -> "Charlie", "age" -> 23, "address" -> address3),
  )

  // Crear un Encoder para la clase Map[String, Any]
  implicit val mapEncoder: Encoder[Map[String, Any]] = Encoders
    .kryo[Map[String, Any]]

  // val dsMaps: Dataset[Map[String, Any]] = dataMaps.toDS()

  // Datasets de mapas anidados
  case class PeopleWithAddress(name: String, age: Int, address: Address)

  val dataPeopleWithAddress: Seq[PeopleWithAddress] = Seq(
    PeopleWithAddress("Alice", 34, address1),
    PeopleWithAddress("Bob", 45, address2),
    PeopleWithAddress("Charlie", 23, address3),
  )
  val dsPeopleWithAddress: Dataset[PeopleWithAddress] =
    dataPeopleWithAddress.toDS()
  dsPeopleWithAddress.show(false)

  val dataMapsNested: Seq[Map[String, Any]] = Seq(
    Map(
      "name" -> "Alice",
      "age" -> 34,
      "address" -> Map("city" -> "Los Angeles", "state" -> "California"),
    ),
    Map(
      "name" -> "Bob",
      "age" -> 45,
      "address" -> Map("city" -> "New York", "state" -> "New York"),
    ),
    Map(
      "name" -> "Charlie",
      "age" -> 23,
      "address" -> Map("city" -> "Chicago", "state" -> "Illinois"),
    ),
  )

  // Crear un Encoder para la clase Map[String, Any]
  implicit val mapNestedEncoder: Encoder[Map[String, Any]] = Encoders
    .kryo[Map[String, Any]]

  // Map[String, Any] es un tipo de dato muy flexible que puede contener cualquier tipo de valor
  // pero al no ser determinista, Spark no puede inferir el esquema de forma automática
  /// val dsMapsNested: Dataset[Map[String, Any]] = dataMapsNested.toDS()

  // Datasets de Sets

  type NombresDeAmigos = Set[String]
  val dataSets: Seq[NombresDeAmigos] = Seq(
    Set("Alice", "Bob", "Charlie"),
    Set("David", "Eve", "Frank"),
    Set("George", "Helen", "Ivan"),
  )

  // Crear un Encoder para la clase Set[String]
  implicit val setEncoder: Encoder[NombresDeAmigos] = Encoders
    .kryo[NombresDeAmigos]

  val dsSets: Dataset[NombresDeAmigos] = dataSets.toDS()
  dsSets.show(false)
  dsSets.printSchema()
  dsSets.take(10).foreach(println)

  // Funcion implicita para convertir un Set[String] a un Dataset
  implicit def setToDataset(set: Set[String]): Dataset[String] = set.toSeq.toDS()

  val dsSets2: Dataset[String] = dataSets.toDS().flatMap(set => set)
  dsSets2.show(false)
  dsSets2.printSchema()
  dsSets2.take(10).foreach(println)

  implicit class ShowDataset[T <: Any](ds: Dataset[T]) {
    def showDS(numRecords: Int = 20, truncate: Boolean = false): Unit = ds
      .take(numRecords).foreach(record => println(record))
  }

  println("dsSets show")
  // Usamos el método showDS() de la clase ShowDataset
  dsSets.showDS()

  // Datasets de Sets anidados

  type NombresDeAmigosAnidados = Set[Set[String]]
  val dataSetsAnidados: Seq[NombresDeAmigosAnidados] = Seq(
    Set(Set("Alice", "Bob"), Set("Charlie", "David")),
    Set(Set("Eve", "Frank"), Set("George", "Helen")),
    Set(Set("Ivan", "Jack"), Set("Kate", "Liam")),
  )

  // Crear un Encoder para la clase Set[Set[String]]
  implicit val setAnidadoEncoder: Encoder[NombresDeAmigosAnidados] = Encoders
    .kryo[NombresDeAmigosAnidados]

  val dsSetsAnidados: Dataset[NombresDeAmigosAnidados] = dataSetsAnidados.toDS()
  dsSetsAnidados.printSchema()
  dsSetsAnidados.showDS()

  // Datasets de secuencias

  type NombresDeAmigosSeq = Seq[String]
  val dataSeq: Seq[NombresDeAmigosSeq] = Seq(
    Seq("Alice", "Bob", "Charlie"),
    Seq("David", "Eve", "Frank"),
    Seq("George", "Helen", "Ivan"),
  )

  // Crear un Encoder para la clase Seq[String]
  implicit val seqEncoder: Encoder[NombresDeAmigosSeq] = Encoders
    .kryo[NombresDeAmigosSeq]

  val dsSeq: Dataset[NombresDeAmigosSeq] = dataSeq.toDS()
  dsSeq.showDS(10)
  dsSeq.printSchema()
  // Datasets de secuencias anidadas

  type NombresDeAmigosSeqAnidados = Seq[Seq[String]]
  val dataSeqAnidados: Seq[NombresDeAmigosSeqAnidados] = Seq(
    Seq(Seq("Alice", "Bob"), Seq("Charlie", "David")),
    Seq(Seq("Eve", "Frank"), Seq("George", "Helen")),
    Seq(Seq("Ivan", "Jack"), Seq("Kate", "Liam")),
  )

  // Crear un Encoder para la clase Seq[Seq[String]]
  implicit val seqAnidadoEncoder: Encoder[NombresDeAmigosSeqAnidados] = Encoders
    .kryo[NombresDeAmigosSeqAnidados]

  val dsSeqAnidados: Dataset[NombresDeAmigosSeqAnidados] = dataSeqAnidados.toDS()
  dsSeqAnidados.printSchema()
  dsSeqAnidados.showDS()

  // Datasets de opciones

  type OpcionDeDireccion = Option[Address]
  val dataOptions: Seq[OpcionDeDireccion] =
    Seq(Some(address1), Some(address2), Some(address3), None)

  // Crear un Encoder para la clase Option[Address]
  implicit val optionEncoder: Encoder[OpcionDeDireccion] = Encoders
    .kryo[OpcionDeDireccion]

  val dsOptions: Dataset[OpcionDeDireccion] = dataOptions.toDS()
  dsOptions.showDS(10)
  dsOptions.printSchema()

  // Datasets de opciones anidadas
  // Option[Option[Address]] puede ser un tipo de dato útil para representar datos que pueden estar presentes o no
  // Opion[Option[T]] es un tipo de dato que puede tener dos valores: Some(Some(value)) o None y se utiliza para representar datos anidados
  type OpcionDeDireccionAnidada = Option[Option[Address]]
  val dataOptionsAnidadas: Seq[OpcionDeDireccionAnidada] =
    Seq(Some(Some(address1)), Some(Some(address2)), Some(Some(address3)), None)

  // Crear un Encoder para la clase Option[Option[Address]]
  implicit val optionAnidadoEncoder: Encoder[OpcionDeDireccionAnidada] =
    Encoders.kryo[OpcionDeDireccionAnidada]

  val dsOptionsAnidadas: Dataset[OpcionDeDireccionAnidada] =
    dataOptionsAnidadas.toDS()
  dsOptionsAnidadas.showDS(10)
  dsOptionsAnidadas.printSchema()

  // Otros ejemplos de Datasets avanzados de procesamiento funcional de datos
  // Cosas que se pueden hacer con Datasets que no se pueden hacer con DataFrames

  // Datasets de secuencias de tuplas
  val dataTuples: Seq[(String, Int, Address)] =
    Seq(("Alice", 34, address1), ("Bob", 45, address2), ("Charlie", 23, address3))

  // Crear un Encoder para la clase (String, Int, Address)
  implicit val tupleEncoder: Encoder[(String, Int, Address)] = Encoders
    .kryo[(String, Int, Address)]

  val dsTuples: Dataset[(String, Int, Address)] = dataTuples.toDS()
  dsTuples.showDS(10)
  dsTuples.printSchema()

  // Es mejor usar un case class en lugar de una tupla para representar datos estructurados?
  // Las tuplas son más eficientes que los case class porque no requieren la creación de objetos adicionales
  // Las tuplas son más fáciles de usar que los case class porque no requieren la definición de clases adicionales
  // Las tuplas son más flexibles que los case class porque pueden contener cualquier tipo de datos
  // Las tuplas son más seguras que los case class porque no permiten la creación de objetos nulos
  // Las tuplas son más fáciles de serializar que los case class porque no requieren la definición de encoders adicionales
  // Las tuplas son más fáciles de deserializar que los case class porque no requieren la definición de decoders adicionales
  // Las tuplas son más fáciles de leer que los case class porque no requieren la definición de métodos adicionales
  // Las tuplas son más fáciles de escribir que los case class porque no requieren la definición de métodos adicionales
  // Las tuplas son más fáciles de depurar que los case class porque no requieren la definición de métodos adicionales

  // Datasets de secuencias de tuplas anidadas
  val dataTuplesAnidadas: Seq[((String, Int), Address)] = Seq(
    (("Alice", 34), address1),
    (("Bob", 45), address2),
    (("Charlie", 23), address3),
  )

  // Crear un Encoder para la clase ((String, Int), Address)
  implicit val tupleAnidadoEncoder: Encoder[((String, Int), Address)] = Encoders
    .kryo[((String, Int), Address)]

  val dsTuplesAnidadas: Dataset[((String, Int), Address)] =
    dataTuplesAnidadas.toDS()

  dsTuplesAnidadas.showDS(10)
  dsTuplesAnidadas.printSchema()

  // Datasets de secuencias de tuplas anidadas con tipos de datos complejos
  type Nombre = String
  type Edad = Int
  type NombreEdad = (Nombre, Int)
  type PersonTupleAnidado = ((Nombre, Edad, Address), (Nombre, Edad, Address))
  val dataTuplesAnidadasComplejas
  : Seq[((Nombre, Edad, Address), (Nombre, Edad, Address))] = Seq(
    (("Alice", 34, address1), ("Bob", 45, address2)),
    (("Charlie", 23, address3), ("David", 56, address1)),
    (("Eve", 45, address2), ("Frank", 34, address3)),
  )

  // Crear un Encoder para la clase ((String, Int, Address), (String, Int, Address))
  implicit val tupleAnidadoComplejoEncoder
  : Encoder[((Nombre, Edad, Address), (Nombre, Edad, Address))] = Encoders
    .kryo[((Nombre, Edad, Address), (Nombre, Edad, Address))]

  val dsTuplesAnidadasComplejas
  : Dataset[((Nombre, Edad, Address), (Nombre, Edad, Address))] =
    dataTuplesAnidadasComplejas.toDS()

  dsTuplesAnidadasComplejas.showDS(10)
  dsTuplesAnidadasComplejas.printSchema()

  spark.stop()
}
