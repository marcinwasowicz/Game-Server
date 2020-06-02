package components.game.ships

class Ship(var healthPoints: Int) {
  def getDamage() = {
    healthPoints = healthPoints - 1
  }

  def isSunk(): Boolean = {
    healthPoints <= 0
  }
}
