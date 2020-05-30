package components.client

import akka.actor.{Actor, ActorRef, ActorSelection, Props}
import akka.pattern.ask
import akka.util.Timeout
import components.common._
import components.game.chat.GameChatClient

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

class GameManager(name: String, roomManager: ActorSelection) extends Actor {
  var currentRoomId: Option[Int] = None
  var currentRoomActor: Option[ActorRef] = None
  implicit val timeout: Timeout = 5.seconds

  var currentGame: Option[ActorRef] = None

  def listing(): Unit =
    currentRoomActor.fold{
      println("You are not in any room")
    }{
      room => Try{
        Await.result(room ? RoomListingRequest(currentRoomId.get), 5.seconds) match {
          case RoomListing(_, listing) =>
            println("Users in room:")
            listing.foreach{
              login => println(s"--\t$login")
            }
          case _ => ()
        }
      } match {
        case Failure(exception) => println(exception.getMessage)
        case _ => ()
      }
    }

  def createRoom(): Unit = roomManager ! RoomCreationRequest(name)
  def joinRoom(roomId: Int): Unit = roomManager ! RoomJoinRequest(name, roomId)
  def leaveRoom(): Unit = {
    currentRoomActor.foreach{
      room =>
        room ! RoomLeft(name)
        println("Room left")
    }
    currentRoomId = None
    currentRoomActor = None
  }


  override def receive: Receive = {
    case Signal("new") => createRoom()

    case Signal(s"join $id") => id.toIntOption.foreach(joinRoom)

    case Signal("list") => listing()

    case Signal("leave") => leaveRoom()

    case Signal(s"game $command") =>
      command match{
        case "new chat" => currentRoomActor.foreach{_ ! RoomGameCreationRequest("chat")}
        case _ => currentGame.foreach(_ ! Signal(command))
      }

    case RoomJoinProblem(_, err) => println(s"Joining failed: $err")

    case RoomCreationProblem(_, err) => println(s"Creating room failed: $err")

    case RoomJoined(roomId, roomActor) =>
      leaveRoom()
      println(s"Room $roomId joined")
      currentRoomId = Some(roomId)
      currentRoomActor = Some(roomActor)

    case RoomCreated(roomId, roomActor) =>
      leaveRoom()
      println(s"Room $roomId created")
      currentRoomId = Some(roomId)
      currentRoomActor = Some(roomActor)

    case RoomGameCreated(gameName, gameActor) =>
      val actorName =  s"game-$gameName-player"
      gameName match {
        case "chat" =>
          println(s"Game $gameName started")
          currentGame = Some(context.actorOf(Props(new GameChatClient(name, gameActor)), actorName))
      }

    case msg: GameMessage =>
      currentGame.foreach(_.forward(msg))
  }
}
