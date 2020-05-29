package Components

import akka.actor.{Actor, ActorRef}

class GameManager(name: String, roomManager: ActorRef) extends Actor {
  def createRoomRequest(): Unit = {
    ()
  }

  def joinRoomRequest(roomId: Int): Unit = {
    ()
  }

  override def receive: Receive = {
    case _ => ()
  }
}
