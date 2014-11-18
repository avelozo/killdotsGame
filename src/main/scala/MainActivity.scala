package edu.luc.etl.cs313.scala.hello.uidemo

import android.app.Activity
import android.graphics.Color
import android.os.{AsyncTask, Bundle}
import android.util.Log
import android.view.ContextMenu.ContextMenuInfo
import android.view.{ContextMenu, KeyEvent, Menu, MenuItem, MotionEvent, View}
import android.view.View.OnKeyListener

import scala.collection.mutable.ArrayBuffer

import view.DotView
import model._

import scala.util.Random

// TODO separate life cycle and UI event handling

/** Android UI demo program */
class MainActivity extends Activity with TypedActivity {

  val TAG = "edu.luc.etl.cs313.scala.hello.uidemo"

  /** Dot diameter */
  val DOT_DIAMETER = 6

  /** The application model */
  final val dotModel = new Dots

  /** The application view */
  def dotView = findView(TR.dots)

  /** Delay between generation of dots. */
  val DELAY = 5000

  /** The dot generator */
  var dotGenerator: DotGenerator = _

  override def onCreate(state: Bundle) = {
    super.onCreate(state)
    setContentView(R.layout.main)
    wireComponents()
  }

  override def onStart() = {
    super.onStart()
    dotGenerator = new DotGenerator(dotModel, dotView, Color.BLACK)
    dotGenerator.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null)
  }

  override def onStop() = {
    dotGenerator.cancel(true)
    dotGenerator = null
    super.onStop()
  }

  override def onCreateOptionsMenu(menu: Menu) = {
    getMenuInflater.inflate(R.menu.simple_menu, menu)
    true
  }

  override def onOptionsItemSelected(item: MenuItem) = item.getItemId match {
    case R.id.menu_clear => dotModel.clearDots() ; true
    case _ => super.onOptionsItemSelected(item)
  }

  override def onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) =
    menu.add(Menu.NONE, 1, Menu.NONE, "Clear").setAlphabeticShortcut('x') // TODO externalize strings

  override def onContextItemSelected(item: MenuItem) = item.getItemId match {
    case 1 => dotModel.clearDots() ; true
    case _ => false
  }

  protected def wireComponents(): Unit = {
    // connect the dots view to the model
    dotView.setDots(dotModel)

    dotView.setOnCreateContextMenuListener(this)
    dotView.setOnTouchListener(new TrackingTouchListener(dotModel))

    dotView.setOnKeyListener(new OnKeyListener {
      override def onKey(v: View, keyCode: Int, event: KeyEvent): Boolean = {
        if (KeyEvent.ACTION_DOWN == event.getAction)
          keyCode match {
            case KeyEvent.KEYCODE_SPACE => makeDot(dotModel, dotView, Color.MAGENTA); true
            case KeyEvent.KEYCODE_ENTER => makeDot(dotModel, dotView, Color.BLUE); true
            case _ => false
          }
        else
          false
      }
    })

    // wire up the controller
    findView(TR.button1).setOnClickListener(new View.OnClickListener {
      override def onClick(v: View) = makeDot(dotModel, dotView, Color.RED)
    })
    findView(TR.button2).setOnClickListener(new View.OnClickListener {
      override def onClick(v: View) = makeDot(dotModel, dotView, Color.GREEN)
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

  /** Listen for taps. */
  class TrackingTouchListener(dots: Dots) extends View.OnTouchListener {

    val tracks = new ArrayBuffer[Int]

    override def onTouch(v: View, evt: MotionEvent): Boolean = {
      val action = evt.getAction
      action & MotionEvent.ACTION_MASK match {
        case MotionEvent.ACTION_DOWN | MotionEvent.ACTION_POINTER_DOWN =>
          val idx = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
            MotionEvent.ACTION_POINTER_INDEX_SHIFT
          tracks += evt.getPointerId(idx)
        case MotionEvent.ACTION_POINTER_UP =>
          val idx = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
            MotionEvent.ACTION_POINTER_INDEX_SHIFT;
          tracks -= evt.getPointerId(idx)
        case MotionEvent.ACTION_MOVE =>
          for (i <- tracks) {
            val idx = evt.findPointerIndex(i)
            for (j <- 0 until evt.getHistorySize) {
              addDot(
                dots,
                evt.getHistoricalX(idx, j),
                evt.getHistoricalY(idx, j),
                evt.getHistoricalPressure(idx, j),
                evt.getHistoricalSize(idx, j)
              )
            }
          }
        case _ => return false
      }

      for (i <- tracks) {
        val idx = evt.findPointerIndex(i)
        addDot(dots,
          evt.getX(idx),
          evt.getY(idx),
          evt.getPressure(idx),
          evt.getSize(idx)
        )
      }

      true
    }

    private def addDot(dots: Dots, x: Float, y: Float, p: Float, s: Float) =
      dots.addDot(x, y, Color.CYAN, ((p + 0.5) * (s + 0.5) * DOT_DIAMETER).toInt)
  }

  // TODO figure out how to replace this with a future

  /** Generate new dots, one per second. */
  class DotGenerator(dots: Dots, view: DotView, color: Int)
    extends AsyncTask[AnyRef, AnyRef, AnyRef] {

    override protected def onProgressUpdate(values: AnyRef*) =
      makeDot(dots, view, color) // this method runs on the UI thread!

    override protected def doInBackground(params: AnyRef*): AnyRef = {
      while (! isCancelled) {
        Log.d(TAG, "dot generator scheduling dot creation of color " + color)
        publishProgress(null)
        try { Thread.sleep(DELAY) } catch { case _: InterruptedException => return null }
      }
      null
    }
  }

  /**
   * @param dots the dots we're drawing
   * @param view the view in which we're drawing dots
   * @param color the color of the dot
   */
  protected def makeDot(dots: Dots, view: DotView, color: Int): Unit = {
    val pad = (DOT_DIAMETER + 2) * 2
    dots.addDot(
      DOT_DIAMETER + (Random.nextFloat() * (view.getWidth - pad)),
      DOT_DIAMETER + (Random.nextFloat() * (view.getHeight - pad)),
      color,
      DOT_DIAMETER)
  }
}