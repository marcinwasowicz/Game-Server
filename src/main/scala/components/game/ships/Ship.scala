package components.game.ships

class Ship(healthPoints: Int) {
  def getDamage() = {
    healthPoints -= 1
  }

  def isSunk(): Boolean = {
    healthPoints == 0
  }
}
