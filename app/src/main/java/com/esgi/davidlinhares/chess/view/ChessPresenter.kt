package com.esgi.davidlinhares.chess.view

import com.esgi.davidlinhares.chess.game.ChessBoard
import com.esgi.davidlinhares.chess.game.Game
import com.esgi.davidlinhares.chess.model.Box
import com.esgi.davidlinhares.chess.model.GameType
import com.esgi.davidlinhares.chess.model.KingStatus
import com.esgi.davidlinhares.chess.model.Pawn

class ChessPresenter(override var game: Game, val context: ChessActivity) : IChessPresenter {
    var listener: ChessActivityListener? = null
    var moves: Int = 0

    override fun boxSelectedAction(box: Box, pawn: Pawn?) {
        if (pawn != null && pawn.side == game.getPlayingSide() && !game.castling) {
            val movesAvailable = game.playerSelectedBox(box, pawn)
            if (movesAvailable.isNotEmpty()) listener?.also { it.onBoxSelectedRetrieved(movesAvailable) }
        } else {
            if (game.playerMove(box)) {
                listener?.also {
                    moves++
                    if (game.gameType == GameType.SINGLE_PLAYER && !isKingMat()) {
                        game.iaMove()
                    }
                    it.onPawnMovementSuccess()
                }
            } else listener?.also { it.onPawnMovementError() }
        }
    }

    override fun undoButtonClicked() {
        game.undo()
        moves--
        listener?.also { it.onUndoActionCompleted() }
    }

    override fun getChessboardList(): List<Pair<Box, Pawn?>> {
        return game.printChessboard()
    }

    override fun isKingChecked(): Boolean {
        return game.kingStatus == KingStatus.CHECKED
    }

    override fun isKingMat(): Boolean {
        return game.kingStatus == KingStatus.MAT
    }

    override fun menuButtonClicked() {
        this.context.finish()
    }

    override fun replayButtonClicked() {
        this.listener?.also {
            this.game = Game(ChessBoard(), game.gameType, game.ai)
            moves = 0
            it.onGameReseted()
        }
    }
}