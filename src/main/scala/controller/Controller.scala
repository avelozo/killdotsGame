package edu.luc.etl.cs313.scala.uidemo
package controller

import android.app.Activity
import android.view.{Menu, MenuItem, View}
import edu.luc.etl.cs313.scala.uidemo.model.{Dots, Square, Squares}
import edu.luc.etl.cs313.scala.uidemo.view.DotView

import scala.collection.mutable.ListBuffer
import scala.util.Random

/** Controller mixin (stackable trait) for Android UI demo program */
trait Controller extends Activity with TypedActivityHolder {

  val dotModel: Dots
  val squareModel: Squares

  var dotView: DotView = _

  // TODO consider using State pattern

  var isDotsView = true

  def toggleView(): Unit = {
    if (isDotsView) {
      isDotsView = false
      setContentView(R.layout.list)
      connectListView()
    } else {
      isDotsView = true
      setContentView(R.layout.main)
      connectDotsView()
    }
  }



  override def onCreateOptionsMenu(menu: Menu) = {
    getMenuInflater.inflate(R.menu.simple_menu, menu)
    true
  }

  override def onOptionsItemSelected(item: MenuItem) = item.getItemId match {
    //case R.id.menu_clear => dotModel.clearDots() ; true
    //case R.id.menu_toggleView => toggleView() ; true
    case _ => super.onOptionsItemSelected(item)
  }

  def connectDotsView(): Unit = {
    dotView = findView(TR.dots)

    dotView.setDots(dotModel)
    dotView.setOnTouchListener(new TrackingTouchListener(dotModel))

    // This listener provides a tiny bit of mediation from model to view.
    // Conceptually, it represents the dashed arrow (events) from model to view.
    dotModel.setDotsChangeListener(new Dots.DotsChangeListener {
      def onDotsChange(dots: Dots) = {
        dotView.invalidate()
      }
    })

    dotModel.setScoreChangeListener(new Dots.ScoreChangeListener {
      def onScoreChange(score: Int) = {
        findView(TR.text2).setText("Score: " + score.toString)
      }
    })
  }

  /**
   * @param dots the dots we're drawing
   * @param color the color of the dot
   */
  def makeDot(dots: Dots, color: Int): Unit = {
    var squarePos : Square = null
    val availableSquares = new ListBuffer[Square]()
    val squareList = squareModel.getSquares()

    squareList.foreach(square => {
      if (!square.full) {
        availableSquares += square
      }
    })

    if (availableSquares.length > 0) {
      squarePos = availableSquares(Random.nextInt(availableSquares.length))
      dots.addDot(squarePos,color,DOT_DIAMETER)
    }
  }

  def changeDot(dots: Dots): Unit ={
    dots.changeDot()
  }

  def moveDot(dots: Dots): Unit ={
    dots.moveDot(squareModel.getSquares())
  }

  def connectListView(): Unit = {
    val scroll = findView(TR.scroll)
    scroll.post(new Runnable() { override def run() = scroll.fullScroll(View.FOCUS_DOWN) })
    val listView = findView(TR.list)
    for (d <- dotModel.getDots)
      listView.append(d.toString + String.format("%n"))
    // This listener provides a tiny bit of mediation from model to view.
    // Conceptually, it represents the dashed arrow (events) from model to view.
    dotModel.setDotsChangeListener(new Dots.DotsChangeListener {
      override def onDotsChange(dots: Dots) = {
        val d = dots.getLastDot
        if (d == null)
          listView.setText("")
        else
          listView.append(d.toString + String.format("%n"))
      }
    })
  }
}