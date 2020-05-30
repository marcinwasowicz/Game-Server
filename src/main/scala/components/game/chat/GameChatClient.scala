package components.game.chat

import akka.actor.ActorRef
import components.common.Signal
import components.game.Game

class GameChatClient(name: String, serverGameActor: ActorRef) extends GameChat{
  override def receive: Receive = {
    case Signal(s"send $message") =>
      serverGameActor ! NewMessage(name, message)
    case NewMessage(from, message) =>
      println(s"$from: $message")
    case _ => ()
  }
}
