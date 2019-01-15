package com.esgi.davidlinhares.chess.view

import com.esgi.davidlinhares.chess.model.Box
import com.esgi.davidlinhares.chess.model.Pawn

interface ChessActivityListener {
    fun onBoxViewClicked(box: Box, pawn: Pawn?)
    fun onBoxSelectedRetrieved(boxes: List<Box>)
    fun onPawnMovementSuccess()
    fun onPawnMovementError()
    fun onUndoActionCompleted()
    fun onGameReseted()
}