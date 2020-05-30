package Components

import akka.actor.Actor

class Room(roomId: Int) extends Actor {
  override def receive: Receive = {
    _ => println("GOT")
  }
}
