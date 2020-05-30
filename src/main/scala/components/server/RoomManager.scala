package components.server

import akka.actor.{Actor, ActorRef, Props}
import components.common._

import scala.collection.mutable
import scala.util.Random

class RoomManager() extends Actor {
  val roomRefs = mutable.HashMap.empty[Int, ActorRef]

  def newRoomId: Int = {
    Option(Random.nextInt(20))
//    THIS ONE SHOULD BE USED; 20 FOR TESTING
//    Option(Random.nextInt(Int.MaxValue))
      .filter(!roomRefs.contains(_))
      .getOrElse(newRoomId)
  }

  def processCreateRequest(name: String): Unit = {
    val roomId = newRoomId
    val newRoom = context.actorOf(Props(new Room(roomId, name, sender)), s"room-$roomId")
    roomRefs.addOne(roomId -> newRoom)
    sender ! RoomCreated(roomId, newRoom)
  }

  def processJoinRequest(name: String, roomId: Int): Unit = {
    roomRefs.get(roomId) match {
      case Some(roomActor) =>
        roomActor.forward(RoomJoinRequest(name, roomId))
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
