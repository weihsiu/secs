# SECS: Scala Entity Component System

## Prelude
While learning Rust and looking for a fun library to get my hands dirty, [Bevy](https://github.com/bevyengine/bevy) caught my eyes and using ECS (Entity Component System) to manage the artifacts in the game world and interactions within it was pretty refreshing.  So I thought wouldn't it be nice to be able to do the same thing in Scala, especially with the new meta programming facilities made available in Scala 3, maybe I can implement some of the same features without resorting to macros.  Hence this experiment.

## TLDR: What I have accomplished
I tried to model the API after Bevy, though some of the type signatures have changed because we are not restricted (shackled?) by the Rust lifetime checker.  But all in all, I believe I have accomplished what I had set out to do and designed an API that's pretty pleasant to use.  If you want to learn more, read on.

## Entity
I used Opaque Type Alias to model entity which is simply a Int identifier.  Nothing fancy.  Since the identifier is never manipulated directly, there is no need to get the value out.

```scala
opaque type Entity = Int

object Entity:
  def apply(entity: Int): Entity = entity
```

## Component
Components in SECS are simply normal case classes that extends Component (marker trait) and derives ComponentMeta.  You can put whatever you want in a component and should be immutable.  ComponentMeta is a simple way to generate a singleton given for each component type and it uses the derives mechanism in Scala 3 to accomplish the task with minimum boilerplate.

```scala
case class Dimension(width: Double, height: Double) extends Component derives ComponentMeta
```

## System
System in SECS are just normal Scala functions.  They are required to be inlined since we are doing some type level trickery to get the necessary information at compile time.  They use using clause to summon both Command and Query (more on them later) to provide the abilities to view and manipulate components.  I didn't follow the design decision in Bevy to structure systems more rigidly (having API to register them, for instance), instead you are just writing normal functions and use normal scala syntax to compose them.  I think this is more intuitive and flexible.

```scala
inline def updateDimensions(using
      command: Command,
      query: Query1[(EntityC, Dimension)]
  ): Unit = ???
```

## Command
Command/EntityCommand consist of a simple set of APIs to spawn/despawn (Bevy speak) entities, and manipulate components.

