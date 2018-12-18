package com.esgi.davidlinhares.chess.model

data class Move(val pawn: Pawn, val from: Box, val pawnTaken: Pawn?, val to: Box)