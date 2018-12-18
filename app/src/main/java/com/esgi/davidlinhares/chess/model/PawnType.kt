package com.esgi.davidlinhares.chess.model

enum class PawnType(val description: String, val points: Int) {
    PAWN("Pawn", 100),
    KNIGHT("Knight", 300),
    QUEEN("Queen", 900),
    ROOK("Rook", 500),
    BISHOP("Bishop", 300),
    KING("king", 0)
}
