package com.esgi.davidlinhares.chess.game
import com.esgi.davidlinhares.chess.model.Box

data class PlayerMove(val from: Box, val to: Box) {
    constructor(move: Pair<Box, Box>) : this(move.first, move.second)
    fun toPair(): Pair<Box, Box> = Pair(from, to)
}