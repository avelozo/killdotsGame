package edu.luc.etl.cs313.scala.uidemo
package view

import android.content.Context
import android.graphics.{Canvas, Color, Paint}
import android.graphics.Paint.Style
import android.util.AttributeSet
import android.view.View

import model._

import scala.util.Random

object DotView {
  /** Dot diameter */
  val DOT_DIAMETER = 6 // TODO externalize
}

/**
 * I see spots!
 *
 * @param context
 * @param attrs
 * @param defStyle
 *
 * @author <a href="mailto:android@callmeike.net">Blake Meike</a>
 */
class DotView(context: Context, attrs: AttributeSet, defStyle: Int) extends View(context, attrs, defStyle) {

  import DotView.DOT_DIAMETER

  { setFocusableInTouchMode(true) }

  /** The model underlying this view. */
  private var dots: Dots = _

  /** @param context the rest of the application */
  def this(context: Context) = {
    this(context, null, 0)
    setFocusableInTouchMode(true)
  }

  /**
   * @param context
   * @param attrs
   */
  def this(context: Context, attrs: AttributeSet) = {
    this(context, attrs, 0)
    setFocusableInTouchMode(true)
  }

  /**
   * Injects the model underlying this view.
   *
   * @param dots
   * */
  def setDots(dots: Dots): Unit = this.dots = dots

  /** @see android.view.View#onDraw(android.graphics.Canvas) */
  override protected def onDraw(canvas: Canvas): Unit = {
    val paint = new Paint
    paint.setStyle(Style.STROKE)
    paint.setColor(if (hasFocus) Color.BLUE else Color.GRAY)

    canvas.drawRect(0, 0, getWidth - 1, getHeight - 1, paint)

    if (null == dots) return

    paint.setStyle(Style.FILL)
    for (dot <- dots.getDots) {
      paint.setColor(dot.color)
      canvas.drawCircle(dot.x, dot.y, dot.diameter, paint)
    }
  }

  /**
   * @param dots the dots we're drawing
   * @param view the view in which we're drawing dots
   * @param color the color of the dot
   */
  def makeDot(dots: Dots, view: DotView, color: Int): Unit = {
    val pad = (DOT_DIAMETER + 2) * 2
    dots.addDot(
      DOT_DIAMETER + (Random.nextFloat() * (view.getWidth - pad)),
      DOT_DIAMETER + (Random.nextFloat() * (view.getHeight - pad)),
      color,
      DOT_DIAMETER)
  }
}
