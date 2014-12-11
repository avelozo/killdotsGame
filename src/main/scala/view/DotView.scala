package edu.luc.etl.cs313.scala.uidemo
package view

import android.content.Context
import android.graphics.Paint.Style
import android.graphics.{Canvas, Color, Paint}
import android.util.AttributeSet
import android.view.View
import edu.luc.etl.cs313.scala.uidemo.model._

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

  { setFocusableInTouchMode(true)
   }

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

    paint.setStyle(Style.FILL_AND_STROKE)
    paint.setColor(Color.rgb(80, 74, 37))
    canvas.drawRect(0, 0, getWidth, getHeight, paint)

    if (null == dots) return

    paint.setStyle(Style.FILL)
    for (dot <- dots.getDots) {
      paint.setColor(dot.color)
      canvas.drawCircle(dot.pos.x + dots.squareModel.side/2,
                        dot.pos.y + dots.squareModel.side/2,
                        dot.diameter/2,
                        paint)
    }
  }
}
