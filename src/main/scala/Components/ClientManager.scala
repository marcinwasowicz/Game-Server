package Components

import akka.actor.{Actor, ActorRef}

import scala.collection.mutable

class ClientManager extends Actor{

    val clientDictionary = new mutable.HashMap[String, ActorRef]()

    def listClients(): Unit = clientDictionary.foreach{case (name, _) => println(name)}

    def logClientIn(name: String, client: ActorRef): Unit = {
        clientDictionary.contains(name) match {
            case true =>
                client ! Signal("Log In Failure")
            case _ =>
                clientDictionary.put(name, client)
                client ! TaggedMessage("Log In Successful", name)
        }
    }

    def logClientOut(name: String): Unit = clientDictionary.remove(name)

    override def receive: Receive = {
        case TaggedMessage(name, "logout") =>
            logClientOut(name)
        case TaggedMessage(name, "login") =>
            logClientIn(name, sender())
        case Signal("list") =>
            listClients()
    }

}
