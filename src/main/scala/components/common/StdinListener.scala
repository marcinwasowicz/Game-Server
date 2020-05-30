package components.common

import akka.actor.Actor

trait StdinListener extends Actor{

    def getAndParseInput(): Any

    def listenerLoop(): Unit = {
        val message = getAndParseInput()
        this.context.parent ! message
        listenerLoop()
    }

    override def receive: Receive = {
        case Start(_) =>
            listenerLoop()
    }

}
