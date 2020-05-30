package Components

import akka.actor.{Actor, ActorRef, ActorSelection}

class GameManager(name: String, roomManager: ActorSelection) extends Actor {
  override def receive: Receive = {
    case msg: RoomCreationRequest =>
      roomManager ! msg
    case msg: RoomJoinRequest =>
      roomManager ! msg
    case RoomJoinProblem(_, err) =>
      println(s"Joining failed: $err")
    case RoomCreationProblem(_, err) =>
      println(s"Creating room failed: $err")
    case RoomJoined(roomId) =>
      println(s"Room $roomId joined")
    case RoomCreated(roomId) =>
      println(s"Room $roomId created")
  }
}
