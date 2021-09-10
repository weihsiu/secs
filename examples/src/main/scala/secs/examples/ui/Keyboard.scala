package secs.examples.ui

import org.scalajs.dom

trait Keyboard:
  def keyDown(keyCode: Int): Boolean

object Keyboard:
  val htmlKeyboard = new Keyboard:
    private var downKeys = Set.empty[Int]
    dom.document.onkeydown = e => downKeys += e.keyCode
    dom.document.onkeyup = e => downKeys -= e.keyCode

    def keyDown(keyCode: Int): Boolean = downKeys(keyCode)
