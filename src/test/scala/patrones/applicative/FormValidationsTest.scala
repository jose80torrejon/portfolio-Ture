package eoi.de.examples
package patrones.applicative

import org.scalatest.funsuite.AnyFunSuite

class FormValidationsTest extends AnyFunSuite {

  test("validateForm returns Left when name is empty") {
    val formData = Map("name" -> "", "email" -> "valid@email.com")
    val result = FormValidations.validateForm(formData)
    assert(result == Left(List("name cannot be blank")))
  }

  test("validateForm returns Left when email does not contain @") {
    val formData = Map("name" -> "name", "email" -> "invalid")
    val result = FormValidations.validateForm(formData)
    assert(result == Left(List("Email must contain @")))
  }

  test("validateForm returns Right when name and email are valid") {
    val formData = Map("name" -> "name", "email" -> "valid@email.com")
    val result = FormValidations.validateForm(formData)
    assert(result == Right(formData))
  }
}