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
    findView(TR.text1).setText("W" + dotView.getWidth)
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
        findView(TR.text1).setText(if (null == d) "" else "WID" + dotView.getWidth)
        findView(TR.text2).setText(if (null == d) "" else "xlast" + dotView.getMeasuredWidth) //d.y.toString)
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
    while(h < 768){
      while(w < 768) {
        var square: Square = new Square(w, h, false)
        list += square
        w += side
      }
      w = 0
      h += side
    }
    list
  }

  /**
   * @param dots the dots we're drawing
   * @param color the color of the dot
   */
  def makeDot(dots: Dots, color: Int): Unit = {
    var found = false
    var squarePos = new Square(0, 0, false)
    listSquares.foreach(sq => found = found || !sq.full)

    if (found) {
      do{
         squarePos = listSquares(Random.nextInt(listSquares.length))
      }while(squarePos.full)
      dots.addDot(squarePos,color,DOT_DIAMETER)
    }
  }

  def changeDot(dots: Dots): Unit ={

    dots.getDots().foreach(dot =>

      if(dot.color== Color.GREEN){
        dot.color= Color.YELLOW

      }else{
        dot.color= Color.GREEN
      })
  }

  def moveDot(dots: Dots): Unit ={

    val possibleMoves = new ListBuffer[Square]()
    var newSquare = new Square(0, 0, false)

    dots.getDots().foreach(dot => {

      possibleMoves.clear()

      listSquares.foreach(square => {
        if (!square.full &&
          (Math.abs(square.x - dot.pos.x) <= dotModel.side) &&
          (Math.abs(square.y - dot.pos.y) <= dotModel.side)) {
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