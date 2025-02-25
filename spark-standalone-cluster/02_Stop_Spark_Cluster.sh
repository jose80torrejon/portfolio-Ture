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
ssh spark-devops@master '/opt/spark/sbin/stop-master.sh'
# Tras esto en el navegador podemos acceder a la interfaz web de spark en http://master:8080
# En la interfaz web podemos ver la URL del master, que será algo como spark://master:7077

# Entramos por ssh a cada uno de los nodos y ejecutamos el siguiente comando:
ssh spark-devops@worker1 '/opt/spark/sbin/stop-slave.sh spark://master:7077'

ssh spark-devops@worker2 '/opt/spark/sbin/stop-slave.sh spark://master:7077'

ssh spark-devops@worker3 '/opt/spark/sbin/stop-slave.sh spark://master:7077'

