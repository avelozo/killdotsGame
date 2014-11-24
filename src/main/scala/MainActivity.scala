package edu.luc.etl.cs313.scala.uidemo

import android.app.Activity
import android.graphics.Color
import android.os.{AsyncTask, Bundle}
import android.view.{Menu, MenuItem}

import model._
import controller._

object MainActivity {
  val TAG = "edu.luc.etl.cs313.scala.uidemo"
}

/** Main activity for Android UI demo program. Responsible for Android lifecycle. */
class MainActivity extends Activity with TypedActivity with Controller {

  /** The application model */
  override val dotModel = new Dots

  /** The dot generator */
  var dotGenerator: DotGenerator = _

  override def onCreate(state: Bundle) = {
    super.onCreate(state)
    setContentView(R.layout.main)
    connectDotsView()
  }

  override def onStart() = {
    super.onStart()
    dotGenerator = new DotGenerator(dotModel, this, Color.BLACK)
    dotGenerator.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null)
  }

  override def onStop() = {
    dotGenerator.cancel(true)
    dotGenerator = null
    super.onStop()
  }
}