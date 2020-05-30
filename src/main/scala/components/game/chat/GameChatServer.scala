package components.game.chat

import akka.actor.ActorRef

import scala.collection.mutable

class GameChatServer(players: mutable.HashMap[String, ActorRef]) extends GameChat{
  override def receive: Receive = {
    case NewMessage(from, message) =>
      players
        .filter(_._1 != from)
        .foreach{
          _._2 ! NewMessage(from, message)
        }
  }
}
