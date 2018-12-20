package com.esgi.davidlinhares.chess.game

import com.esgi.davidlinhares.chess.model.Box
import com.esgi.davidlinhares.chess.model.ChessSide
import com.esgi.davidlinhares.chess.model.GameDifficulty
import com.esgi.davidlinhares.chess.model.IChessIA

class ChessIA(override val chessboard: ChessBoard, override val difficulty: GameDifficulty, override val side: ChessSide) : IChessIA {
    private var playSequence: MutableList<Pair<PlayerMove, PlayerMove>> = mutableListOf()

    override fun play(): Pair<Box, Box> {
        return getPlaySequence(difficulty).first().first.toPair()
    }

    private fun getPlaySequence(difficulty: GameDifficulty): List<Pair<PlayerMove, PlayerMove>> {
        return when (difficulty) {
            GameDifficulty.NORMAL -> determineNormalSequence()
            GameDifficulty.HARD -> determineBestSequence()
        }
    }

    private fun determineNormalSequence(): List<Pair<PlayerMove, PlayerMove>> {
        return determineBestSequence()
    }

    private fun determineBestSequence(): List<Pair<PlayerMove, PlayerMove>> {
        if (isPredictedMove()) {
            playSequence.removeAt(0)
            if (playSequence.count() > 0) return playSequence
        }

        playSequence.add(Pair(PlayerMove(getBestMove(side)), PlayerMove(getBestMove(chessboard.getOppositeSide(side)))))

        return playSequence
    }

    private fun isPredictedMove(): Boolean {
        if (chessboard.playsHistoric.isEmpty() || playSequence.isEmpty()) return false

        val opponentMove = chessboard.playsHistoric.last()
        val bestOpponentMove = playSequence.first().second

        return opponentMove.from == bestOpponentMove.from && opponentMove.to == bestOpponentMove.to
    }

    private fun getBestMove(side: ChessSide): Pair<Box, Box> {
        val pawns = chessboard.getPawnsWichCanMove(side)
        val firstBox = chessboard.getBox(pawns.first()) // Used to initiate best move
        var bestMove = Pair(firstBox, firstBox)
        var grade = chessboard.gradeMove(firstBox, firstBox)

        pawns.forEach { pawn ->
            val box = chessboard.getBox(pawn)
            chessboard.getMovePossibilities(box)?.also { possibilities ->
                possibilities.forEach { possibility ->
                    val possibilityGrade = chessboard.gradeMove(box, possibility)
                    if (grade < possibilityGrade) {
                        grade = possibilityGrade
                        bestMove = Pair(box, possibility)
                    }
                }
            }
        }

        return bestMove
    }
}