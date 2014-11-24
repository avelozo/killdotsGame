package edu.luc.etl.cs313.scala.uidemo
package controller

import android.os.AsyncTask
import android.util.Log

import model.Dots

// TODO figure out how to replace this with a future

/** Generate new dots, one per second. */
class DotGenerator(dots: Dots, controller: Controller, color: Int)
  extends AsyncTask[AnyRef, AnyRef, AnyRef] {

  /** Delay between generation of dots. */
  val DELAY = 5000 // TODO externalize

  override protected def onProgressUpdate(values: AnyRef*) =
    controller.makeDot(dots, color) // this method runs on the UI thread!

  override protected def doInBackground(params: AnyRef*): AnyRef = {
    while (! isCancelled) {
      Log.d(TAG, "dot generator scheduling dot creation of color " + color)
      publishProgress(null)
      try { Thread.sleep(DELAY) } catch { case _: InterruptedException => return null }
    }
    null
  }
}
