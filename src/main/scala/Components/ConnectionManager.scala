package Components

import akka.actor.{Actor, ActorSelection}

class ConnectionManager(clientManager: ActorSelection) extends Actor{

    override def receive: Receive = {
        case Login(name) =>
            clientManager ! Login(name)
        case Logout(name) =>
            clientManager ! Logout(name)
        case LoginSuccess(name) =>
            context.parent ! LoginSuccess(name)
        case LoginFailure(name) =>
            context.parent ! LoginFailure(name)
        case Listing(logins) =>
            logins.foreach(println)
    }
}
