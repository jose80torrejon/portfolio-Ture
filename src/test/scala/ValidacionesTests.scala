package eoi.de.examples

import org.scalatest.funsuite.AnyFunSuite
import Validaciones._

class ValidacionesTests extends AnyFunSuite {

  test("contieneArroba should return true if the email contains @") {
    assert(contieneArroba("example@domain.com"))
    assert(!contieneArroba("exampledomain.com"))
  }

  test("terminaCom should return true if the email ends with .com") {
    assert(terminaCom("example@domain.com"))
    assert(!terminaCom("example@domain.net"))
  }

  test("contienePunto should return true if the email contains .") {
    assert(contienePunto("example@domain.com"))
    assert(!contienePunto("example@domaincom"))
  }

  test("validarEmail should return true if the email passes all provided tests") {
    assert(!validarEmail("example@do main.com")(contieneArroba, terminaCom, contienePunto, contieneEspacios))
    assert(!validarEmail("exampledomaincom")(contieneArroba, terminaCom, contienePunto, contieneEspacios))
  }

  test("validarEmailConRegex valid email test") {
    assert(validarEmailConRegex("test@example.com"))
    assert(!validarEmailConRegex("test@examp le.com"))
  }

  test("validarEmailConRegex invalid email test") {
    assert(!validarEmailConRegex("test.example.com"))
  }
}