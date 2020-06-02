package components.client

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.{ConfigFactory, ConfigValueFactory}
import components.common.Start
import components.server.ServerApp.serverConfiguration

import scala.io.StdIn

object ClientApp extends App {
    var clientConfiguration = ConfigFactory.load.getConfig("ClientConfig")
    val servAddress = StdIn.readLine("Server IP Address: ")
    val clientSystem = ActorSystem("ClientSystem", clientConfiguration)
    val clientManager = clientSystem.actorSelection(s"akka://ServerSystem@$servAddress/user/server/client-manager")
    val roomManager = clientSystem.actorSelection(s"akka://ServerSystem@$servAddress/user/server/room-manager")
    val client = clientSystem.actorOf(Props(new Client(clientManager, roomManager)), "client")
    client ! Start()
}
