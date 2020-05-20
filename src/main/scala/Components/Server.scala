package Components

import akka.actor.{Actor, PoisonPill, Props}

class Server extends Actor{
    val simpleListener = context.actorOf(Props(new SimpleListener()))

    val clientManager = context.actorOf(Props(new ClientManager()), "ClientManager")

    def init(): Unit = {
        simpleListener ! Signal("start")
        println("Server Correctly initialized")
    }

    def shutDown(): Unit = {
        println("Server Correctly Shutting Down")
        self ! PoisonPill
    }

    override def receive: Receive ={
        case Signal("Start") =>
            init()
        case Signal("close") =>
            shutDown()
    }
}
