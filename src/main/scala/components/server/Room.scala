package components.server

import akka.actor.Actor
import components.common._

import scala.collection.mutable

class Room(roomId: Int, creatorName: String) extends Actor {
  val playersInRoom: mutable.Set[String] = mutable.Set(creatorName)
  val maxPlayers = 2

  override def receive: Receive = {
    case RoomJoinRequest(name, _) =>
      if(playersInRoom.size < maxPlayers)
        playersInRoom.contains(name) match {
          case false =>
            playersInRoom += name
            sender ! RoomJoined(roomId, context.self)
          case true =>
            sender ! RoomJoinProblem(roomId, "Player is currently in this room")
        }
      else
        sender ! RoomJoinProblem(roomId, "Room full")
    case RoomLeft(name) =>
      playersInRoom.remove(name)
    case RoomListingRequest(_) =>
      sender ! RoomListing(roomId, playersInRoom.toList)
  }
}
