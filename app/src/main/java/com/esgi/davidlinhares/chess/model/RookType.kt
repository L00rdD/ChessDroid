package com.esgi.davidlinhares.chess.model

enum class RookType(val white: Array<Box>?, val black: Array<Box>?) {
    NONE(null, null),
    SMALL(arrayOf(Box.H1), arrayOf(Box.H8)),
    BIG(arrayOf(Box.A1), arrayOf(Box.A8)),
    ALL(arrayOf(Box.H1, Box.A1), arrayOf(Box.H8, Box.A8))
}
