package edu.luc.etl.cs313.scala.uidemo.model

import edu.luc.etl.cs313.scala.uidemo

import scala.collection.mutable.ListBuffer

/**
 * A dot: the coordinates, color and size.
 * @param x horizontal coordinate.
 * @param y vertical coordinate.
 * @param color the color.
 * @param diameter dot diameter.
 */
case class Dot(var x: Float, var y: Float, var color: Int,  diameter: Int)
case class Square(var x: Float, var y: Float)
object Dots {
  trait DotsChangeListener {
    /** @param dots the dots that changed. */
    def onDotsChange(dots: Dots): Unit
  }
}

/** A list of dots. */
class Dots {

  private val dots = new ListBuffer[Dot]

  private var dotsChangeListener: Dots.DotsChangeListener = _

  /** @param l set the change listener. */
  def setDotsChangeListener(l: Dots.DotsChangeListener) = dotsChangeListener = l

  /** @return the most recently added dot. */
  def getLastDot(): Dot = if (dots.size <= 0) null else dots.last // TODO convert to option

  /** @return immutable list of dots. */
  def getDots(): List[Dot] = dots.toList

  /**
   * @param x dot horizontal coordinate.
   * @param y dot vertical coordinate.
   * @param color dot color.
   * @param diameter dot size.
   */
  def addDot(x: Float, y: Float, color: Int, diameter: Int): Unit = {
    dots += Dot(x, y, color, diameter)
    notifyListener()
  }

  def killDot(x:Float, y: Float, color: Int, diameter: Int): Unit ={
    getDots().foreach(dot => findDot(dot,x, y, color, diameter) )

    notifyListener()

  }

  private def findDot(dot:Dot, xpress:Float, ypress: Float, colorPress: Int, diameterPress: Int): Unit ={
   if((dot.x+uidemo.DOT_DIAMETER*10>xpress) && (dot.x-uidemo.DOT_DIAMETER*10<xpress)&& (dot.y+uidemo.DOT_DIAMETER*10>ypress) && (dot.y-uidemo.DOT_DIAMETER*10<ypress)){
     dots -= Dot(dot.x, dot.y, colorPress, diameterPress)
   }
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
