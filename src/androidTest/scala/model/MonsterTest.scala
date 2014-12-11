package model

import org.scalatest.junit.JUnitSuite
import android.graphics.Color
import edu.luc.etl.cs313.scala.uidemo._
import edu.luc.etl.cs313.scala.uidemo.model.{Dots, Square}
import org.junit.Assert._
import org.junit.Test

import scala.collection.mutable.ListBuffer

/**
 * This is a simple unit test of an object without dependencies.
 * This follows the XUnit Testcase Superclass pattern.
 */
class MonsterTest extends JUnitSuite {

  def fixture() = new Dots

  /** Verifies that the list of monsters is initially empty. */
  @Test def testPreconditions(): Unit = {
    val model = fixture()
    assertEquals(0, model.getDots().length)
  }

  /** Verifies the monster is created. */
  @Test def testCreation(): Unit = {
    val model = fixture()
    model.addDot(Square(20, 30, 50, true), Color.YELLOW, DOT_DIAMETER)
    assertEquals(1, model.getDots().length)
  }

  /** Verifies the monster is killed. */
  @Test def testKill(): Unit = {
    val model = fixture()
    model.addDot(Square(20, 30, 50, true), Color.YELLOW, DOT_DIAMETER)
    model.killDot(35, 40, Color.YELLOW, DOT_DIAMETER)
    assertEquals(0, model.getDots().length)
  }

  /** Verifies the monster is in protected state. */
  @Test def testChange(): Unit = {
    val model = fixture()
    model.addDot(Square(20, 30, 50, true), Color.YELLOW, DOT_DIAMETER)
    model.changeDot()
    model.killDot(35, 40, Color.YELLOW, DOT_DIAMETER)
    assertEquals(1, model.getDots().length)
  }

  /** Verifies the monster is moving. */
  @Test def testMove(): Unit = {
    val model = fixture()
    val squares : ListBuffer[Square] = new ListBuffer[Square]
    squares += Square(20, 30, 50, true)
    squares += Square(70, 30, 50, true)
    model.addDot(Square(20, 30, 50, true), Color.YELLOW, DOT_DIAMETER)
    model.moveDot(squares.toList)
    model.killDot(35, 40, Color.YELLOW, DOT_DIAMETER)
    assertEquals(1, model.getDots().length)
  }

}
