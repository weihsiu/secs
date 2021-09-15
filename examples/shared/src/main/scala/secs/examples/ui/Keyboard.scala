package secs.examples.ui

enum KeyCode(val value: Int):
  case Space extends KeyCode(32)
  case Left extends KeyCode(37)
  case Up extends KeyCode(38)
  case Right extends KeyCode(39)

trait Keyboard:
  def keyDown(keyCode: KeyCode): Boolean
