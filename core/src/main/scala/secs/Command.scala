package secs

trait Command:
  def spawn(): EntityCommand
  def entity(entity: Entity): EntityCommand

trait EntityCommand:
  def entity(): Entity
  def insert(component: Component): EntityCommand
  def remove(component: Component): EntityCommand

object Command:
  given (using world: World): Command with
    def spawn() = ???
    def entity(entity: Entity) = ???
