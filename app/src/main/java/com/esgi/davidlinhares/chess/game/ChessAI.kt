package com.esgi.davidlinhares.chess.game

import com.esgi.davidlinhares.chess.model.Box
import com.esgi.davidlinhares.chess.model.ChessSide
import com.esgi.davidlinhares.chess.model.GameDifficulty
import com.esgi.davidlinhares.chess.model.IChessAI

class ChessAI(override val chessboard: ChessBoard, override val difficulty: GameDifficulty, override val side: ChessSide) : IChessAI {
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
        val predicted = isPredictedMove() //Check prediction
        if (playSequence.isNotEmpty()) playSequence.removeAt(0) // Remove move already done
        if (predicted && playSequence.count() > 0) return playSequence

        val bestMove = getBestMove(side)
        val newChessBoard = chessboard.clone()
        newChessBoard.move(bestMove.first, bestMove.second)
        val bestOpponentMove = getBestMove(chessboard.getOppositeSide(side), newChessBoard)

        playSequence.add(Pair(PlayerMove(bestMove.first, bestMove.second), PlayerMove(bestOpponentMove.first, bestOpponentMove.second)))

        return playSequence
    }

    private fun isPredictedMove(): Boolean {
        if (chessboard.playsHistoric.isEmpty() || playSequence.isEmpty()) return false

        val opponentMove = chessboard.playsHistoric.last()
        val bestOpponentMove = playSequence.first().second

        return opponentMove.from == bestOpponentMove.from && opponentMove.to == bestOpponentMove.to
    }

    private fun getBestMove(side: ChessSide): Pair<Box, Box> {
        return getBestMove(side, this.chessboard)
    }

    private fun getBestMove(side: ChessSide, chessboard: ChessBoard): Pair<Box, Box> {
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