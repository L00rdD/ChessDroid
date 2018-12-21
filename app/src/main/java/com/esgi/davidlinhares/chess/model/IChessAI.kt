package com.esgi.davidlinhares.chess.model

import com.esgi.davidlinhares.chess.game.ChessBoard

interface IChessAI {
    val chessboard: ChessBoard
    val difficulty: GameDifficulty
    val side: ChessSide

    fun play(): Pair<Box, Box>
}
