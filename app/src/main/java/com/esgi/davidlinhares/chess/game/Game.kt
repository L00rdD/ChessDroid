package com.esgi.davidlinhares.chess.game
import ChessBoard
import com.esgi.davidlinhares.chess.model.Box
import com.esgi.davidlinhares.chess.model.GameType
import com.esgi.davidlinhares.chess.model.Pawn

class Game(private val chessBoard: ChessBoard, val gameType: GameType) {
    var pawnSelected: Pawn? = null
    private var pawnBox: Box? = null
    private var pawnSelectedMovementsAvailable: List<Box> = emptyList()

    fun printChessboard(): List<Pair<Box, Pawn?>> {
        return chessBoard.getChessboard()
    }

    fun playerSelectedBox(box: Box, pawn: Pawn): List<Box> {
        pawnSelectedMovementsAvailable = chessBoard.getCurrentMoveAvailable(box)
        if (!pawnSelectedMovementsAvailable.isEmpty()) {
            pawnSelected = pawn
            pawnBox = box
        }
        return pawnSelectedMovementsAvailable
    }

    fun playerMove(box: Box): Boolean {
        pawnSelected = null
        val fromBox = pawnBox ?: return false
        pawnBox = null
        return chessBoard.move(fromBox, box)
    }
}