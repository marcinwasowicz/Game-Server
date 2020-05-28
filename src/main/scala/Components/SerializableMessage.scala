package Components

sealed trait SerializableMessage

final case class Signal(info: String) extends SerializableMessage

sealed trait ConnectionMessage extends SerializableMessage

final case class LoginSuccess(name: String) extends ConnectionMessage
final case class LoginFailure(message: String) extends ConnectionMessage
final case class Login(name: String) extends ConnectionMessage
final case class Logout(name: String) extends ConnectionMessage

sealed trait ListingMessage extends SerializableMessage

final case class ListingRequest(name: String) extends ListingMessage
final case class Listing(logins: List[String]) extends ListingMessage

final case class Start(u: Unit) extends SerializableMessage
case object Start{
  def apply(): Start = Start(())
}