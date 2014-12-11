package model

import edu.luc.etl.cs313.scala.uidemo.model.Squares
import org.junit.Assert._
import org.junit.Test
import org.scalatest.junit.JUnitSuite

/**
 * This is a simple unit test of an object without dependencies.
 * This follows the XUnit Testcase Superclass pattern.
 */
class SquareTest extends JUnitSuite {

  def fixture() = new Squares

  /** Verifies that the list of squares is initially empty. */
  @Test def testPreconditions(): Unit = {
    val model = fixture()
    assertEquals(0, model.getSquares().length)
  }

  /** Verifies the square is created. */
  @Test def testCreation(): Unit = {
    val model = fixture()
    model.addSquare(20, 30, 50, false)
    assertEquals(1, model.getSquares().length)
  }

  /** Verifies the list is populated with the entire screen. */
  @Test def testPopulation(): Unit = {
    val model = fixture()
    model.populate(768, 768)
    assertEquals(169, model.getSquares().length)
  }

}
