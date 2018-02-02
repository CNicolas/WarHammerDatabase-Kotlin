package warhammer.database.entities

data class PlayerEntity(override val name: String,
                        override val id: Int = -1) : NamedEntity