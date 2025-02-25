

import org.openjdk.jmh.annotations._

import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class ValidacionesBenchmark {

  val email: String = "test@example.com"

  @Benchmark
  def benchmarkEmailValidation(): Boolean =
    Validaciones.validarEmailConRegex(email)

  @Benchmark
  def benchmarkEmailValidation2(): Boolean = Validaciones.validarEmail(email)(
    Validaciones.contieneArroba,
    Validaciones.contienePunto,
    Validaciones.terminaCom,
    Validaciones.contieneEspacios
  )

}
