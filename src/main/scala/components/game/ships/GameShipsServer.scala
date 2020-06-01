package components.game.ships

import akka.actor.ActorRef

import scala.collection.mutable
import scala.util.Random
class GameShipsServer() extends GameShips {

  val width = 10
  val height= 10
  val numberOfShips = 1
  val shipLength = 3

  var clientsBoardDictionary = new mutable.HashMap[String, mutable.HashMap[(Int, Int), Ship]]()
  var gameClientDictionary = new mutable.HashMap[String, ActorRef]()
  var counter = 0
  var isFirst = true

  def checkRow(position: (Int, Int), diff: (Int, Int), length: Int, dict: mutable.HashMap[(Int, Int), Ship]): Boolean =
    (0 until length).toList.forall(num => !dict.contains((position._1 + num*diff._1, position._2+num*diff._2)))

  def fillRow(position: (Int, Int), diff: (Int, Int), length: Int, dict: mutable.HashMap[(Int, Int), Ship], ship: Ship): Unit = {
    (0 until length).toList.foreach(num => dict.put((position._1 + num*diff._1, position._2+num*diff._2), ship))
  }

  def checkBorders(position: (Int, Int), length: Int, direction: (Int, Int)): Boolean =
    position._1+(length-1)*direction._1 >= 0 && position._1+(length-1)*direction._1 < height &&
    position._2+(length-1)*direction._2 >=0 && position._2+(length-1)*direction._2 < width

  def tryPosition(position: (Int, Int), length: Int, dict: mutable.HashMap[(Int, Int), Ship]): Boolean = {
    val directions = List((-1, 0), (1, 0), (0, 1), (0, -1)).filter(direction => checkBorders(position, length, direction))
    directions.foldLeft(false)((acc, direction) =>
      if(!acc && checkRow(position, direction, length, dict)){
        fillRow(position, direction, length, dict, new Ship(length))
        !acc
      } else acc)
  }

  def initializeBoard(board: mutable.HashMap[(Int, Int), Ship]): Unit = {
    val randomGenerator = Random
    for(_<- 1 to numberOfShips){
      var position = (randomGenerator.nextInt(height), randomGenerator.nextInt(width))
      while(!tryPosition(position, shipLength, board)){
        position = (randomGenerator.nextInt(height), randomGenerator.nextInt(width))
      }
    }
  }

  def incrementAndCheckClients(): Unit = {
    counter = counter + 1
    if(counter >= 2){
      initializeAndSendBoards()
    }
  }

  def checkForWinner(shooterName: String, targetName: String) = {
    if(clientsBoardDictionary.apply(targetName).values.toList.forall(ship => ship.isSunk())){
      gameClientDictionary.foreach{case(_, ref) => ref ! GameEndMessage(shooterName)}
    }
  }

  def tryShooting(target: (Int, Int), shooter: ActorRef): Unit = {
    val shooterName: String = gameClientDictionary.filter(pair => pair._2 == shooter).keys.head
    val targetName: String = gameClientDictionary.filter(pair => pair._2 != shooter).keys.head

    if (clientsBoardDictionary.apply(targetName).contains(target)) {
      clientsBoardDictionary.apply(targetName).apply(target).getDamage()
      gameClientDictionary.foreach { case (_, ref) => ref ! ShotResultMessage(target, shooterName, true,
        clientsBoardDictionary.apply(targetName).apply(target).isSunk())
      }
      checkForWinner(shooterName, targetName)
    } else {
      gameClientDictionary.foreach { case (_, ref) => ref ! ShotResultMessage(target, shooterName, false, false) }
    }
  }

  def initializeAndSendBoards(): Unit = gameClientDictionary.foreach{case (name, ref) =>
    initializeBoard(clientsBoardDictionary.apply(name))
    ref ! InitBoardMessage((width, height),clientsBoardDictionary.apply(name).keys.toList, isFirst)
    isFirst = !isFirst
  }

  override def receive: Receive = {
    case GameClientCreated(name) =>
      gameClientDictionary.put(name, sender())
      clientsBoardDictionary.put(name, new mutable.HashMap[(Int, Int), Ship]())
      incrementAndCheckClients()
    case ShotRequestMessage(target) =>
      tryShooting(target, sender())
  }
}