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

/**
 * A square: the position, size and occupation.
 * @param x horizontal position.
 * @param y vertical position.
 * @param side size of the side.
 * @param full occupation status.
 */
case class Square(var x: Float, var y: Float, var side: Float, var full: Boolean)

/** A list of squares. */
class Squares {

  /** Internal list. */
  private val squares = new ListBuffer[Square]

  /** @return immutable list of squares. */
  def getSquares(): List[Square] = squares.toList

  /**
   * @param x horizontal position.
   * @param y vertical position.
   * @param side size of the side.
   * @param full occupation status.
   */
  def addSquare(x: Float, y: Float, side: Float, full: Boolean): Unit = {
    squares += Square(x, y, side, full)
  }

  /**
   * @param width horizontal screen size.
   * @param height vertical screen size.
   */
  def populate(width: Int, height: Int): Unit = {

    val qntSquare : Int = 768 / 57
    val side = 768 / qntSquare

    for (h <- 0 until qntSquare) {
      for (w <- 0 until qntSquare) {
        addSquare(h * side, w * side, side, false)
      }
    }

  }

}

object Dots {
  trait DotsChangeListener {
    /** @param dots the monsters that changed. */
    def onDotsChange(dots: Dots): Unit
  }
  trait LevelChangeListener {
    /** @param level the new level. */
    def onLevelChange(level: Int): Unit
  }
  trait ScoreChangeListener {
    /** @param score the new score. */
    def onScoreChange(score: Int): Unit
  }
}

/** A list of monsters. */
class Dots {

  private val dots = new ListBuffer[Dot]
  private var level = 0
  private var score = 0
  private var dotsChangeListener: Dots.DotsChangeListener = _
  private var levelChangeListener: Dots.LevelChangeListener = _
  private var scoreChangeListener: Dots.ScoreChangeListener = _

  /** @param l set the monsters change listener. */
  def setDotsChangeListener(l: Dots.DotsChangeListener) = dotsChangeListener = l

  /** @param l set the level change listener. */
  def setLevelChangeListener(l: Dots.LevelChangeListener) = levelChangeListener = l

  /** @param l set the score change listener. */
  def setScoreChangeListener(l: Dots.ScoreChangeListener) = scoreChangeListener = l

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
    notifyMonsterListener()
  }

  def killDot(x:Float, y: Float, color: Int, diameter: Int): Unit ={
    getDots().foreach(dot => findDot(dot,x, y, color, diameter) )
    notifyMonsterListener()
  }

  private def findDot(dot:Dot, xpress:Float, ypress: Float, colorPress: Int, diameterPress: Int): Unit ={
    if((dot.pos.x + dot.pos.side > xpress) &&
      (dot.pos.x < xpress) &&
      (dot.pos.y + dot.pos.side > ypress) &&
      (dot.pos.y < ypress)){
      val count = dots.length
      dots -= Dot(dot.pos, colorPress, diameterPress)
      if (dots.length < count) {
        dot.pos.full = false
        score += 1
        notifyScoreListener()
      }
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
    notifyMonsterListener()
  }

  def moveDot(listSquares: List[Square]): Unit = {
    val possibleMoves = new ListBuffer[Square]()
    var newSquare: Square = null

    getDots().foreach(dot => {
      possibleMoves.clear()

      listSquares.foreach(square => {
        if (!square.full &&
          (Math.abs(square.x - dot.pos.x) <= dot.pos.side) &&
          (Math.abs(square.y - dot.pos.y) <= dot.pos.side)) {
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

    notifyMonsterListener()
  }

  /** Remove all dots. */
  def clearDots(): Unit = {
    dots.clear()
    notifyMonsterListener()
  }

  private def notifyMonsterListener(): Unit =
    if (null != dotsChangeListener)
      dotsChangeListener.onDotsChange(this)

  private def notifyLevelListener(): Unit =
    if (null != levelChangeListener)
      levelChangeListener.onLevelChange(level)

  private def notifyScoreListener(): Unit =
    if (null != scoreChangeListener)
      scoreChangeListener.onScoreChange(score)

}
