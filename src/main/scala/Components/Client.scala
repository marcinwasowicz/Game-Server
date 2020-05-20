package Components

import akka.actor.{Actor, ActorSelection, PoisonPill, Props}

class Client(clientManager: ActorSelection) extends Actor{

    val simpleListener = context.actorOf(Props(new SimpleListener()))
    val connectionManager = context.actorOf(Props(new ConnectionManager(clientManager)))
    var name: String = null

    def init(): Unit = {
        simpleListener ! Signal("start")
    }

    def shutDown(): Unit = {
        println("Client Correctly Shutting Down")
        self ! PoisonPill
    }

    override def receive: Receive = {
        case Signal("start") =>
            init()
        case Signal("close") =>
            name match {
                case null =>
                    shutDown()
                case _ =>
                    clientManager ! TaggedMessage(name, "logout")
                    shutDown()
            }
        case Signal("list") =>
            clientManager ! Signal("list")
        case Signal("logout") =>
            name = null
            connectionManager ! TaggedMessage("logout", name)
        case TaggedMessage("login", proposedName)=>
            connectionManager ! TaggedMessage("login", proposedName)
        case Signal("Log In Failure") =>
            println("log in failed")
        case TaggedMessage("Log In Successful", acceptedName) =>
            name = acceptedName
            println("successfully logged to server")
    }
}
