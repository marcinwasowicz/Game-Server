package Components

import akka.actor.{Actor, ActorRef}

import scala.collection.mutable

class RoomManager() extends Actor {
  val roomRefs = mutable.HashMap.empty[Int, ActorRef]

  def processCreateRequest(name: String, sender: ActorRef): Unit = ()

  def processJoinRequest(name: String, roomId: Int, sender: ActorRef): Unit = ()

  override def receive: Receive = {
    case _ => ()
  }
}
