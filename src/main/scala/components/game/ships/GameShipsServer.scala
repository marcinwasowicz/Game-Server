package components.game.ships

import akka.actor.ActorRef
import scala.collection.mutable
import scala.util.Random
class GameShipsServer() extends GameShips {

  val width = 10
  val height= 10
  val numberOfShips = 1
  val shipLength = 3

  var clientBoards = new mutable.HashMap[String, mutable.HashMap[(Int, Int), Ship]]()
  var clientDictionary = new mutable.HashMap[String, ActorRef]()
  var isFirst = new mutable.HashMap[String, Boolean]()

  def checkRow(position: (Int, Int), diff: (Int, Int), length: Int, dict: mutable.HashMap[(Int, Int), Ship]): Boolean =
    (0 until length).toList.forall(num => !dict.contains((position._1 + num*diff._1, position._2+num*diff._2)))

  def fillRow(position: (Int, Int), diff: (Int, Int), length: Int, dict: mutable.HashMap[(Int, Int), Ship], ship: Ship): Unit =
    (0 until length).toList.foreach(num => dict.put((position._1 + num*diff._1, position._2+num*diff._2), ship))

  def checkBorders(position: (Int, Int), length: Int, direction: (Int, Int)): Boolean =
    position._1+(length-1)*direction._1 >= 0 && position._1+(length-1)*direction._1 < height &&
    position._2+(length-1)*direction._2 >=0 && position._2+(length-1)*direction._2 < width

  def tryPosition(position: (Int, Int), length: Int, dict: mutable.HashMap[(Int, Int), Ship]): Boolean =
    List((-1, 0), (1, 0), (0, 1), (0, -1)).
      filter(direction => checkBorders(position, length, direction)).
        foldLeft(false)((acc, direction) =>
          if(!acc && checkRow(position, direction, length, dict)){
            fillRow(position, direction, length, dict, new Ship(length))
            !acc
          } else acc)

  def findPosition(length: Int, board: mutable.HashMap[(Int, Int), Ship]): Unit =
    while(!tryPosition((Random.nextInt(height), Random.nextInt(width)), length, board)){}

  def initializeBoard(board: mutable.HashMap[(Int, Int), Ship], numOfShips: Int, length: Int): Unit =
    (1 to numOfShips).foreach( _ => findPosition(length, board))

  def sendBoard(keyValPair: (String, ActorRef)): Unit =
    keyValPair._2 ! InitBoardMessage((height, width), clientBoards.apply(keyValPair._1).keys.toList,isFirst.apply(keyValPair._1))

  def initializeAndSendBoards(): Unit = {
    clientBoards.foreach(keyValPair => initializeBoard(clientBoards.apply(keyValPair._1), numberOfShips, shipLength))
    clientDictionary.foreach(keyValPair => sendBoard(keyValPair))
  }

  def incrementAndCheckClients(name: String, ref: ActorRef): Unit = {
    clientDictionary.put(name, ref)
    clientBoards.put(name, new mutable.HashMap[(Int, Int), Ship]())
    if(clientDictionary.size >= 2){
      isFirst.put(name, false)
      initializeAndSendBoards()
    }
    else{
      isFirst.put(name, true)
    }
  }

  def shootShip(targetBoard: mutable.HashMap[(Int, Int), Ship], target: (Int,Int), shooterName: String): Unit = {
    val targetShip = targetBoard.apply(target)
    targetShip.getDamage()
    clientDictionary.values.foreach(ref => ref ! ShotResultMessage(target, shooterName, true,targetShip.isSunk()))
  }

  def checkForWinner(targetBoard: mutable.HashMap[(Int, Int), Ship], shooterName: String): Unit =
    if(targetBoard.values.toList.forall(ship => ship.isSunk())){
      clientDictionary.values.foreach(ref => ref ! GameEndMessage(shooterName))
    }

  def tryShooting(target: (Int, Int), shooter: ActorRef): Unit = {
    val shooterName: String = clientDictionary.filter(pair => pair._2 == shooter).keys.head
    val targetName: String = clientDictionary.filter(pair => pair._2 != shooter).keys.head
    val targetBoard = clientBoards.apply(targetName)

    if (targetBoard.contains(target)) {
      shootShip(targetBoard, target, shooterName)
      checkForWinner(targetBoard, shooterName)
    }
    else {
      clientDictionary.values.foreach(ref => ref ! ShotResultMessage(target, shooterName, false, false))
    }
  }

  override def receive: Receive = {
    case GameClientCreated(name) =>
      incrementAndCheckClients(name, sender())
    case ShotRequestMessage(target) =>
      tryShooting(target, sender())
  }
}