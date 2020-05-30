package Components

import akka.actor.{Actor, ActorRef, ActorSelection}

class GameManager(name: String, roomManager: ActorSelection) extends Actor {
  var currentRoomId: Option[Int] = None
  var currentRoomActor: Option[ActorRef] = None

  override def receive: Receive = {
    case msg: RoomCreationRequest =>
      roomManager ! msg
    case msg: RoomJoinRequest =>
      roomManager ! msg
    case RoomJoinProblem(_, err) =>
      println(s"Joining failed: $err")
    case RoomCreationProblem(_, err) =>
      println(s"Creating room failed: $err")
    case RoomJoined(roomId, roomActor) =>
      println(s"Room $roomId joined")
      currentRoomId = Some(roomId)
      currentRoomActor = Some(roomActor)
    case RoomCreated(roomId, roomActor) =>
      println(s"Room $roomId created")
      currentRoomId = Some(roomId)
      currentRoomActor = Some(roomActor)
    case msg: Signal =>
      currentRoomActor.foreach(_ ! msg)
  }
}
