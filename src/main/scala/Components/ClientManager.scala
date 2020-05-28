package Components

import akka.actor.{Actor, ActorRef}

import scala.collection.mutable

class ClientManager extends Actor{
    val clientDictionary = new mutable.HashMap[String, ActorRef]()

    @Deprecated def listClients(): Unit = clientDictionary.foreach{
        case (name, _) => println(name)
    }

    def logClientIn(name: String, client: ActorRef): Unit = {
        clientDictionary.contains(name) match {
            case true =>
                client ! LoginFailure(name)
            case _ =>
                clientDictionary.put(name, client)
                client ! LoginSuccess(name)
        }
    }

    def logClientOut(name: String): Unit = clientDictionary.remove(name)

    override def receive: Receive = {
        case Logout(name) =>
            logClientOut(name)
        case Login(name) =>
            logClientIn(name, sender())
        case ListingRequest(name) =>
            clientDictionary
              .get(name)
              .foreach{
                  _ ! Listing(clientDictionary.keys.toList)
              }
    }

}
