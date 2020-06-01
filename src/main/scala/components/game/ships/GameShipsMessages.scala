package components.game.ships

import components.common.GameMessage

final case class InitBoardMessage(positions: List[(Int, Int)], isFirst: Boolean) extends GameMessage
final case class ShotRequestMessage(target: (Int, Int)) extends GameMessage
final case class ShotResultMessage(target: (Int, Int), shooter: String,  hit: Boolean, sunk: Boolean) extends GameMessage
final case class GameEndMessage(winner: String) extends GameMessage
final case class GameClientCreated(name: String) extends GameMessage


