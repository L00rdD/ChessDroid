package com.esgi.davidlinhares.chess.view
import com.esgi.davidlinhares.chess.game.Game
import com.esgi.davidlinhares.chess.model.Box
import com.esgi.davidlinhares.chess.model.KingStatus
import com.esgi.davidlinhares.chess.model.Pawn

interface IChessPresenter {
    val game: Game
    fun boxSelectedAction(box: Box, pawn: Pawn?)
    fun getChessboardList(): List<Pair<Box, Pawn?>>
    fun isKingChecked(): Boolean
    fun isKingMat(): Boolean
    fun undoButtonClicked()
}

