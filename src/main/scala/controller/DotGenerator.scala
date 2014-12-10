package edu.luc.etl.cs313.scala.uidemo
package controller

import android.os.AsyncTask
import android.util.Log

import model.Dots

// TODO figure out how to replace this with a future

/** Generate new dots, one per 5 seconds. */
class DotGenerator(dots: Dots, controller: Controller, color: Int)
  extends AsyncTask[AnyRef, AnyRef, AnyRef] {

  /** Delay between generation of dots. */
  val DELAY = 5000 // TODO externalize

  override protected def onProgressUpdate(values: AnyRef*) = {
    //while (dots.getDots().length < 4)
      controller.makeDot(dots, color) // this method runs on the UI thread!
  }

  def changeDots(): Unit ={
    controller.changeDot(dots)
  }

  override protected def doInBackground(params: AnyRef*): AnyRef = {
    while (! isCancelled) {
      Log.d(TAG, "dot generator scheduling dot creation of color " + color)
      publishProgress(null)
      try { Thread.sleep(DELAY) } catch { case _: InterruptedException => return null }
      //changeDots
    }
    null
  }
}

class MonsterChanger(dots: Dots, controller: Controller, color: Int)
  extends AsyncTask[AnyRef, AnyRef, AnyRef] {

  /** Delay between change of monsters. */
  val DELAY = 3000

  override protected def onProgressUpdate(values: AnyRef*) = {
    controller.changeDot(dots) // this method runs on the UI thread!
  }

  override protected def doInBackground(params: AnyRef*): AnyRef = {
    while (! isCancelled) {
      Log.d(TAG, "monster changer scheduling monster change of color " + color)
      try { Thread.sleep(DELAY) } catch { case _: InterruptedException => return null }
      publishProgress(null)
    }
    null
  }
}

class MonsterMover(dots: Dots, controller: Controller, color: Int)
  extends AsyncTask[AnyRef, AnyRef, AnyRef] {

  /** Delay between generation of dots. */
  val DELAY = 1000 // TODO externalize

  override protected def onProgressUpdate(values: AnyRef*) = {
    controller.moveDot(dots) // this method runs on the UI thread!
  }

  override protected def doInBackground(params: AnyRef*): AnyRef = {
    while (! isCancelled) {
      Log.d(TAG, "dot generator scheduling dot creation of color " + color)
      try { Thread.sleep(DELAY) } catch { case _: InterruptedException => return null }
      publishProgress(null)
    }
    null
  }
}
