package components.server

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import components.common._

class Server extends Actor{
    val simpleListener: ActorRef = context.actorOf(Props(new SimpleListener()), "stdin-listener")

    val clientManager: ActorRef = context.actorOf(Props(new ClientManager()), "client-manager")
    val roomManager: ActorRef = context.actorOf(Props(new RoomManager()), "room-manager")

    def init(): Unit = {
        simpleListener ! Start()
        println("Server Correctly initialized")
    }

    def shutDown(): Unit = {
        println("Server Correctly Shutting Down")
        self ! PoisonPill
    }

    override def receive: Receive ={
        case Start(_) =>
            init()
        case Signal("close") =>
            shutDown()
    }
}
