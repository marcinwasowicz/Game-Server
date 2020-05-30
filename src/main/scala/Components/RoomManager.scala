package Components

import akka.actor.{Actor, ActorContext, ActorRef, Props}

import scala.collection.mutable
import scala.util.Random

class RoomManager() extends Actor {
  val roomRefs = mutable.HashMap.empty[Int, ActorRef]

  def newRoomId: Int = {
    Option(Random.nextInt(Int.MaxValue))
      .filter(!roomRefs.contains(_))
      .getOrElse(newRoomId)
  }

  def processCreateRequest(name: String): Unit = {
    val roomId = newRoomId
    val newRoom = context.actorOf(Props(new Room(roomId)), s"room-$roomId")
    roomRefs.addOne(roomId -> newRoom)
    sender ! RoomCreated(roomId, newRoom)
  }

  def processJoinRequest(name: String, roomId: Int): Unit = {
    roomRefs.get(roomId) match {
      case Some(roomActor) =>
//        roomActor.forward(RoomJoinRequest(name, roomId))
        sender ! RoomJoined(roomId)
      case None =>
        sender ! RoomJoinProblem(roomId, "Room does not exist")
    }
  }

  override def receive: Receive = {
    case RoomCreationRequest(name) =>
      processCreateRequest(name)
    case RoomJoinRequest(name, roomId) =>
      processJoinRequest(name, roomId)
  }
}
