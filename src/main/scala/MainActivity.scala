package edu.luc.etl.cs313.scala.uidemo

import android.app.Activity
import android.graphics.Color
import android.os.{AsyncTask, Bundle}
import android.view.ContextMenu.ContextMenuInfo
import android.view.{ContextMenu, Menu, MenuItem, View}

import model._
import controller._

object MainActivity {
  val TAG = "edu.luc.etl.cs313.scala.uidemo"
}

/** Main activity for Android UI demo program. Responsible for Android lifecycle. */
class MainActivity extends Activity with TypedActivity {

  /** The application model */
  final val dotModel = new Dots

  /** The application view */
  def dotView = findView(TR.dots)

  /** The dot generator */
  var dotGenerator: _root_.controller.DotGenerator = _

  override def onCreate(state: Bundle) = {
    super.onCreate(state)
    setContentView(R.layout.main)
    dotView.setDots(dotModel)
    new Controller { lazy val activity = MainActivity.this }
  }

  override def onStart() = {
    super.onStart()
    dotGenerator = new _root_.controller.DotGenerator(dotModel, dotView, Color.BLACK)
    dotGenerator.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null)
  }

  override def onStop() = {
    dotGenerator.cancel(true)
    dotGenerator = null
    super.onStop()
  }

  // TODO should these methods be in the controller?

  override def onCreateOptionsMenu(menu: Menu) = {
    getMenuInflater.inflate(R.menu.simple_menu, menu)
    true
  }

  override def onOptionsItemSelected(item: MenuItem) = item.getItemId match {
    case R.id.menu_clear => dotModel.clearDots(); true
    case _ => super.onOptionsItemSelected(item)
  }

  override def onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) =
    menu.add(Menu.NONE, 1, Menu.NONE, "Clear").setAlphabeticShortcut('x') // TODO externalize strings

  override def onContextItemSelected(item: MenuItem) = item.getItemId match {
    case 1 => dotModel.clearDots(); true
    case _ => false
  }
}