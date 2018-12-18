package com.esgi.davidlinhares.chess.view

import com.esgi.davidlinhares.chess.game.Game
import com.esgi.davidlinhares.chess.model.Box
import com.esgi.davidlinhares.chess.model.Pawn

class ChessPresenter(override val game: Game) : IChessPresenter {
    var listener: ChessActivityListener? = null
    override fun boxSelectedAction(box: Box, pawn: Pawn?) {
        if (pawn != null) {
            val movesAvailable = game.playerSelectedBox(box, pawn)
            if (movesAvailable.isNotEmpty()) listener?.also { it.onBoxSelectedRetrieved(movesAvailable) }
        } else {
            if (game.playerMove(box)) listener?.also { it.onPawnMovementSucees() } else listener?.also { it.onPawnMovementError() }
        }
    }

    override fun getChessboardList(): List<Pair<Box, Pawn?>> {
        return game.printChessboard()
    }
}