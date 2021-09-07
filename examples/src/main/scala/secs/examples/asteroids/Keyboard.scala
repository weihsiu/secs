package secs.examples.asteroids

import org.scalajs.dom

object Keyboard:
  var downKeys = Set.empty[Int]
  dom.document.onkeydown = e => downKeys += e.keyCode
  dom.document.onkeyup = e => downKeys -= e.keyCode

  def keyDown(keyCode: Int): Boolean = downKeys(keyCode)
