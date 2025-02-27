package manejo_bases_datos

import org.apache.spark.sql.SparkSession

object LeerTabla extends App {
  private val spark = SparkSession.builder().appName("de-with-scala").master("local[*]").getOrCreate()

  //AEROPUERTOS
  //Conexión a la bbdd MySql que me creado en local con los ddls de la carpeta manejo_base_datos
  private val airportsDF = spark.read.format("jdbc").option("url", "jdbc:mysql://localhost:3306/my_db").option("user", "root").option("password", "admin") // replace the password
    .option("dbtable", "airports")
    .load()
  airportsDF.show()

  // Listamos los aeropuertos de la ciudad de New York
  airportsDF.createOrReplaceTempView("aeropuertos")
  spark.sql("SELECT * FROM aeropuertos WHERE city='New York'").show()

  // AEROLINEAS
  private val airlinesDF = spark.read.format("jdbc").option("url", "jdbc:mysql://localhost:3306/my_db").option("user", "root").option("password", "admin") // replace the password
    .option("dbtable", "airlines")
    .load()
  airlinesDF.show()

  // Contamos los aerolineas disponibles
  airlinesDF.createOrReplaceTempView("aerolineas")
  spark.sql("SELECT count(*) FROM aerolineas").show()


/* Conexión con PostgreSQL local
  val jdbcDF = session.read
    .format("jdbc")
    .option("url", "jdbc:postgresql:/localhost:5432/my_db")
    .option("dbtable", "airports")
    .option("user", "postgres")
    .option("password", "admin")
    .load()
*/

  spark.stop()


}