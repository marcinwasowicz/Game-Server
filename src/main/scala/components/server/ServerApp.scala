package components.server

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import components.common._

object ServerApp extends App {
    val serverConfiguration = ConfigFactory.load.getConfig("ServerConfig")
    val serverSystem = ActorSystem("ServerSystem", serverConfiguration)
    val server = serverSystem.actorOf(Props(new Server()), "server")
    server ! Start()
}
