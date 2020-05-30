package Components

import akka.actor.{Actor, ActorContext, ActorRef, Props}

import scala.collection.mutable
import scala.util.Random

class RoomManager() extends Actor {
  val roomRefs = mutable.HashMap.empty[Int, ActorRef]

  def newRoomId: Int = {
    Option(Random.nextInt)
      .filter(!roomRefs.contains(_))
      .getOrElse(newRoomId)
  }

  def processCreateRequest(name: String, sender: ActorRef): Unit = {
    val roomId = newRoomId
  }

  def processJoinRequest(roomJoinRequest: RoomJoinRequest)(implicit context: ActorContext): Unit = {
    println(context.sender.path)
    roomRefs.get(roomJoinRequest.roomId) match {
      case Some(roomActor) =>
//        roomActor.forward(roomJoinRequest)
        ()
      case None =>
        ()
    }
  }

  override def receive: Receive = {
    case RoomCreationRequest(name) =>
      processCreateRequest(name, sender)
    case msg: RoomJoinRequest =>
      processJoinRequest(msg)
  }
}
