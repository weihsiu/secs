package secs.examples.ui

trait Renderer:
  def width: Double
  def height: Double
  def strokeRect(color: String, x: Double, y: Double, width: Double, height: Double): Unit
  def fillRect(color: String, x: Double, y: Double, width: Double, height: Double): Unit
  def strokePolygon(
      scale: Double,
      angle: Double,
      color: String,
      x: Double,
      y: Double,
      segments: List[(Double, Double, Double, Double)]
  ): Unit
  def animateFrame(frame: Double => Unit): Unit
