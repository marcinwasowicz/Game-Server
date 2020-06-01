package components.game.ships

import akka.actor.ActorRef

import scala.collection.mutable

class GameShipsServer(playersInRoom: mutable.HashMap[String, ActorRef]) extends GameShips {

  val firstPlayerBoard = new mutable.HashMap[(Int, Int), Ship]()
  val secondPlayerBoard = new mutable.HashMap[(Int, Int), Ship]()


  override def receive: Receive = {
    case
  }
}
