package warhammer.database.entities.hand

import warhammer.database.entities.NamedEntity

data class Hand(override val id: Int = -1,
                override var name: String,
                val characteristicDicesCount: Int = 0,
                val expertiseDicesCount: Int = 0,
                val fortuneDicesCount: Int = 0,
                val conservativeDicesCount: Int = 0,
                val recklessDicesCount: Int = 0,
                val challengeDicesCount: Int = 0,
                val misfortuneDicesCount: Int = 0) : NamedEntity