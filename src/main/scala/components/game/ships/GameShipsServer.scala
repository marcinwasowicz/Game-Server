package components.game.ships

import akka.actor.ActorRef


import scala.collection.mutable

class GameShipsServer(playersInRoom: mutable.HashMap[String, ActorRef]) extends GameShips {
  val hitFailed = false
  val hitSuccess = true
  val sinkFailed = false
  val sinkSuccess = true

  val width = 10
  val height= 10
  val numberOfShips = 10

  var clientsBoardDictionary = new mutable.HashMap[String, mutable.HashMap[(Int, Int), Ship]]()
  var gameClientDictionary = new mutable.HashMap[String, ActorRef]()
  var counter = 0

  def initializeBoard(board: mutable.HashMap[(Int, Int), Ship]): Unit = {}

  def tryShooting(target: (Int, Int), shooter: ActorRef): Unit = {
    val shooterName: String = gameClientDictionary.filter(pair => pair._2 == shooter).keys.head
    val targetName: String = gameClientDictionary.filter(pair => pair._2 != shooter).keys.head

    if (clientsBoardDictionary.apply(targetName).contains(target)) {
      clientsBoardDictionary.apply(targetName).apply(target).getDamage()
      gameClientDictionary.foreach { case (_, ref) => ref ! ShotResultMessage(target, shooterName, hitSuccess,
        clientsBoardDictionary.apply(targetName).apply(target).isSunk())
      }
    } else {
      gameClientDictionary.foreach { case (_, ref) => ref ! ShotResultMessage(target, shooterName, hitFailed, sinkFailed) }
    }
  }


  def initializeAndSendBoards(): Unit = gameClientDictionary.foreach{case (name, ref) =>
    initializeBoard(clientsBoardDictionary.apply(name))
    ref ! InitBoardMessage((width, height),clientsBoardDictionary.apply(name).keysIterator.toList, false )
  }


  override def receive: Receive = {
    case GameClientCreated(name) =>
      gameClientDictionary.put(name, sender())
      clientsBoardDictionary.put(name, new mutable.HashMap[(Int, Int), Ship]())
      counter += 1
      if(counter >= 2){
        initializeAndSendBoards()
      }
    case ShotRequestMessage(target) =>
      tryShooting(target, sender())
  }
}
