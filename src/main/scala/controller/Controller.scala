package edu.luc.etl.cs313.scala.uidemo
package controller

import android.app.Activity
import android.graphics.Color
import android.view.View.OnKeyListener
import android.view.{KeyEvent, Menu, MenuItem, View}
import edu.luc.etl.cs313.scala.uidemo.model.{Dots, Square}
import edu.luc.etl.cs313.scala.uidemo.view.DotView

import scala.collection.mutable.ListBuffer
import scala.util.Random

/** Controller mixin (stackable trait) for Android UI demo program */
trait Controller extends Activity with TypedActivityHolder {

  val dotModel: Dots
  var listSquares: ListBuffer[Square] = new ListBuffer[Square]

  private var dotView: DotView = _

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
    case R.id.menu_clear => dotModel.clearDots() ; true
    case R.id.menu_toggleView => toggleView() ; true
    case _ => super.onOptionsItemSelected(item)
  }

  def connectDotsView(): Unit = {
    dotView = findView(TR.dots)

    dotView.setDots(dotModel)
    dotView.setOnTouchListener(new TrackingTouchListener(dotModel))
    dotView.setOnKeyListener(new OnKeyListener {
      override def onKey(v: View, keyCode: Int, event: KeyEvent): Boolean =
      if (KeyEvent.ACTION_DOWN == event.getAction)
        keyCode match {
          case KeyEvent.KEYCODE_SPACE => makeDot(dotModel, Color.MAGENTA); true
          case KeyEvent.KEYCODE_ENTER => makeDot(dotModel, Color.BLUE); true
          case _ => false
        }
      else
        false
    })

    findView(TR.button1).setOnClickListener(new View.OnClickListener {
      override def onClick(v: View) = makeDot(dotModel, Color.RED)
    })
    findView(TR.button2).setOnClickListener(new View.OnClickListener {
      override def onClick(v: View) = makeDot(dotModel, Color.GREEN)
    })

    // This listener provides a tiny bit of mediation from model to view.
    // Conceptually, it represents the dashed arrow (events) from model to view.
    dotModel.setDotsChangeListener(new Dots.DotsChangeListener {
      def onDotsChange(dots: Dots) = {
        val d = dots.getLastDot
        findView(TR.text1).setText(if (null == d) "" else "")
        findView(TR.text2).setText(if (null == d) "" else "") //d.y.toString)
        dotView.invalidate()
      }
    })
  }

  def calcSquares(): ListBuffer[Square] = {
    val qntSquare : Int = 768 / 57
    val side: Float = 768 / qntSquare
    dotModel.side = side
    var h: Float = 0
    var w : Float = 0
    var list = new ListBuffer[Square]

    for (h <- 0 until qntSquare) {
      for (w <- 0 until qntSquare) {
        list += new Square(h * side, w * side, false)
      }
    }
    list
  }

  /**
   * @param dots the dots we're drawing
   * @param color the color of the dot
   */
  def makeDot(dots: Dots, color: Int): Unit = {
    var found = false
    var squarePos : Square = null
    listSquares.foreach(sq => found = found || !sq.full)

    if (found) {
      do{
         squarePos = listSquares(Random.nextInt(listSquares.length))
      }while(squarePos.full)
      dots.addDot(squarePos,color,DOT_DIAMETER)
    }
  }

  def changeDot(dots: Dots): Unit ={
    dots.changeDot()
  }

  def moveDot(dots: Dots): Unit ={
    dots.moveDot(listSquares)
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