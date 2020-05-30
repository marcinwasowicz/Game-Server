package components.game

import akka.actor.Actor

trait Game extends Actor{
  val gameName: String
}
