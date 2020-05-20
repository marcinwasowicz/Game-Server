package Components

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

object ClientApp extends App {
    val clientConfiguration = ConfigFactory.load.getConfig("ClientConfig")
    val clientSystem = ActorSystem("ClientSystem", clientConfiguration)
    val clientManager = clientSystem.actorSelection("akka://ServerSystem@127.0.0.1:4000/user/GameServer/ClientManager")
    val client = clientSystem.actorOf(Props(new Client(clientManager)))
    client ! Signal("start")
}
