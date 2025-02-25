#!/usr/bin/env bash

# Suponemos que tenemos un cluster de 3 nodos con las siguientes IPs:
# worker1: 192.168.13.11
# worker2: 192.168.13.12
# worker3: 192.168.13.13

# Y tenemos el master en:
# master: 192.168.13.10

# Suponemos que tenemos instalado spark en /opt/spark y que todos los servidores tienen el mismo usuario y contraseña
# usuario: spark-devops
# contraseña: spark-devops

# Primero iniciamos el master
ssh spark-devops@master '/opt/spark/sbin/start-master.sh'

ssh spark-devops@master '/opt/spark/bin/spark-class org.apache.spark.deploy.master.Master --properties-file conf/spark-defaults.conf -c 2 -m 2g'


# Tras esto en el navegador podemos acceder a la interfaz web de spark en http://master:8080
# En la interfaz web podemos ver la URL del master, que será algo como spark://master:7077

# Entramos por ssh a cada uno de los nodos y ejecutamos el siguiente comando:
ssh spark-devops@worker1 '/opt/spark/sbin/start-worker.sh spark://master:7077 -c 4 -m 10g'

ssh spark-devops@worker2 '/opt/spark/sbin/start-worker.sh spark://master:7077 -c 4 -m 10g'

ssh spark-devops@worker3 '/opt/spark/sbin/start-worker.sh spark://master:7077 -c 4 -m 10g'

# Tras esto ya tendremos un cluster de spark funcionando con un master y 3 workers
# Para lanzar aplicaciones en el cluster podemos hacerlo desde el master o desde cualquier otro nodo con:
/opt/spark/bin/spark-submit --master spark://master:7077 /apps/spark-eoi-assembly.jar
# O también podemos hacerlo desde la interfaz web de spark en http://master:8080
# En la interfaz web podemos ver las aplicaciones que se están ejecutando y su estado

# O si desde el master tenemos instalados los certificados de los nodos, podemos iniciar o parar el cluster con:
# Para ello el fichero /opt/spark/conf/slaves debe contener las IPs de los nodos
/opt/spark/sbin/start-all.sh
/opt/spark/sbin/start-slave.sh
/opt/spark/sbin/stop-all.sh
/opt/spark/sbin/stop-slave.sh