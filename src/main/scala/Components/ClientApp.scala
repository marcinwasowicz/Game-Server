package Components

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

object ClientApp extends App {
    val clientConfiguration = ConfigFactory.load.getConfig("ClientConfig")
    val serverPort = ConfigFactory.load.getConfig("ServerConfig").getInt("akka.remote.artery.canonical.port")
    val clientSystem = ActorSystem("ClientSystem", clientConfiguration)
    val clientManager = clientSystem.actorSelection(s"akka://ServerSystem@127.0.0.1:$serverPort/user/server/client-manager")
    val roomManager = clientSystem.actorSelection(s"akka://ServerSystem@127.0.0.1:$serverPort/user/server/room-manager")
    val client = clientSystem.actorOf(Props(new Client(clientManager, roomManager)), "client")
    client ! Start()
}
