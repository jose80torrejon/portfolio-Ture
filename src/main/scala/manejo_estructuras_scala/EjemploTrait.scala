
package manejo_estructuras_scala

object EjemploTrait extends App {

  trait Desplazable {
    def mover(x: Int, y: Int): Unit
  }
  class Punto(var x: Int, var y: Int) extends Desplazable {
    def mover(nuevaX: Int, nuevaY: Int): Unit = {
      this.x = nuevaX
      this.y = nuevaY
    }
    override def toString: String = s"($x, $y)"
  }
  val punto = new Punto(0, 0)
  println(punto.toString)
  punto.mover(1, 1)
  println(punto.toString)
}
