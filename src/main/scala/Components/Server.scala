package Components

import akka.actor.{Actor, PoisonPill, Props}

class Server extends Actor{
    val simpleListener = context.actorOf(Props(new SimpleListener()), "stdin-listener")

    val clientManager = context.actorOf(Props(new ClientManager()), "client-manager")

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
