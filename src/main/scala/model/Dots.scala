package edu.luc.etl.cs313.scala.uidemo.model

import android.graphics.Color

import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
 * A monster: the square, color and size.
 * @param pos square position.
 * @param color the color.
 * @param diameter dot diameter.
 */
case class Dot(var pos: Square, var color: Int,  diameter: Int)
case class Square(var x: Float, var y: Float, var full: Boolean)


object Dots {
  trait DotsChangeListener {
    /** @param dots the monsters that changed. */
    def onDotsChange(dots: Dots): Unit
  }
}

/** A list of monsters. */
class Dots {

  var side : Float = 0
  private val dots = new ListBuffer[Dot]

  private var dotsChangeListener: Dots.DotsChangeListener = _

  /** @param l set the change listener. */
  def setDotsChangeListener(l: Dots.DotsChangeListener) = dotsChangeListener = l

  /** @return the most recently added monster. */
  def getLastDot(): Dot = if (dots.size <= 0) null else dots.last // TODO convert to option

  /** @return immutable list of monsters. */
  def getDots(): List[Dot] = dots.toList

  /**
   * @param pos square position.
   * @param color dot color.
   * @param diameter dot size.
   */
  def addDot(pos: Square, color: Int, diameter: Int): Unit = {
    dots += Dot(pos, color, diameter)
    notifyListener()
  }

  def killDot(x:Float, y: Float, color: Int, diameter: Int): Unit ={
    getDots().foreach(dot => findDot(dot,x, y, color, diameter) )
    notifyListener()
  }

  private def findDot(dot:Dot, xpress:Float, ypress: Float, colorPress: Int, diameterPress: Int): Unit ={
    if((dot.pos.x + side > xpress) &&
      (dot.pos.x < xpress) &&
      (dot.pos.y + side > ypress) &&
      (dot.pos.y < ypress)){
      dot.pos.full = false
      dots -= Dot(dot.pos, colorPress, diameterPress)
    }
  }

  def changeDot(): Unit = {
    getDots().foreach(dot =>
      if(dot.color == Color.GREEN){
        dot.color = Color.YELLOW
      }else{
        dot.color = Color.GREEN
      }
    )
    notifyListener()
  }

  def moveDot(listSquares: ListBuffer[Square]): Unit = {
    val possibleMoves = new ListBuffer[Square]()
    var newSquare: Square = null

    getDots().foreach(dot => {
      possibleMoves.clear()

      listSquares.foreach(square => {
        if (!square.full &&
          (Math.abs(square.x - dot.pos.x) <= side) &&
          (Math.abs(square.y - dot.pos.y) <= side)) {
          possibleMoves += square
        }
      })

      if (possibleMoves.length > 0) {
        newSquare = possibleMoves(Random.nextInt(possibleMoves.length))

        newSquare.full = true
        dot.pos.full = false
        dot.pos = newSquare
      }
    })

    notifyListener()
  }

  /** Remove all dots. */
  def clearDots(): Unit = {
    dots.clear()
    notifyListener()
  }

  private def notifyListener(): Unit =
    if (null != dotsChangeListener)
      dotsChangeListener.onDotsChange(this)

}
