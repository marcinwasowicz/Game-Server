package components.server

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.{ConfigFactory, ConfigValue, ConfigValueFactory}
import components.common._

import scala.io.StdIn

object ServerApp extends App {
    var serverConfiguration = ConfigFactory.load.getConfig("ServerConfig")
    val address = StdIn.readLine("IP address: ").split(":")
    serverConfiguration = serverConfiguration.withValue("akka.remote.artery.canonical.hostname", ConfigValueFactory.fromAnyRef(address(0)))
    serverConfiguration = serverConfiguration.withValue("akka.remote.artery.canonical.port", ConfigValueFactory.fromAnyRef(address(1)))
    val serverSystem = ActorSystem("ServerSystem", serverConfiguration)
    val server = serverSystem.actorOf(Props(new Server()), "server")
    server ! Start()
}
