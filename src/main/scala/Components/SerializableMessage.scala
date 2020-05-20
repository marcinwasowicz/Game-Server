package Components

sealed trait SerializableMessage

final case class Signal(info: String) extends SerializableMessage
final case class TaggedMessage(tag: String, message: String) extends SerializableMessage
final case class AuthoredMessage(author: String, tag: String, message: String) extends SerializableMessage


