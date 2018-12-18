package com.esgi.davidlinhares.chess.model

interface IChessBoard {
    val boxes: MutableMap<Box, Pawn?>
    var sidePlaying: ChessSide
    var playCount: Int
    var playsHistoric: ArrayList<Move>

    fun getSideHistorical(side: ChessSide): List<Move>
    fun move(from: Box, to: Box): Boolean
    fun cancelLastMove()
    fun switchSidePlaying()
    fun getKingStatus(side: ChessSide): KingStatus
    fun isPawnUnderAttack(pawn: Pawn): Boolean
    fun printChessBoard()
    fun printHistorical()
    fun gradePawnPosition(pawn: Pawn)
}
