# PRIMEROS PASOS Y CONFIGURACIÓN NECESARIA

## Configuración del proyecto en IntelliJ:
Project Structure->
1) Platform Settings (SDK) -> Download SDK -> GraalVM Comunity Edition (Version 21). Comienza proceso de Indexación y demás (tiempo).
2) Global Libraries (Scala) -> + (New Global Library) -> Coursier (Version 2.13.14)

Asegurarnos que en Project coge en SDK la Graal 21 y en Language level seleccionar 21 - Record patterns, pattern matching for switch.

## Primera Ejecución (Errores típicos):
1) La primera vez que ejecutamos la aplicación vamos a tener este mensaje de error, es debido a que no está cogiendo las dependencias marcadas como provided en el SBT:
Error: Unable to initialize main class...
Caused by: java.lang.NoClassDefFoundError: org/apache/spark...

Hay que ir a la configuracion de la ejecución  -> Edit Configuration -> Modify Options -> Activar "Add dependencies with "provided" scope to classpath"

2) Obtendremos un segundo mensaje de error similar a este:

class org.apache.spark.storage.StorageUtils$(in unnamed module @0x378542de)cannot access class sun.nio.ch.DirectBuffer

Es debido a las restricciones de seguridad de la maquina de Java, para solucionarlo hay que añadir estas líneas al marcar la opción "Add VM Options" en la configuración de ejecución:

--add-opens=java.base/java.lang=ALL-UNNAMED

--add-opens=java.base/java.util=ALL-UNNAMED

--add-opens=java.base/java.io=ALL-UNNAMED

--add-opens=java.base/java.util.concurrent=ALL-UNNAMED

--add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED

--add-opens=java.base/java.util.concurrent.locks=ALL-UNNAMED

--add-opens=java.base/java.util.regex=ALL-UNNAMED

--add-opens=java.base/java.util.stream=ALL-UNNAMED

--add-opens=java.base/java.util.function=ALL-UNNAMED

--add-opens=java.base/java.util.jar=ALL-UNNAMED

--add-opens=java.base/java.util.zip=ALL-UNNAMED

--add-opens=java.base/java.util.spi=ALL-UNNAMED

--add-opens=java.base/java.lang.invoke=ALL-UNNAMED

--add-opens=java.base/java.lang.reflect=ALL-UNNAMED

--add-opens=java.base/java.net=ALL-UNNAMED

--add-opens=java.base/java.nio=ALL-UNNAMED

--add-opens=java.base/sun.nio.ch=ALL-UNNAMED

--add-opens=java.base/sun.nio.cs=ALL-UNNAMED

--add-opens=java.base/sun.security.action=ALL-UNNAMED

--add-opens=java.base/sun.util.calendar=ALL-UNNAMED

--add-opens=java.security.jgss/sun.security.krb5=ALL-UNNAMED

Para no tener que hacer siempre estos 2 pasos, se puede añadir esta configuración en Edit Templates del proyecto para que la coja para todas las aplicaciones.


## Variables de entorno necesarias:
1) Descargar la carpeta "winutil\shadoop-2.7.1" de este repo (https://github.com/steveloughran/winutils/tree/master),y te lo copias en local (C:\winutils\hadoop-2.7.1)

2) Descargar la carpeta "spark-2.4.4-bin-hadoop2.7" del zip "spark-2.4.4-bin-hadoop2.7.tgz" , yo lo he cogido de esta ruta (https://archive.apache.org/dist/spark/spark-2.4.4/) y te lo copias en local (C:\spark-2.4.4-bin-hadoop2.7)

3) Añades las 2 variables de entorno en Windows:
HADOOP_HOME --> C:\winutils\hadoop-2.7.1
SPARK_HOME --> C:\spark-2.4.4-bin-hadoop2.7\bin

4) Añadir a la variable PATH las 2 rutas:
C:\winutils\hadoop-2.7.1\bin
C:\spark-2.4.4-bin-hadoop2.7\bin


## Últimas versiones recomendadas para evitar que cada vez se nos descargue unas diferentes:
- project\build.properties SBT -> 1.10.7
- project\plugins.sbt sbt-scalafmt -> 2.5.4
- build.sbt

    Spark: 3.5.4

    Scala: 2.13.16

    Scalafmt: 3.8.6
    
    Scalatest: 3.2.19