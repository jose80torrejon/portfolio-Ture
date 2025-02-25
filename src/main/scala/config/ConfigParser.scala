
package config

import com.typesafe.config.Config

// Ejemplo de uso de Typesafe Config
// El fichero de configuración se encuentra en src/main/resources/application.conf
class ConfigParser {
  lazy val config: Config = getConfig

  // Devuelve la configuración completa
  def getConfig: com.typesafe.config.Config = {
    // Para cargar la configuración se utiliza ConfigFactory.load()
    import com.typesafe.config.ConfigFactory
    ConfigFactory.load()
  }

  // Devuelve el valor de la clave "app.version" como String
  def getAppVersion: String = config.getString("app.version")

  // Devuelve el valor de la clave "app.name" como String
  def getAppName: String = config.getString("app.name")

  def kafkaBroker: String = config
    .getString("kafka.broker.valor_por_defecto")

  def titulo: String = config.getString("titulo")

}

object ConfigParser {
  def apply(): ConfigParser = new ConfigParser()
}

object Main extends App {
  val configParser = ConfigParser()
  println(configParser.getAppVersion)
  println(configParser.getAppName)

  /*
  app {
    name = "Ejemplo de uso de config"
    version = "1.0.0"

    # Ejemplo de array
    array = [1, 2, 3, 4, 5]

    # Ejemplo de objeto
    object = {
        key1 = "value1"
        key2 = "value2"
        key3 = "value3"
    }

}
   */

  // Acceso a un arrary
  val array = configParser.config.getIntList("app.array")
  println(array)
  // Acceso a un objeto
  val obj = configParser.config.getObject("app.object")
  println(obj)
  // Acceso a un valor de un objeto
  val key1 = configParser.config.getString("app.object.key1")
  println(key1)

  // Acceso a un valor de un array
  val value1 = obj.toConfig.getString("key1")
  println(value1)

  // Fichero reference.conf
  // Se puede utilizar un fichero reference.conf para definir valores por defecto
  // Se puede sobreescribir en application.conf
  // Se puede utilizar un fichero application.conf para definir valores específicos

  // Ejemplo de reference.conf
  // kafka {
  //   broker = "localhost:9092"
  // }
  val kafkaBroker = configParser.config.getString("kafka.broker.valor_por_defecto")
  println(configParser.kafkaBroker)

  // Titulo: Está tanto en el reference.conf como en el application.conf

  println(s"Titulo es: ${configParser.titulo}")


  // Ejemplo de inclusión de ficheros de configuración
  // Se puede incluir un fichero de configuración en otro
  // Se puede utilizar include "nombre_fichero.conf" para incluir un fichero de configuración
  val bdProd = configParser.config.getString("bd.prod.url")
  println(bdProd)

}
