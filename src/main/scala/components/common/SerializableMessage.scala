package components.common

import akka.actor.ActorRef

sealed trait SerializableMessage

final case class Signal(info: String) extends SerializableMessage
final case class Start(u: Unit) extends SerializableMessage
case object Start{
  def apply(): Start = Start(())
}

sealed trait ConnectionMessage extends SerializableMessage

final case class LoginSuccess(name: String) extends ConnectionMessage
final case class LoginFailure(message: String) extends ConnectionMessage
final case class Login(name: String) extends ConnectionMessage
final case class Logout(name: String) extends ConnectionMessage

sealed trait ListingMessage extends SerializableMessage

final case class ListingRequest(name: String) extends ListingMessage
final case class Listing(logins: List[String]) extends ListingMessage

sealed trait RoomMessage extends SerializableMessage

final case class RoomCreationRequest(name: String) extends RoomMessage
final case class RoomJoinRequest(name: String, roomId: Int) extends RoomMessage
final case class RoomCreated(roomId: Int, roomActor: ActorRef) extends RoomMessage
final case class RoomJoined(roomId: Int, roomActor: ActorRef) extends RoomMessage
final case class RoomJoinProblem(roomId: Int, msg: String) extends RoomMessage
final case class RoomCreationProblem(roomId: Int, msg: String) extends RoomMessage