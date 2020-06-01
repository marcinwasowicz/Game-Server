package components.game.ships

import akka.actor.ActorRef


import scala.collection.mutable

class GameShipsServer(playersInRoom: mutable.HashMap[String, ActorRef]) extends GameShips {

  val width = 10
  val height= 10

  var clientsBoardDictionary = new mutable.HashMap[String, mutable.HashMap[(Int, Int), Ship]]()
  var gameClientDictionary = new mutable.HashMap[String, ActorRef]()
  var counter = 0

  def initializeBoard(board: mutable.HashMap[(Int, Int), Ship]) = {}

  def initializeAndSendBoards() = gameClientDictionary.foreach{case (name, ref) =>
    initializeBoard(clientsBoardDictionary.apply(name))

  }



  override def receive: Receive = {
    case GameClientCreated(name) =>
      gameClientDictionary.put(name, sender())
      clientsBoardDictionary.put(name, new mutable.HashMap[(Int, Int), Ship]())
      counter += 1
      if(counter >= 2){
        initializeAndSendBoards()
      }
    case
  }
}
