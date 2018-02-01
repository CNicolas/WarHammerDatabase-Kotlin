package entities

data class Player(override val name: String,
                  override val id: Int = -1) : WarHammerNamedEntity