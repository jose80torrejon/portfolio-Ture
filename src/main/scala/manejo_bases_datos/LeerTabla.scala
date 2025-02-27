package manejo_bases_datos

import org.apache.spark.sql.{DataFrame, SparkSession}

object LeerTablaAppFunctions {
  // Constantes para la configuración de la base de datos
  private val dbUrl = "jdbc:mysql://localhost:3306/my_db"
  private val dbUser = "root"
  private val dbPassword = "admin"

  // Función para cargar una tabla desde la base de datos
  def cargarTabla(nombreTabla: String)(implicit spark: SparkSession): DataFrame = {
    spark.read
      .format("jdbc")
      .option("url", dbUrl)
      .option("user", dbUser)
      .option("password", dbPassword)
      .option("dbtable", nombreTabla)
      .load()
  }
}
object LeerTablaApp extends App {
  import LeerTablaAppFunctions._
  implicit val spark: SparkSession = SparkSession.builder().appName("de-with-scala").master("local[*]").getOrCreate()

  //AEROPUERTOS
  //Conexión a la bbdd MySql que me creado en local con los ddls de la carpeta manejo_base_datos
  private val airportsDF = cargarTabla("airports")

  airportsDF.show()

  // Listamos los aeropuertos de la ciudad de New York
  airportsDF.createOrReplaceTempView("aeropuertos")
  spark.sql("SELECT * FROM aeropuertos WHERE city='New York'").show()

  // AEROLINEAS
  private val airlinesDF = cargarTabla("airlines")

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