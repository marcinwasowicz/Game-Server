package components.server

import akka.actor.{Actor, ActorRef, Props}
import components.common._
import components.game.Game
import components.game.chat.GameChatServer
import components.game.ships.GameShipsServer

import scala.collection.mutable

class Room(roomId: Int, creatorName: String, creatorActor: ActorRef) extends Actor {
  val playersInRoom: mutable.HashMap[String, ActorRef] = mutable.HashMap(creatorName -> creatorActor)
  val maxPlayers = 2
  var currentGame: Option[ActorRef] = None

  def createGame(gameName: String): Unit = {
    gameName match{
      case "chat" =>
        currentGame = Some(context.actorOf(Props(new GameChatServer(playersInRoom)), s"game-chat-$roomId"))
        playersInRoom
          .values
          .foreach(_ ! RoomGameCreated("chat", currentGame.get))
      case "ships" =>
        currentGame = Some(context.actorOf(Props(new GameShipsServer(playersInRoom)), s"game-ship-$roomId"))
        playersInRoom
          .values
          .foreach(_ ! RoomGameCreated("ship", currentGame.get))
      case _ => ()
    }
  }

  override def receive: Receive = {
    case RoomJoinRequest(name, _) =>
      if(playersInRoom.size < maxPlayers)
        if (playersInRoom.contains(name)) {
          sender ! RoomJoinProblem(roomId, "Player is currently in this room")
        } else {
          playersInRoom += name -> sender
          sender ! RoomJoined(roomId, context.self)
        }
      else
        sender ! RoomJoinProblem(roomId, "Room full")
    case RoomLeft(name) =>
      playersInRoom.remove(name)
    case RoomListingRequest(_) =>
      sender ! RoomListing(roomId, playersInRoom.keys.toList)
    case RoomGameCreationRequest(gameName) =>
      createGame(gameName)
  }
}
