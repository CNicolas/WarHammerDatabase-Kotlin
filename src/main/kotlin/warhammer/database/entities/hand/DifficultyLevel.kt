package warhammer.database.entities.hand

enum class DifficultyLevel(val challengeDicesCount: kotlin.Int) {
    NONE(0),
    EASY(1),
    MEDIUM(2),
    HARD(2),
    EXTREME(2),
    GODLIKE(2)
}