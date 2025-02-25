Vamos a ver cómo podemos usar GROUPING SETS y ROLLUP en Spark SQL para realizar operaciones de agregación más avanzadas.

```scala
object GroupingSetsApp extends App with SparkSessionWrapper {

spark.sparkContext.setLogLevel("ERROR")

// Desactivamos AQE
spark.conf.set("spark.sql.adaptive.enabled", "false")
// Desactivamos el broadcast join
spark.conf.set("spark.sql.autoBroadcastJoinThreshold", "-1")

import spark.implicits._

val data = Seq(
("Banana", "Fruit", 1000, 1),
("Carrot", "Vegetable", 1000, 1),
("Bean", "Vegetable", 2000, 2),
("Orange", "Fruit", 2000, 2),
("Banana", "Fruit", 4000, 3),
("Carrot", "Vegetable", 4000, 3),
("Bean", "Vegetable", 3000, 0 ),
("Orange", "Fruit", 3000, 0)
)


val df = data.toDF("Name", "Category", "Sales", "Quantity")

df.createOrReplaceTempView("sales_data")

val result = spark.sql("SELECT Name, Category, sum(Sales) FROM sales_data GROUP BY Name, Category GROUPING SETS((Name, Category))")

// Idem. pero con el método groupingSets
// Usamos rollup para obtener el mismo resultado que con grouping sets
val resultDf = df.groupBy("Name", "Category").agg(functions.sum("Sales")).rollup("Name", "Category").count()

result.explain(true)
resultDf.explain(true)
````

Verás con estos operadores que se crea un paso con el operador `Expand`.

`Expand` se encarga de añadir una columna adicional a la salida de la tabla local.

En este caso, añade una columna adicional llamada `spark_grouping_id` que se utiliza para identificar los grupos.

```json
== Physical Plan ==
*(2) HashAggregate(keys=[Name#26, Category#27, spark_grouping_id#25L], functions=[sum(Sales#15)], output=[Name#26, Category#27, sum(Sales)#22L])
+- Exchange hashpartitioning(Name#26, Category#27, spark_grouping_id#25L, 200), ENSURE_REQUIREMENTS, [plan_id=20]
+- *(1) HashAggregate(keys=[Name#26, Category#27, spark_grouping_id#25L], functions=[partial_sum(Sales#15)], output=[Name#26, Category#27, spark_grouping_id#25L, sum#32L])
+- *(1) Expand [[Sales#15, Name#23, Category#24, 0]], [Sales#15, Name#26, Category#27, spark_grouping_id#25L]
+- *(1) LocalTableScan [Sales#15, Name#23, Category#24]
```

`+- *(1) Expand` se encarga de añadir una columna adicional a la salida de la tabla local.

En este caso, añade una columna adicional llamada `spark_grouping_id` que se utiliza para identificar los grupos.

### ¿Cuál dirías que es más óptimo?

#### Usando GROUPING SETS:

```json
== Physical Plan ==
*(2) HashAggregate(keys=[Name#26, Category#27, spark_grouping_id#25L], functions=[sum(Sales#15)], output=[Name#26, Category#27, sum(Sales)#22L])
+- Exchange hashpartitioning(Name#26, Category#27, spark_grouping_id#25L, 200), ENSURE_REQUIREMENTS, [plan_id=20]
+- *(1) HashAggregate(keys=[Name#26, Category#27, spark_grouping_id#25L], functions=[partial_sum(Sales#15)], output=[Name#26, Category#27, spark_grouping_id#25L, sum#54L])
+- *(1) Expand [[Sales#15, Name#23, Category#24, 0]], [Sales#15, Name#26, Category#27, spark_grouping_id#25L]
+- *(1) LocalTableScan [Sales#15, Name#23, Category#24]
```

#### Usando ROLLUP:

```json
== Physical Plan ==
*(3) HashAggregate(keys=[Name#48, Category#49, spark_grouping_id#47L], functions=[count(1)], output=[Name#48, Category#49, count#44L])
+- Exchange hashpartitioning(Name#48, Category#49, spark_grouping_id#47L, 200), ENSURE_REQUIREMENTS, [plan_id=67]
+- *(2) HashAggregate(keys=[Name#48, Category#49, spark_grouping_id#47L], functions=[partial_count(1)], output=[Name#48, Category#49, spark_grouping_id#47L, count#57L])
+- *(2) Expand [[Name#13, Category#14, 0], [Name#13, null, 1], [null, null, 3]], [Name#48, Category#49, spark_grouping_id#47L]
+- *(2) HashAggregate(keys=[Name#13, Category#14], functions=[], output=[Name#13, Category#14])
+- Exchange hashpartitioning(Name#13, Category#14, 200), ENSURE_REQUIREMENTS, [plan_id=61]
+- *(1) HashAggregate(keys=[Name#13, Category#14], functions=[], output=[Name#13, Category#14])
+- *(1) LocalTableScan [Name#13, Category#14]
```

Ambos planes físicos realizan operaciones similares de agrupación y agregación, y ambos requieren comparables cantidades
de procesamiento y recursos de memoria.

Sin embargo, hay algunas diferencias clave que podrían influir en la eficiencia del procesamiento según el tamaño y
naturaleza de tus datos:

- El plan con `GROUPING SETS` realiza una única agregación parcial en la etapa 1.
    - La función `partial_sum` se calcula para cada combinación distinta de Name y Category.
    - Luego, en la etapa 2, se realiza una agregación final para combinar los resultados parciales.
- El plan con `ROLLUP`, realiza una transformación adicional `Expand` en la etapa 2 que genera las diferentes
  combinaciones de valores para Name y Category.
    - Este paso adicional puede tomar más tiempo y memoria, sobre todo si tienes un gran número de combinaciones
      distintas.
    - Sin embargo, el plan con `ROLLUP` también puede ser más eficiente en algunos casos, ya que puede reducir la
      cantidad de datos que se deben procesar en la etapa final de agregación.
- Ambos planes involucran una operación de `Exchange`, que reparte los datos entre las particiones basándose en los
  valores de las claves de agrupación. Si tienes una gran cantidad de datos, esto podría influir en el rendimiento, pues
  una operación de `Exchange` puede implicar un costoso trasvase de datos en la red.
- Generalmente, para conjuntos de datos grandes, `GROUPING SETS` pueden ser más eficientes que `ROLLUP` debido a
  que `ROLLUP` requiere una etapa adicional de expansión de los datos.
- Sin embargo, la eficiencia relativa de estos dos métodos puede depender de otros factores también, como la
  distribución de tus datos y la configuración de tu cluster de Spark.

## Bucketización de datos

```scala
object GroupingSets02App extends App with SparkSessionWrapper {

  spark.sparkContext.setLogLevel("ERROR")

  // Desactivamos AQE
  spark.conf.set("spark.sql.adaptive.enabled", "false")
  // Desactivamos el broadcast join
  spark.conf.set("spark.sql.autoBroadcastJoinThreshold", "-1")

  import spark.implicits._

  val data = Seq(
    ("Banana", "Fruit", 1000, 1),
    ("Carrot", "Vegetable", 1000, 1),
    ("Bean", "Vegetable", 2000, 2),
    ("Orange", "Fruit", 2000, 2),
    ("Banana", "Fruit", 4000, 3),
    ("Carrot", "Vegetable", 4000, 3),
    ("Bean", "Vegetable", 3000, 0),
    ("Orange", "Fruit", 3000, 0)
  )

  // Generamos usa collección de datos enorme a partir de la colección de datos anterior
  // La función flatMap aplica la función que recibe a cada elemento de la colección y devuelve una colección de elementos
  val data2 = (0 until 1000000).flatMap { _ =>
    data
  }

  val BucketSize = 4
  val BucketColumn = "Name"
  // Seguimos el critrio de los NoSQL y usamos el nombre de la tabla como el nombre de la columna de bucketing
  val TablaPorNombre = "sales_data_by_name"

  val df = data2.toDF(BucketColumn, "Category", "Sales", "Quantity")

  // Vamos a usar bucketing para mejorar el rendimiento
  df.write.bucketBy(BucketSize, BucketColumn).saveAsTable(TablaPorNombre)

  val dfConBucketing = spark.table(TablaPorNombre)

  val result = spark.sql(s"SELECT $BucketColumn, Category, sum(Sales) FROM $TablaPorNombre GROUP BY $BucketColumn, Category GROUPING SETS(($BucketColumn, Category))")

  // Idem. pero con el método groupingSets
  // Usamos rollup para obtener el mismo resultado que con grouping sets
  val resultDf = dfConBucketing.groupBy(BucketColumn, "Category").agg(functions.sum("Sales")).rollup(BucketColumn, "Category").count()

  result.explain(true)
  resultDf.explain(true)

  result.show()
}
```

### Con GROUPING SETS

```json
== Parsed Logical Plan ==
'Aggregate [groupingsets(Vector(0, 1), 'Name, 'Category, 'Name, 'Category)], ['Name, 'Category, unresolvedalias('sum('Sales), None)]
+- 'UnresolvedRelation [sales_data_by_name], [], false

== Analyzed Logical Plan ==
Name: string, Category: string, sum(Sales): bigint
Aggregate [Name#38, Category#39, spark_grouping_id#37L], [Name#38, Category#39, sum(Sales#27) AS sum(Sales)#34L]
+- Expand [[Name#25, Category#26, Sales#27, Quantity#28, Name#35, Category#36, 0]], [Name#25, Category#26, Sales#27, Quantity#28, Name#38, Category#39, spark_grouping_id#37L]
+- Project [Name#25, Category#26, Sales#27, Quantity#28, Name#25 AS Name#35, Category#26 AS Category#36]
+- SubqueryAlias spark_catalog.default.sales_data_by_name
+- Relation spark_catalog.default.sales_data_by_name[Name#25,Category#26,Sales#27,Quantity#28] parquet

== Optimized Logical Plan ==
Aggregate [Name#38, Category#39, spark_grouping_id#37L], [Name#38, Category#39, sum(Sales#27) AS sum(Sales)#34L]
+- Expand [[Sales#27, Name#25, Category#26, 0]], [Sales#27, Name#38, Category#39, spark_grouping_id#37L]
+- Project [Sales#27, Name#25, Category#26]
+- Relation spark_catalog.default.sales_data_by_name[Name#25,Category#26,Sales#27,Quantity#28] parquet

== Physical Plan ==
*(2) HashAggregate(keys=[Name#38, Category#39, spark_grouping_id#37L], functions=[sum(Sales#27)], output=[Name#38, Category#39, sum(Sales)#34L])
+- Exchange hashpartitioning(Name#38, Category#39, spark_grouping_id#37L, 200), ENSURE_REQUIREMENTS, [plan_id=53]
+- *(1) HashAggregate(keys=[Name#38, Category#39, spark_grouping_id#37L], functions=[partial_sum(Sales#27)], output=[Name#38, Category#39, spark_grouping_id#37L, sum#67L])
+- *(1) Expand [[Sales#27, Name#25, Category#26, 0]], [Sales#27, Name#38, Category#39, spark_grouping_id#37L]
+- *(1) ColumnarToRow
+- FileScan parquet spark_catalog.default.sales_data_by_name[Name#25,Category#26,Sales#27] Batched: true, Bucketed: true, DataFilters: [], Format: Parquet, Location: InMemoryFileIndex(1 paths)[file:/C:/MASTER-DE/SparkEOI/spark-warehouse/sales_data_by_name], PartitionFilters: [], PushedFilters: [], ReadSchema: struct<Name:string,Category:string,Sales:int>, SelectedBucketsCount: 4 out of 4
```

### Con ROLLUP

```json
== Parsed Logical Plan ==
'Aggregate [rollup(Vector(0), Vector(1), Name#25, Category#26)], [Name#25, Category#26, count(1) AS count#57L]
+- Aggregate [Name#25, Category#26], [Name#25, Category#26, sum(Sales#27) AS sum(Sales)#49L]
+- SubqueryAlias spark_catalog.default.sales_data_by_name
+- Relation spark_catalog.default.sales_data_by_name[Name#25,Category#26,Sales#27,Quantity#28] parquet

== Analyzed Logical Plan ==
Name: string, Category: string, count: bigint
Aggregate [Name#61, Category#62, spark_grouping_id#60L], [Name#61, Category#62, count(1) AS count#57L]
+- Expand [[Name#25, Category#26, sum(Sales)#49L, Name#58, Category#59, 0], [Name#25, Category#26, sum(Sales)#49L, Name#58, null, 1], [Name#25, Category#26, sum(Sales)#49L, null, null, 3]], [Name#25, Category#26, sum(Sales)#49L, Name#61, Category#62, spark_grouping_id#60L]
+- Project [Name#25, Category#26, sum(Sales)#49L, Name#25 AS Name#58, Category#26 AS Category#59]
+- Aggregate [Name#25, Category#26], [Name#25, Category#26, sum(Sales#27) AS sum(Sales)#49L]
+- SubqueryAlias spark_catalog.default.sales_data_by_name
+- Relation spark_catalog.default.sales_data_by_name[Name#25,Category#26,Sales#27,Quantity#28] parquet

== Optimized Logical Plan ==
Aggregate [Name#61, Category#62, spark_grouping_id#60L], [Name#61, Category#62, count(1) AS count#57L]
+- Expand [[Name#25, Category#26, 0], [Name#25, null, 1], [null, null, 3]], [Name#61, Category#62, spark_grouping_id#60L]
+- Aggregate [Name#25, Category#26], [Name#25, Category#26]
+- Project [Name#25, Category#26]
+- Relation spark_catalog.default.sales_data_by_name[Name#25,Category#26,Sales#27,Quantity#28] parquet

== Physical Plan ==
*(2) HashAggregate(keys=[Name#61, Category#62, spark_grouping_id#60L], functions=[count(1)], output=[Name#61, Category#62, count#57L])
+- Exchange hashpartitioning(Name#61, Category#62, spark_grouping_id#60L, 200), ENSURE_REQUIREMENTS, [plan_id=104]
+- *(1) HashAggregate(keys=[Name#61, Category#62, spark_grouping_id#60L], functions=[partial_count(1)], output=[Name#61, Category#62, spark_grouping_id#60L, count#70L])
+- *(1) Expand [[Name#25, Category#26, 0], [Name#25, null, 1], [null, null, 3]], [Name#61, Category#62, spark_grouping_id#60L]
+- *(1) HashAggregate(keys=[Name#25, Category#26], functions=[], output=[Name#25, Category#26])
+- *(1) HashAggregate(keys=[Name#25, Category#26], functions=[], output=[Name#25, Category#26])
+- *(1) ColumnarToRow
+- FileScan parquet spark_catalog.default.sales_data_by_name[Name#25,Category#26] Batched: true, Bucketed: true, DataFilters: [], Format: Parquet, Location: InMemoryFileIndex(1 paths)[file:/C:/MASTER-DE/SparkEOI/spark-warehouse/sales_data_by_name], PartitionFilters: [], PushedFilters: [], ReadSchema: struct<Name:string,Category:string>, SelectedBucketsCount: 4 out of 4
```

Cuando los datos están bucketizados por la columna `Name`, Spark puede optimizar algunas operaciones ya que es
consciente de la localidad de los datos.

En particular, puede reducir el coste de la operación Shuffle durante el proceso de agregación.

Aquí reflejo algunos cambios que se pueden ver en los planes físicos:

#### GROUPING SETS

En el plan físico, una diferencia clave es que ahora Spark está utilizando `FileScan` para leer los datos de Parquet.

Esto es una mejora de rendimiento sobre la tabla regular, lo que ocurre porque los datos están organizados en buckets
por la columna `Name`.

La operación `FileScan` ahora tiene la propiedad `Bucketed: true`, lo que indica que Spark está sacando provecho de la
bucketización.

Finalmente, `SelectedBucketsCount` indica que Spark está leyendo los datos de todos los buckets (4 de 4), esto significa
que está aprovechándose de la bucketización de los datos para mejorar el rendimiento.

#### ROLLUP

Similar al caso de `GROUPING SETS`, el plan físico ahora también utiliza `FileScan` para leer los archivos de Parquet,
con `Bucketed: true`.

La operación `HashAggregate` es la misma en ambos casos, por `Name` y `Category`.

Nuevamente, `SelectedBucketsCount` indica que Spark está leyendo todos los buckets disponibles (4 de 4).

En resumen, la bucketización de los datos permite a Spark optimizar la lectura y el procesamiento de los datos, ya que
el bucketizado permite a Spark utilizar 'FileScan' de los buckets en paralelo, lo cual es más eficiente que leer toda la
tabla. Además, la ejecución de las operaciones de agregación puede ser más eficiente y rápida debido a la disminución de
la necesidad de shuffling de los datos en la fase de agregación.

## Con Bucketing y Particionado

```scala
// Tabla con bucketing y particionada por Category
val TablaPorNombreYCategoria = "sales_data_by_name_and_category"
df.write.partitionBy("Category").bucketBy(BucketSize, BucketColumn).saveAsTable(TablaPorNombreYCategoria)

val dfConBucketingYParticionado = spark.table(TablaPorNombreYCategoria)

val result2 = spark.sql(s"SELECT $BucketColumn, Category, sum(Sales) FROM $TablaPorNombreYCategoria GROUP BY $BucketColumn, Category GROUPING SETS(($BucketColumn, Category))")

// Idem. pero con el método groupingSets
// Usamos rollup para obtener el mismo resultado que con grouping sets
val resultDf2 = dfConBucketingYParticionado.groupBy(BucketColumn, "Category").agg(functions.sum("Sales")).rollup(BucketColumn, "Category").count()

result2.explain(true)
resultDf2.explain(true)

result2.show(truncate = false)
resultDf2.show(truncate = false)
```

Las diferencias que se aprecian son debidas cómo los datos están particionados:

### GROUPING SETS

```json
== Parsed Logical Plan ==
'Aggregate [groupingsets(Vector(0, 1), 'Name, 'Category, 'Name, 'Category)], ['Name, 'Category, unresolvedalias('sum('Sales), None)]
+- 'UnresolvedRelation [sales_data_by_name_and_category], [], false

== Analyzed Logical Plan ==
Name: string, Category: string, sum(Sales): bigint
Aggregate [Name#111, Category#112, spark_grouping_id#110L], [Name#111, Category#112, sum(Sales#99) AS sum(Sales)#107L]
+- Expand [[Name#98, Sales#99, Quantity#100, Category#101, Name#108, Category#109, 0]], [Name#98, Sales#99, Quantity#100, Category#101, Name#111, Category#112, spark_grouping_id#110L]
   +- Project [Name#98, Sales#99, Quantity#100, Category#101, Name#98 AS Name#108, Category#101 AS Category#109]
      +- SubqueryAlias spark_catalog.default.sales_data_by_name_and_category
         +- Relation spark_catalog.default.sales_data_by_name_and_category[Name#98,Sales#99,Quantity#100,Category#101] parquet

== Optimized Logical Plan ==
Aggregate [Name#111, Category#112, spark_grouping_id#110L], [Name#111, Category#112, sum(Sales#99) AS sum(Sales)#107L]
+- Expand [[Sales#99, Name#98, Category#101, 0]], [Sales#99, Name#111, Category#112, spark_grouping_id#110L]
   +- Project [Sales#99, Name#98, Category#101]
      +- Relation spark_catalog.default.sales_data_by_name_and_category[Name#98,Sales#99,Quantity#100,Category#101] parquet

== Physical Plan ==
*(2) HashAggregate(keys=[Name#111, Category#112, spark_grouping_id#110L], functions=[sum(Sales#99)], output=[Name#111, Category#112, sum(Sales)#107L])
+- Exchange hashpartitioning(Name#111, Category#112, spark_grouping_id#110L, 200), ENSURE_REQUIREMENTS, [plan_id=230]
   +- *(1) HashAggregate(keys=[Name#111, Category#112, spark_grouping_id#110L], functions=[partial_sum(Sales#99)], output=[Name#111, Category#112, spark_grouping_id#110L, sum#140L])
      +- *(1) Expand [[Sales#99, Name#98, Category#101, 0]], [Sales#99, Name#111, Category#112, spark_grouping_id#110L]
         +- *(1) ColumnarToRow
            +- FileScan parquet spark_catalog.default.sales_data_by_name_and_category[Name#98,Sales#99,Category#101] Batched: true, Bucketed: true, DataFilters: [], Format: Parquet, Location: CatalogFileIndex(1 paths)[file:/C:/MASTER-DE/SparkEOI/spark-warehouse/sales_data_by_name_and_cate..., PartitionFilters: [], PushedFilters: [], ReadSchema: struct<Name:string,Sales:int>, SelectedBucketsCount: 4 out of 4
```

- En el plan físico, la operación de FileScan está leyendo desde una ubicación que está particionada por `Category`. Se
  menciona `Bucketed: true`, lo que indica que los datos están agrupados en buckets por `Category`.
- En la etapa de Exchange, los datos se distribuyen basándose en `Name` y `Category`, lo que potencialmente reduce el
  número de datos que tienen que ser cambiados de lugar, gracias a la partición por `Category`.

### ROLLUP

```json
== Parsed Logical Plan ==
'Aggregate [rollup(Vector(0), Vector(1), Name#98, Category#101)], [Name#98, Category#101, count(1) AS count#130L]
+- Aggregate [Name#98, Category#101], [Name#98, Category#101, sum(Sales#99) AS sum(Sales)#122L]
   +- SubqueryAlias spark_catalog.default.sales_data_by_name_and_category
      +- Relation spark_catalog.default.sales_data_by_name_and_category[Name#98,Sales#99,Quantity#100,Category#101] parquet

== Analyzed Logical Plan ==
Name: string, Category: string, count: bigint
Aggregate [Name#134, Category#135, spark_grouping_id#133L], [Name#134, Category#135, count(1) AS count#130L]
+- Expand [[Name#98, Category#101, sum(Sales)#122L, Name#131, Category#132, 0], [Name#98, Category#101, sum(Sales)#122L, Name#131, null, 1], [Name#98, Category#101, sum(Sales)#122L, null, null, 3]], [Name#98, Category#101, sum(Sales)#122L, Name#134, Category#135, spark_grouping_id#133L]
   +- Project [Name#98, Category#101, sum(Sales)#122L, Name#98 AS Name#131, Category#101 AS Category#132]
      +- Aggregate [Name#98, Category#101], [Name#98, Category#101, sum(Sales#99) AS sum(Sales)#122L]
         +- SubqueryAlias spark_catalog.default.sales_data_by_name_and_category
            +- Relation spark_catalog.default.sales_data_by_name_and_category[Name#98,Sales#99,Quantity#100,Category#101] parquet

== Optimized Logical Plan ==
Aggregate [Name#134, Category#135, spark_grouping_id#133L], [Name#134, Category#135, count(1) AS count#130L]
+- Expand [[Name#98, Category#101, 0], [Name#98, null, 1], [null, null, 3]], [Name#134, Category#135, spark_grouping_id#133L]
   +- Aggregate [Name#98, Category#101], [Name#98, Category#101]
      +- Project [Name#98, Category#101]
         +- Relation spark_catalog.default.sales_data_by_name_and_category[Name#98,Sales#99,Quantity#100,Category#101] parquet

== Physical Plan ==
*(2) HashAggregate(keys=[Name#134, Category#135, spark_grouping_id#133L], functions=[count(1)], output=[Name#134, Category#135, count#130L])
+- Exchange hashpartitioning(Name#134, Category#135, spark_grouping_id#133L, 200), ENSURE_REQUIREMENTS, [plan_id=281]
   +- *(1) HashAggregate(keys=[Name#134, Category#135, spark_grouping_id#133L], functions=[partial_count(1)], output=[Name#134, Category#135, spark_grouping_id#133L, count#143L])
      +- *(1) Expand [[Name#98, Category#101, 0], [Name#98, null, 1], [null, null, 3]], [Name#134, Category#135, spark_grouping_id#133L]
         +- *(1) HashAggregate(keys=[Name#98, Category#101], functions=[], output=[Name#98, Category#101])
            +- *(1) HashAggregate(keys=[Name#98, Category#101], functions=[], output=[Name#98, Category#101])
               +- *(1) ColumnarToRow
                  +- FileScan parquet spark_catalog.default.sales_data_by_name_and_category[Name#98,Category#101] Batched: true, Bucketed: true, DataFilters: [], Format: Parquet, Location: CatalogFileIndex(1 paths)[file:/C:/MASTER-DE/SparkEOI/spark-warehouse/sales_data_by_name_and_cate..., PartitionFilters: [], PushedFilters: [], ReadSchema: struct<Name:string>, SelectedBucketsCount: 4 out of 4
```

- A nivel de operaciones es igual al caso no particionado. Sin embargo, al igual que en el caso de `GROUPING SETS`, la
  operación de FileScan indica `Bucketed: true`, lo que significa que los datos están organizados en buckets
  por `Category`.
- También en el caso de `ROLLUP`, la etapa de `Exchange` se beneficia de que los datos estén particionados
  por `Category`.

En resumen, se debería ver una mejora en el rendimiento gracias a la disminución en la cantidad de datos siendo movidos
en la operación Exchange, gracias a la partición por `Category`.

Para elegir si se prefiere `GROUPING SETS` o `ROLLUP`, puede depender del tamaño y la distribución de los datos.
Generalmente, `GROUPING SETS` puede ser más eficiente ya que requiere menos transformaciones de los datos.
