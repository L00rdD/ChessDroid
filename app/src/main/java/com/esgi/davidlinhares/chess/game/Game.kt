package com.esgi.davidlinhares.chess.game
import com.esgi.davidlinhares.chess.model.*

class Game(private val chessBoard: ChessBoard, val gameType: GameType, val ai: ChessAI = ChessAI(chessBoard, GameDifficulty.NORMAL, ChessSide.BLACK)) {
    private var pawnSelected: Pawn? = null
    var castling = false
    var kingStatus: KingStatus = KingStatus.FREE
    private var pawnBox: Box? = null
    private var pawnSelectedMovementsAvailable: MutableList<Box> = mutableListOf()
    private var castlingMoves: MutableMap<Box, RookType> = mutableMapOf()

    fun printChessboard(): List<Pair<Box, Pawn?>> {
        return chessBoard.getChessboard()
    }

    fun getPlayingSide(): ChessSide {
        return chessBoard.sidePlaying
    }

    fun playerSelectedBox(box: Box, pawn: Pawn): List<Box> {
        pawnSelectedMovementsAvailable = chessBoard.getCurrentMoveAvailable(box).toMutableList()
        if (!pawnSelectedMovementsAvailable.isEmpty()) {
            pawnSelected = pawn
            pawnBox = box
            if (pawn.type == PawnType.KING) {
                if (chessBoard.getCastling() != RookType.NONE) castling = true
            }
        }
        return pawnSelectedMovementsAvailable
    }

    fun playerMove(box: Box): Boolean {
        pawnSelected = null
        val fromBox = pawnBox ?: return false
        pawnBox = null
        castling = false

        val move = chessBoard.move(fromBox, box)
        if (move) kingStatus = chessBoard.getKingStatus()

        return move
    }

    fun iaMove(): Boolean {
        if (gameType == GameType.VERSUS) return false

        val iaMove = ai.play()
        val move = chessBoard.move(iaMove.first, iaMove.second)
        if (move) kingStatus = chessBoard.getKingStatus()

        return move
    }

    fun undo() {
        chessBoard.cancelLastMove()
    }
}