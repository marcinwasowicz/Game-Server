package components.game.ships

import akka.actor.ActorRef

import scala.collection.mutable

class GameShipsServer(playersInRoom: mutable.HashMap[String, ActorRef]) extends GameShips {
  override def receive: Receive = ???
}
