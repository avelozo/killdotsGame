package edu.luc.etl.cs313.scala.uidemo

import android.app.Activity
import android.graphics.Color
import android.os.{AsyncTask, Bundle}
import android.view.View
import edu.luc.etl.cs313.scala.uidemo.controller._
import edu.luc.etl.cs313.scala.uidemo.model._

import scala.util.Random

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

    dotModel.setLevelChangeListener(new Dots.LevelChangeListener {
      def onLevelChange(level: Int) = {
        findView(TR.text1).setText("Level: " + level.toString)

        monsterChanger.delay = ChangeInterval(level)
        monsterMover.delay = MoveInterval(level)

        for (x <- 1 to MonstersQuantity(level))
          if (Random.nextBoolean())
            makeDot(dotModel, Color.YELLOW) // this method runs on the UI thread!
          else
            makeDot(dotModel, Color.GREEN) // this method runs on the UI thread!
      }
    })

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
    findView(TR.text1).setText("Level: 0")
    findView(TR.text2).setText("Score: 0")

    dotGenerator = new DotGenerator(dotModel, this, 0)
    dotGenerator.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null)
    monsterChanger = new MonsterChanger(dotModel, this)
    monsterChanger.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null)
    monsterMover = new MonsterMover(dotModel, this)
    monsterMover.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null)

    squareModel.populate(dotView.getHeight, dotView.getWidth)
  }

}