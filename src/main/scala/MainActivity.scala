package edu.luc.etl.cs313.scala.uidemo

import android.app.Activity
import android.os.{AsyncTask, Bundle}
import android.view.View
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

    findView(TR.button1).setOnClickListener(new View.OnClickListener {
      override def onClick(v: View) = onClickStart()
    })
  }

  override def onResume() = {
    super.onResume()
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

  def onClickStart(): Unit = {
    findView(TR.button1).setEnabled(false)

    dotGenerator = new DotGenerator(dotModel, this, 0)
    dotGenerator.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null)
    monsterChanger = new MonsterChanger(dotModel, this, 0)
    monsterChanger.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null)
    monsterMover = new MonsterMover(dotModel, this, 0)
    monsterMover.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null)

    squareModel.populate(dotView.getHeight, dotView.getWidth)
  }

}