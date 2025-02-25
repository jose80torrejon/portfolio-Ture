#!/usr/bin/env bash

export JAVA_HOME=/usr/lib/jvm/java-1.21.0-openjdk-amd64/
export PATH=$JAVA_HOME/bin:$PATH

#export TEMP=./tmp
#export TMP=./tmp
#export TMPDIR=./tmp

rm -rf ../target
rm -rf ../project/target
rm -rf ../project/project

cd ..
sbt assembly

ls -lh target/scala-2.13/spark-eoi.jar


# ejecutamos el jar con spark-submit
/home/mario/Development/spark-3.5.0/bin/spark-submit --class eoi.de.examples.spark.sql.datasets.EjemploDatasetsApp03 --executor-memory 2g --master local[2] target/scala-2.13/spark-eoi.jar

cd -