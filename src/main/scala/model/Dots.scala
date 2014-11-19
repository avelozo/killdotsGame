package edu.luc.etl.cs313.scala.uidemo.model

import scala.collection.mutable.ListBuffer

/**
 * A dot: the coordinates, color and size.
 * @param x horizontal coordinate.
 * @param y vertical coordinate.
 * @param color the color.
 * @param diameter dot diameter.
 */
case class Dot(x: Float, y: Float, color: Int, diameter: Int)

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

  /** Remove all dots. */
  def clearDots(): Unit = {
    dots.clear()
    notifyListener()
  }

  private def notifyListener(): Unit =
    if (null != dotsChangeListener)
      dotsChangeListener.onDotsChange(this)

}
