package Components

import akka.actor.{Actor, ActorSelection}

class ConnectionManager(clientManager: ActorSelection) extends Actor{

    override def receive: Receive = {
        case TaggedMessage("login",clientName) =>
            clientManager ! TaggedMessage(clientName, "login")
        case TaggedMessage("logout", clientName) =>
            clientManager ! TaggedMessage(clientName, "logout")
        case Signal("Log In Failure") =>
            context.parent ! Signal("Log In Failure")
        case TaggedMessage("Log In Successful", name) =>
            context.parent ! TaggedMessage("Log In Successful", name)
    }
}
