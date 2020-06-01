package components.game.ships

import akka.actor.ActorRef
import akka.stream.FlowMonitorState.Finished
import components.common.Signal
import components.game.ships
import components.game.ships.Field.Field
import components.game.ships.GameState.GameState

import scala.collection.mutable

object GameState extends Enumeration{
  type GameState = Value
  val WaitingForStart, InProgress, Finished = Value
}

object Field extends Enumeration{
  type Field = Value
  val Missed= Value(".")
  val Hit = Value("X")
  val Ship = Value("O")
  val Empty = Value("_")
}

class GameShipsClient(name: String, serverGameActor: ActorRef) extends GameShips {
  var isMyTurn: Option[Boolean] = None
  var gameState: GameState = GameState.WaitingForStart
  var myBoard: Array[Array[ships.Field.Value]] = Array()
  var enemyBoard: Array[Array[ships.Field.Value]] = Array()

  serverGameActor ! GameClientCreated(name)

  override def receive: Receive = {
    case Signal(s"shoot $coords") =>
      if (isMyTurn.get) {
        val intCoords = coords.split(" ").map(s => s.toInt)
        serverGameActor ! ShotRequestMessage((intCoords(0), intCoords(1)))
      }
    case InitBoardMessage(gridSize, positions, isFirst) =>
      initializeGame(gridSize, positions, isFirst)
      printCurrentState("GAME STARTED!")
    case ShotResultMessage(target, shooterName, hit, sunk) =>
      shooterName match {
        case this.name =>
          enemyBoard(target._1)(target._2) = if(hit) Field.Hit else Field.Missed
          isMyTurn = Some(false)
          printCurrentState(if(hit) "You hit enemy ship!" else "You missed :(")
        case _ =>
          myBoard(target._1)(target._2) = if(hit) Field.Hit else Field.Missed
          isMyTurn = Some(true)
          printCurrentState(if(hit) "You got hit!" else "Enemy missed")
      }
    //
    case GameEndMessage(winnerName) =>
      //
  }

  def initializeGame(gridSize: (Int, Int), shipPositions: List[(Int, Int)], isFirst: Boolean): Unit = {
    myBoard = Array.fill(gridSize._1, gridSize._2)(Field.Empty)
    enemyBoard = Array.fill(gridSize._1, gridSize._2)(Field.Empty)
    isMyTurn = Some(isFirst)
    shipPositions.foreach(pos => myBoard(pos._1)(pos._2) = Field.Ship)
    gameState = GameState.InProgress
  }

  def getBoardRepr(board :Array[Array[Field]]): Array[String] = {
    // works for one-digit coordinates

    val rows = board.length
    val cols = board(0).length
    var res = new Array[String](rows+2)

    res(0) = "  " ++ Array.range(0, cols).map(c => c.toString).mkString("")
    res(1) = " "*(cols+2)
    for(r <- 0 until rows){
      res(2+r) = ('A' to 'Z')(r).toString ++ " " ++ Array.range(0, cols).map(c => board(r)(c).toString).mkString("")
    }

    res
  }

  def printCurrentState(message: String): Unit = {
    val myBoardRepr = getBoardRepr(myBoard)
    val enemyBoardRepr = getBoardRepr(enemyBoard)
    val (rows, cols) = (myBoardRepr.length, myBoardRepr(0).length)
    val myBoardString = "My board"
    val enemyBoardString = "Enemy board"
    val gapWidth = 3
    val header = myBoardString ++ " "*(cols - myBoardString.length + gapWidth) ++ enemyBoardString ++ " "*(cols-enemyBoardString.length)

    println(header)
    for(row <- 0 until rows) {
      println(myBoardRepr(row) ++ " "*gapWidth ++ enemyBoardRepr(row))
    }
    println("\n")
    println(message)
    println(if(isMyTurn.get) "Make your move:" else "Waiting for opponents move")
  }
}
