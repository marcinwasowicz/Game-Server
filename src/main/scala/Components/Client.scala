package Components

import akka.actor.{Actor, ActorSelection, PoisonPill, Props}

class Client(clientManager: ActorSelection) extends Actor{

    val simpleListener = context.actorOf(Props(new SimpleListener()), "stdin-listener")
    val connectionManager = context.actorOf(Props(new ConnectionManager(clientManager)), "connection-manager")
    var name: Option[String] = None

    def doAtLogin[A](loggedAction: => A)(notLoggedAction: => A): A ={
        name.fold{
            notLoggedAction
        }{
            _ => loggedAction
        }
    }

    def doIfLogged(f: => Unit): Unit = doAtLogin(f)(())
    def doIfNotLogged(f: => Unit): Unit = doAtLogin(())(f)

    def init(): Unit = simpleListener ! Start()

    def shutDown(): Unit = {
        println("Client Correctly Shutting Down")
        self ! PoisonPill
    }

    override def receive: Receive = {
        case Start(_) =>
            init()
        case Signal("close") =>
            doIfLogged{
                clientManager ! Logout(name.get)
            }
            shutDown()
        case Signal("list") =>
            doAtLogin{
                clientManager ! ListingRequest(name.get)
            }{
                println("You have to be logged in")
            }
        case Signal("logout") =>
            doAtLogin{
                connectionManager ! Logout(name.get)
            }{
                println("You are not logged in")
            }
            name = None
        case Login(login) =>
            doAtLogin{
                println("You are already logged in")
            }{
                connectionManager ! Login(login)
            }
        case LoginFailure(_) =>
            println("Log in failed")
        case LoginSuccess(acceptedName) =>
            name = Some(acceptedName)
            println("Successfully logged to server")
    }
}
