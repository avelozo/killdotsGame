package edu.luc.etl.cs313.scala.uidemo

import android.app.Activity
import android.graphics.Color
import android.os.{AsyncTask, Bundle}
import edu.luc.etl.cs313.scala.uidemo.controller._
import edu.luc.etl.cs313.scala.uidemo.model._

/** Main activity for Android UI demo program. Responsible for Android lifecycle. */
class MainActivity extends Activity with TypedActivity with Controller {

  /** The application model */
  override val dotModel = new Dots
  override val squareModel = new Squares

  /** The monster threads (generator, changer and mover) */
  var dotGenerator: DotGenerator = _
  var monsterChanger: MonsterChanger = _
  var monsterMover: MonsterMover = _

  override def onCreate(state: Bundle) = {
    super.onCreate(state)
    setContentView(R.layout.main)
    connectDotsView()
  }

  override def onStart() = {
    super.onStart()
    dotGenerator = new DotGenerator(dotModel, this, Color.YELLOW)
    dotGenerator.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null)
    monsterChanger = new MonsterChanger(dotModel, this, Color.YELLOW)
    monsterChanger.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null)
    monsterMover = new MonsterMover(dotModel, this, Color.YELLOW)
    monsterMover.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null)
  }

  override def onResume() = {
    super.onResume()
    dotModel.setSquareModel(squareModel)
    squareModel.populate(768, 768)
  }

  override def onStop() = {
    dotGenerator.cancel(true)
    dotGenerator = null
    monsterChanger.cancel(true)
    monsterChanger = null
    monsterMover.cancel(true)
    monsterMover = null
    super.onStop()
  }
}