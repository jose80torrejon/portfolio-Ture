
package spark.sql
import org.apache.spark.sql.catalyst.expressions.{Expression, Literal, UnaryExpression}
import org.apache.spark.sql.catalyst.expressions.codegen.CodegenFallback
import org.apache.spark.sql.types.{DataType, StringType}

object ExampleFunctions {
  // Una UnaryExpression es una expresión que toma un solo argumento y devuelve un solo valor.
  // En este caso, la expresión toLower toma un argumento de tipo StringType y devuelve un valor de tipo StringType.
  val toLower: UnaryExpression with CodegenFallback = new UnaryExpression
    // CodegenFallback es una clase que proporciona una implementación de backup para la generación de código.
    with CodegenFallback {

    // El método child devuelve el argumento de la expresión.
    override def child: Expression = Literal("HELLO WORLD")

    // El método nullSafeEval evalúa la expresión en el caso de que el argumento no sea nulo.
    override protected def nullSafeEval(input: Any): Any =
      input.toString.toLowerCase

    // El método canEqual devuelve true si el objeto puede ser igualado con otro objeto.
    override def canEqual(that: Any): Boolean = true

    // El método withNewChildInternal devuelve una nueva instancia de la expresión con un nuevo argumento.
    override protected def withNewChildInternal(newChild: Expression): Expression = newChild

    // El método dataType devuelve el tipo de datos de la expresión.
    override def dataType: DataType = StringType

    // El método productArity devuelve el número de elementos en la tupla de la expresión.
    override def productArity: Int = 0
    // El método productElement devuelve el elemento en la posición n de la tupla de la expresión.
    override def productElement(n: Int): Any = throw new IndexOutOfBoundsException

  }

}

import org.apache.spark.sql.functions.{col, udf}

object CEExample02 extends App with SparkSessionWrapper {

  val toLowerCase = udf[String, String](_.toLowerCase())

  spark.sparkContext.setLogLevel("ERROR")
  import spark.implicits._

  val data = Seq("HELLO WORLD").toDF("text")
  data.show(false)

  val lowerCaseDF = data.withColumn("lowerCase", toLowerCase(col("text")))
  lowerCaseDF.show(false)

}
