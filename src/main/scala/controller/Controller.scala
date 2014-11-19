package edu.luc.etl.cs313.scala.uidemo
package controller

import android.graphics.Color
import android.view.View.OnKeyListener
import android.view.{KeyEvent, View}

import model.Dots

/** Controller for Android UI demo program */
trait Controller {

  def activity: MainActivity

  val dotView = activity.dotView
  val dotModel = activity.dotModel
  def findView[A](tr: TypedResource[A]): A = activity.findView(tr)

  dotView.setOnCreateContextMenuListener(activity)
  dotView.setOnTouchListener(new TrackingTouchListener(dotModel))

  dotView.setOnKeyListener(new OnKeyListener {
    override def onKey(v: View, keyCode: Int, event: KeyEvent): Boolean = {
      if (KeyEvent.ACTION_DOWN == event.getAction)
        keyCode match {
          case KeyEvent.KEYCODE_SPACE => dotView.makeDot(dotModel, dotView, Color.MAGENTA); true
          case KeyEvent.KEYCODE_ENTER => dotView.makeDot(dotModel, dotView, Color.BLUE); true
          case _ => false
        }
      else
        false
    }
  })

  // wire up the rest of the controller
  findView(TR.button1).setOnClickListener(new View.OnClickListener {
    override def onClick(v: View) = dotView.makeDot(dotModel, dotView, Color.RED)
  })
  findView(TR.button2).setOnClickListener(new View.OnClickListener {
    override def onClick(v: View) = dotView.makeDot(dotModel, dotView, Color.GREEN)
  })

  dotModel.setDotsChangeListener(new Dots.DotsChangeListener {
    def onDotsChange(dots: Dots) = {
      val d = dots.getLastDot
      findView(TR.text1).setText(if (null == d) "" else d.x.toString)
      findView(TR.text2).setText(if (null == d) "" else d.y.toString)
      dotView.invalidate()
    }
  })
}