package edu.luc.etl.cs313.scala.uidemo
package controller

import android.graphics.Color
import android.os.AsyncTask
import android.util.Log
import model.Dots
import scala.util.Random

// TODO figure out how to replace this with a future

/** Generate new dots, one per 5 seconds. */
class DotGenerator(dots: Dots, controller: Controller, level: Int)
  extends AsyncTask[AnyRef, AnyRef, AnyRef] {

  override protected def onProgressUpdate(values: AnyRef*) = {
    for (x <- 1 to MonstersQuantity(level))
      if (Random.nextBoolean())
        controller.makeDot(dots, Color.YELLOW) // this method runs on the UI thread!
      else
        controller.makeDot(dots, Color.GREEN) // this method runs on the UI thread!
  }

  override protected def doInBackground(params: AnyRef*): AnyRef = {
    Log.d(TAG, "monster generator scheduling monster creation")
    publishProgress(null)
    null
  }
}

class MonsterChanger(dots: Dots, controller: Controller, level: Int)
  extends AsyncTask[AnyRef, AnyRef, AnyRef] {

  override protected def onProgressUpdate(values: AnyRef*) = {
    controller.changeDot(dots) // this method runs on the UI thread!
  }

  override protected def doInBackground(params: AnyRef*): AnyRef = {
    while (! isCancelled) {
      Log.d(TAG, "monster changer scheduling monster change of state")
      try { Thread.sleep(ChangeInterval(level)) } catch { case _: InterruptedException => return null }
      publishProgress(null)
    }
    null
  }
}

class MonsterMover(dots: Dots, controller: Controller, level: Int)
  extends AsyncTask[AnyRef, AnyRef, AnyRef] {

  override protected def onProgressUpdate(values: AnyRef*) = {
    controller.moveDot(dots) // this method runs on the UI thread!
  }

  override protected def doInBackground(params: AnyRef*): AnyRef = {
    while (! isCancelled) {
      Log.d(TAG, "monster mover scheduling monster movement")
      try { Thread.sleep(MoveInterval(level)) } catch { case _: InterruptedException => return null }
      publishProgress(null)
    }
    null
  }
}
