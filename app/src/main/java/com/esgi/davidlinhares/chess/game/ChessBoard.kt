package com.esgi.davidlinhares.chess.game

import com.esgi.davidlinhares.chess.model.Box
import com.esgi.davidlinhares.chess.model.*
import com.github.ajalt.mordant.TermColors

class ChessBoard: IChessBoard {
    override val boxes = mutableMapOf(
            //Black side
            Pair(Box.A8, Pawn(PawnType.ROOK, ChessSide.BLACK)),
            Pair(Box.B8, Pawn(PawnType.KNIGHT, ChessSide.BLACK)),
            Pair(Box.C8, Pawn(PawnType.BISHOP, ChessSide.BLACK)),
            Pair(Box.D8, Pawn(PawnType.QUEEN, ChessSide.BLACK)),
            Pair(Box.E8, Pawn(PawnType.KING, ChessSide.BLACK)),
            Pair(Box.F8, Pawn(PawnType.BISHOP, ChessSide.BLACK)),
            Pair(Box.G8, Pawn(PawnType.KNIGHT, ChessSide.BLACK)),
            Pair(Box.H8, Pawn(PawnType.ROOK, ChessSide.BLACK)),
            Pair(Box.A7, Pawn(PawnType.PAWN, ChessSide.BLACK)),
            Pair(Box.B7, Pawn(PawnType.PAWN, ChessSide.BLACK)),
            Pair(Box.C7, Pawn(PawnType.PAWN, ChessSide.BLACK)),
            Pair(Box.D7, Pawn(PawnType.PAWN, ChessSide.BLACK)),
            Pair(Box.E7, Pawn(PawnType.PAWN, ChessSide.BLACK)),
            Pair(Box.F7, Pawn(PawnType.PAWN, ChessSide.BLACK)),
            Pair(Box.G7, Pawn(PawnType.PAWN, ChessSide.BLACK)),
            Pair(Box.H7, Pawn(PawnType.PAWN, ChessSide.BLACK)),

            //Empty boxes
            Pair(Box.A6, null),
            Pair(Box.B6, null),
            Pair(Box.C6, null),
            Pair(Box.D6, null),
            Pair(Box.E6, null),
            Pair(Box.F6, null),
            Pair(Box.G6, null),
            Pair(Box.H6, null),
            Pair(Box.A5, null),
            Pair(Box.B5, null),
            Pair(Box.C5, null),
            Pair(Box.D5, null),
            Pair(Box.E5, null),
            Pair(Box.F5, null),
            Pair(Box.G5, null),
            Pair(Box.H5, null),
            Pair(Box.A4, null),
            Pair(Box.B4, null),
            Pair(Box.C4, null),
            Pair(Box.D4, null),
            Pair(Box.E4, null),
            Pair(Box.F4, null),
            Pair(Box.G4, null),
            Pair(Box.H4, null),
            Pair(Box.A3, null),
            Pair(Box.B3, null),
            Pair(Box.C3, null),
            Pair(Box.D3, null),
            Pair(Box.E3, null),
            Pair(Box.F3, null),
            Pair(Box.G3, null),
            Pair(Box.H3, null),

            //White side
            Pair(Box.A2, Pawn(PawnType.PAWN, ChessSide.WHITE)),
            Pair(Box.B2, Pawn(PawnType.PAWN, ChessSide.WHITE)),
            Pair(Box.C2, Pawn(PawnType.PAWN, ChessSide.WHITE)),
            Pair(Box.D2, Pawn(PawnType.PAWN, ChessSide.WHITE)),
            Pair(Box.E2, Pawn(PawnType.PAWN, ChessSide.WHITE)),
            Pair(Box.F2, Pawn(PawnType.PAWN, ChessSide.WHITE)),
            Pair(Box.G2, Pawn(PawnType.PAWN, ChessSide.WHITE)),
            Pair(Box.H2, Pawn(PawnType.PAWN, ChessSide.WHITE)),
            Pair(Box.A1, Pawn(PawnType.ROOK, ChessSide.WHITE)),
            Pair(Box.B1, Pawn(PawnType.KNIGHT, ChessSide.WHITE)),
            Pair(Box.C1, Pawn(PawnType.BISHOP, ChessSide.WHITE)),
            Pair(Box.D1, Pawn(PawnType.QUEEN, ChessSide.WHITE)),
            Pair(Box.E1, Pawn(PawnType.KING, ChessSide.WHITE)),
            Pair(Box.F1, Pawn(PawnType.BISHOP, ChessSide.WHITE)),
            Pair(Box.G1, Pawn(PawnType.KNIGHT, ChessSide.WHITE)),
            Pair(Box.H1, Pawn(PawnType.ROOK, ChessSide.WHITE))
    )
    override var sidePlaying = ChessSide.WHITE
    override var playCount = 0
    override var playsHistoric: ArrayList<Move> = arrayListOf()
    var lastmoveIsCastling = false
    private var lastmoveIsPassantCapture = false
    private var doubleMovePawn: Pawn? = null
    private var passantCapture: Box? = null
    private var castlingState: RookType = RookType.NONE

    override fun getSideHistorical(side: ChessSide): List<Move> {
        return playsHistoric.filter { it.pawn.side == side }
    }

    override fun move(from: Box, to: Box): Boolean {
        val pawn = boxes[from] ?: return false
        val taken = getPawn(to)
        val possibilities = getMovePossibilities(from)
        if (possibilities == null || possibilities.isEmpty() || !possibilities.contains(to)) return false
        checkPassantCapture(from, to)
        setDoubleMovePawn(from, to)
        if (pawn.type == PawnType.KING && castlingState != RookType.NONE) {
            if (sidePlaying == ChessSide.WHITE) {
                val type = RookType.values().first { it != RookType.NONE && it != RookType.ALL && it.white!!.contains(to)}
                castling(pawn.side, type)
            } else {
                val type = RookType.values().first { it != RookType.NONE && it != RookType.ALL && it.black!!.contains(to)}
                castling(pawn.side, type)
            }
            castlingState = RookType.NONE
        }
        else {
            lastmoveIsCastling = false
            movePawn(from, to)
            this.playsHistoric.add(Move(pawn, from, taken, to))
        }

        if (getKingStatus() != KingStatus.FREE) {
            movePawn(to, from)
            this.playsHistoric.removeAt(playsHistoric.lastIndex)
            return false
        }

        switchSidePlaying()

        return true
    }

    override fun getKingStatus(side: ChessSide): KingStatus {
        val box = boxes.asIterable().first { it.value != null && it.value!!.side == side && it.value!!.type == PawnType.KING }
        val king = box.value
        val oppositeSide = getOppositeSide(side)
        val oppositeMovePossibilities = getAllMovePossibilities(oppositeSide)
        val movePossibilities = getAllMovePossibilities(side).toMutableList()

        if (king == null ||!oppositeMovePossibilities.contains(box.key)) {
            return KingStatus.FREE
        }

        val possibilities = getKingMovePossibilities(king, box.key)?.toMutableList() ?: return KingStatus.MAT
        var possibilitiesCount = 0;

        if (possibilities.count() == 1) {
            movePossibilities.forEach { if (it == possibilities[0]) possibilitiesCount++ }
        }

        var previousBox = box.key
        var previousContent = king
        possibilities.removeIf {
            movePawn(previousBox, box.key)
            boxes[previousBox] = previousContent
            previousBox = it
            previousContent = getPawn(it)
            movePawn(box.key, it)
            (oppositeMovePossibilities.contains(it) && possibilitiesCount < 2)
        }

        movePawn(previousBox, box.key)
        boxes[previousBox] = previousContent

        return if (possibilities.isEmpty()) KingStatus.MAT else KingStatus.CHECKED
    }

    override fun cancelLastMove() {
        if (playsHistoric.isEmpty()) return
        val lastPawn = playsHistoric.last().pawn

        if (lastmoveIsPassantCapture && passantCapture != null) {
            lastmoveIsPassantCapture = false
            boxes[Box.valueOf("${passantCapture!!.letter}${playsHistoric.last().from.number}")] = Pawn(PawnType.PAWN, sidePlaying)
        }

        undo()
        playsHistoric.lastOrNull()?.also { if (getPawn(it.to)!!.side == lastPawn.side) undo() } // if castling historical gets two entries
        switchSidePlaying()
    }

    override fun switchSidePlaying() {
        this.sidePlaying = if (this.sidePlaying == ChessSide.WHITE) ChessSide.BLACK else ChessSide.WHITE
    }

    override fun isPawnUnderAttack(pawn: Pawn): Boolean {
        return getAllMovePossibilities(getOppositeSide(pawn.side)).contains(getBox(pawn))
    }

    override fun printChessBoard() {
        println("\u001Bc")
        val t = TermColors()
        println(t.green("\n     A    B    C    D    E    F    G    H \n"))
        boxes.forEach {
            if (it.key.letter == 'A') print(t.green("${it.key.number}  "))
            if (it.value == null)
                print("|_ _|")
            else {
                if (it.value != null && it.value!!.side == ChessSide.WHITE)
                    print("|_${t.white(it.value!!.type.name[0].toString())}_|")
                else
                    print("|_${t.red(it.value!!.type.name[0].toString())}_|")
            }
            if (it.key.letter == 'H') println(t.green("  ${it.key.number}"))
        }
        println(t.green("\n     A    B    C    D    E    F    G    H \n"))
    }

    override fun printHistorical() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun gradePawnPosition(pawn: Pawn) {

    }

    fun getKingStatus(): KingStatus = getKingStatus(sidePlaying)

    fun getPawn(box: Box): Pawn? {
        return boxes[box]
    }

    fun getCurrentMoveAvailable(box: Box): List<Box> {
        getPawn(box)?.also {
            if(it.side == sidePlaying) return getMovesAvailable(box)
        }
        return emptyList()
    }

    fun getMovesAvailable(box: Box): List<Box> {
        val pawn = getPawn(box) ?: return emptyList()
        val moves = getMovePossibilities(box)?.toMutableList() ?: return emptyList()

        if(getKingStatus(pawn.side) == KingStatus.MAT) return emptyList()
        var previousBox = box
        var previousContent = getPawn(box)
        moves.removeIf {
            movePawn(previousBox, box)
            boxes[previousBox] = previousContent
            previousBox = it
            previousContent = getPawn(it)
            movePawn(box, it)
            (getAllMovePossibilities(getOppositeSide(pawn.side)).contains(getKing(pawn.side)) || getKingStatus() == KingStatus.CHECKED)
        }

        movePawn(previousBox, box)
        boxes[previousBox] = previousContent

        return moves.toList()
    }

    fun getCastling(): RookType {
        return getCastling(sidePlaying)
    }

    fun getCastling(side: ChessSide): RookType {
        val rookType = getCastlingsTypeAvailable(side)
        val kingBox: Box
        var king: Pawn? = null
        val lineBox: Array<Box>

        if (side == ChessSide.WHITE) {
            kingBox = Box.E1
            lineBox = arrayOf(Box.B1, Box.C1, Box.D1, Box.F1, Box.G1)
        } else {
            kingBox = Box.E8
            lineBox = arrayOf(Box.B8, Box.C8, Box.D8, Box.F8, Box.G8)
        }

        king = getPawn(kingBox)
        if (king == null) return  RookType.NONE

        when(rookType) {
            RookType.NONE -> return RookType.NONE
            RookType.SMALL -> {
                if (getPawn(lineBox[3]) != null || getPawn(lineBox[4]) != null) return RookType.NONE
                return RookType.SMALL
            }
            RookType.BIG -> {
                if (getPawn(lineBox[0]) != null || getPawn(lineBox[1]) != null
                                || getPawn(lineBox[2]) != null) return RookType.NONE
                return RookType.BIG
            }
            RookType.ALL -> {
                var big = true
                var small = true
                if (getPawn(lineBox[3]) != null && getPawn(lineBox[4]) != null) small = false
                if (getPawn(lineBox[0]) != null && getPawn(lineBox[1]) != null
                        && getPawn(lineBox[2]) != null) big = false
                if (big && small) return RookType.ALL
                if (big) return RookType.BIG
                if (small) return RookType.SMALL
                return RookType.NONE
            }
        }
    }

    private fun castling(side: ChessSide, type: RookType): Boolean {
        val king = if (side == ChessSide.WHITE) getPawn(Box.E1) else getPawn(Box.E8)
        val rook: Pawn
        if (king == null) return false
        if (side == ChessSide.WHITE) {
            when(type) {
                RookType.NONE -> return false
                RookType.SMALL -> {
                    rook = getPawn(Box.H1) ?: return false
                    movePawn(Box.E1, Box.G1)
                    movePawn(Box.H1, Box.F1)
                    playsHistoric.add(Move(king, Box.E1, null, Box.G1))
                    playsHistoric.add(Move(king, Box.H1, null, Box.F1))
                }
                RookType.BIG -> {
                    rook = getPawn(Box.A1) ?: return false
                    movePawn(Box.E1, Box.C1)
                    movePawn(Box.A1, Box.D1)
                    playsHistoric.add(Move(king, Box.E1, null, Box.C1))
                    playsHistoric.add(Move(king, Box.A1, null, Box.D1))
                }
                RookType.ALL -> return false
            }
        } else {
            when(type) {
                RookType.NONE -> return false
                RookType.SMALL -> {
                    rook = getPawn(Box.H8) ?: return false
                    movePawn(Box.E8, Box.G8)
                    movePawn(Box.H8, Box.F8)
                    playsHistoric.add(Move(king, Box.E8, null, Box.G8))
                    playsHistoric.add(Move(king, Box.H8, null, Box.F8))
                }
                RookType.BIG -> {
                    rook = getPawn(Box.A8) ?: return false
                    movePawn(Box.E8, Box.C8)
                    movePawn(Box.A8, Box.D8)
                    playsHistoric.add(Move(king, Box.E8, null, Box.C8))
                    playsHistoric.add(Move(king, Box.A8, null, Box.D8))
                }
                RookType.ALL -> return false
            }
        }

        lastmoveIsCastling = true
        return lastmoveIsCastling
    }

    private fun getKing(side: ChessSide): Box? {
        return boxes.toList().firstOrNull { it.second != null && it.second!!.side == ChessSide.WHITE && it.second!!.type == PawnType.KING }?.first
    }

    fun getOppositeSide(side: ChessSide): ChessSide {
        return if (side == ChessSide.WHITE) ChessSide.BLACK else ChessSide.WHITE
    }

    private fun getAllMovePossibilities(side: ChessSide) : List<Box?> {
        val possibilities = mutableListOf<Box>()
        val refPawns = boxes
                .filter { it.value != null && it.value!!.side == side }

        refPawns.forEach { entry ->
            val moves = getMovePossibilities(entry.key)
            if (moves != null && moves.isNotEmpty()) {
                moves.forEach {
                    possibilities.add(it)
                }
            }
        }

        return possibilities
    }

    private fun getAllMovePossibilities(pawns: List<Pawn>) : List<Box> {
        val possibilities = mutableListOf<Box>()

        pawns.forEach {
            val moves = getMovePossibilities(getBox(it))
            if (moves != null && moves.isNotEmpty()) {
                moves.forEach {possibility ->
                    if (!possibilities.contains(possibility)) {
                        possibilities.add(possibility)
                    }
                }
            }
        }

        return possibilities
    }

    private fun getKingMovePossibilities(pawn: Pawn, box: Box): List<Box>? {
        val moves = Box.values().asIterable()

        val sides =  moves.filter {
            it != box &&
                    (it.letter == box.letter && (it.number == box.number + 1 || it.number == box.number - 1)) ||
                    (it.number == box.number && (it.letter == box.letter + 1 || it.letter == box.letter - 1)) ||
                    ((it.letter == box.letter - 1 || it.letter == box.letter + 1) && (it.number == box.number + 1 || it.number == box.number - 1))
        }.toMutableList()

        sides.removeIf {
            val p = getPawn(it)
            (p != null && p.side == pawn.side)
        }

        castlingState = getCastling(pawn.side)
        if (castlingState != RookType.NONE) sides.addAll(if (pawn.side == ChessSide.WHITE) castlingState.white!! else castlingState.black!!)

        return sides.toList()
    }

    private fun movePawn(from: Box, to: Box) {
        if (from == to) return
        if (!boxes.contains(from) && !boxes.contains(to)) return
        boxes[to]= boxes[from]
        boxes[from] = null
        val pawn = getPawn(from) ?: return
    }

    private fun getKnightMovePossibilities(pawn: Pawn, box: Box): List<Box>? {
        val moves = Box.values().asIterable()

        val knights = moves.filter {
            (it != box && it.number == box.number + 2 && it.letter == box.letter - 1) ||
                    (it != box && it.number == box.number - 2 && it.letter == box.letter - 1) ||
                    (it != box && it.number == box.number + 2 && it.letter == box.letter + 1) ||
                    (it != box && it.number == box.number - 2 && it.letter == box.letter + 1) ||
                    (it != box && it.number == box.number - 1 && it.letter == box.letter - 2) ||
                    (it != box && it.number == box.number + 1 && it.letter == box.letter - 2) ||
                    (it != box && it.number == box.number - 1 && it.letter == box.letter + 2) ||
                    (it != box && it.number == box.number + 1 && it.letter == box.letter + 2)
        }.toMutableList()

        knights.removeIf {
            val p = getPawn(it)
            p != null && p.side == pawn.side
        }

        return knights.toList()
    }

    private fun getQueenMovePossibilities(pawn: Pawn, box: Box): List<Box>? {
        val rook = getRookMovePossibilities(pawn, box)
        val bishop = getBishopMovePossibilities(pawn, box) ?: return rook
        if (rook == null) return bishop
        val lines = rook.union(bishop).toMutableList()

        return lines.toList()
    }

    private fun getBishopMovePossibilities(pawn: Pawn, box: Box): List<Box>? {
        val moves = Box.values().asIterable()
        val diagonal =  moves.filter {
            ((box.letter - it.letter == box.number - it.number)
                    || (it.letter - box.letter == box.number - it.number))
        }.toMutableList()

        var maxUpLeft = diagonal.filter { getPawn(it) != null && it.letter < box.letter && it.number > box.number }.minBy { it.number }
        var maxDownLeft = diagonal.filter { getPawn(it) != null && it.letter < box.letter && it.number < box.number }.maxBy { it.number }
        var maxUpRight = diagonal.filter { getPawn(it) != null && it.letter > box.letter && it.number > box.number }.minBy { it.number }
        var maxDownRight = diagonal.filter { getPawn(it) != null && it.letter > box.letter && it.number < box.number }.maxBy { it.number }

        if (maxUpLeft != null && getPawn(maxUpLeft)?.side == pawn.side)
            maxUpLeft = diagonal.firstOrNull { it.letter == maxUpLeft!!.letter + 1 && it.number == maxUpLeft!!.number - 1}
        if (maxDownLeft != null && getPawn(maxDownLeft)?.side == pawn.side)
            maxDownLeft = diagonal.firstOrNull { it.letter == maxDownLeft!!.letter + 1 && it.number == maxDownLeft!!.number + 1}
        if (maxUpRight != null && getPawn(maxUpRight)?.side == pawn.side)
            maxUpRight = diagonal.firstOrNull { it.letter == maxUpRight!!.letter - 1 && it.number == maxUpRight!!.number - 1}
        if (maxDownRight != null && getPawn(maxDownRight)?.side == pawn.side)
            maxDownRight = diagonal.firstOrNull { it.letter == maxDownRight!!.letter - 1 && it.number == maxDownRight!!.number + 1}

        diagonal.removeIf {
            (maxUpLeft != null && it.number > maxUpLeft.number && it.letter < maxUpLeft.letter) ||
                    (maxDownLeft != null && it.number < maxDownLeft.number && it.letter < maxDownLeft.letter) ||
                    (maxUpRight != null && it.number > maxUpRight.number && it.letter > maxUpRight.letter) ||
                    (maxDownRight != null && it.number < maxDownRight.number && it.letter > maxDownRight.letter) ||
                    (it == box)
        }

        return diagonal
    }

    private fun getRookMovePossibilities(pawn: Pawn, pos: Box): List<Box>? {
        val moves = Box.values().asIterable()
        val lines = moves
                .filter { (it != pos) && it.letter == pos.letter || it.number == pos.number }
                .toMutableList()

        var westMax = lines.filter { getPawn(it) != null && it.letter < pos.letter }.maxBy { it.letter }
        var northMax = lines.filter { getPawn(it) != null && it.letter == pos.letter && it.number > pos.number }.minBy { it.number }
        var southMax = lines.filter { getPawn(it) != null && it.letter == pos.letter && it.number < pos.number }.maxBy { it.number }
        var eastMax = lines.filter { getPawn(it) != null && it.letter > pos.letter }.minBy { it.letter }

        if (westMax != null && getPawn(westMax)?.side == pawn.side) {
            westMax = lines.firstOrNull { it.number == pos.number && it.letter == westMax!!.letter + 1 }
        }
        if(northMax != null && getPawn(northMax)?.side == pawn.side) {
            northMax = lines.firstOrNull { it.letter == pos.letter && it.number == northMax!!.number - 1 }
        }
        if(southMax != null && getPawn(southMax)?.side == pawn.side) {
            southMax = lines.firstOrNull { it.letter == pos.letter && it.number == southMax!!.number + 1 }
        }
        if(eastMax != null && getPawn(eastMax)?.side == pawn.side) {
            eastMax = lines.firstOrNull { it.number == pos.number && it.letter == eastMax!!.letter - 1 }
        }

        lines.removeIf {
            (it == pos ||
                    westMax != null && it.number == westMax.number && it.letter < westMax.letter) ||
                    (eastMax != null && it.number == eastMax.number && it.letter > eastMax.letter) ||
                    (northMax != null && it.letter == northMax.letter && it.number > northMax.number) ||
                    (southMax != null && it.letter == southMax.letter && it.number < southMax.number)
        }

        return lines.toList()
    }

    private fun getPawnMovePossibilities(pawn: Pawn, pos: Box): List<Box>? {
        var moves = Box.values().asIterable()
        val passantNumber = if (pawn.side == ChessSide.WHITE) 5 else 4

        var linearMax = getPawnLinearFor(pawn.side, pos)
        linearMax *= if (pawn.side == ChessSide.WHITE) 1 else -1
        val diag = if (pawn.side == ChessSide.WHITE) 1 else -1
        val positions = moves.filter {
            (it.letter == pos.letter && getPawnMaxForSide(pawn.side, linearMax, pos.number, it.number) && getPawn(it) == null) || //Check linear
                    (it.letter == pos.letter + 1 && it.number == pos.number + diag && getPawn(it)?.side == getOppositeSide(pawn.side)) || //Check diagonal
                    (it.letter == pos.letter - 1 && it.number == pos.number + diag && getPawn(it)?.side == getOppositeSide(pawn.side)) //Check diagonal
        }.toMutableList()

        if(positions.isNotEmpty()) {
            val badPos = positions.firstOrNull { it.letter == pos.letter && (linearMax == -2 || linearMax == 2) && it.number == pos.number + linearMax }
            badPos?.also {
                val target = boxes.toList().first { box -> box.first.letter == pos.letter && box.first.number == it.number - diag }
                if (getPawn(target.first) != null) positions.remove(badPos)
            }
        }

        if (pos.number == passantNumber) {
            doubleMovePawn?.also {
                val doubleBox = getBoxOrNull(it)
                if (doubleBox != null) {
                    val posDiff = doubleBox.letter - pos.letter
                    if (posDiff == 1 || posDiff == -1) {
                        val passantBox = Box.valueOf("${pos.letter + posDiff}${pos.number + linearMax}")
                        this.passantCapture = passantBox
                        positions.add(passantBox)
                    }
                }
            }
        }

        return positions.toList()
    }

    private fun getPawnLinearFor(side: ChessSide, box: Box): Int {
        if ((side == ChessSide.WHITE && box.number == 2) || (side == ChessSide.BLACK && box.number == 7)) {
            return 2
        }

        return 1
    }

    private fun getPawnMaxForSide(side: ChessSide, max: Int, posNumber: Int, targetNumber: Int): Boolean {
        if (side == ChessSide.WHITE) {
            return targetNumber <= posNumber + max && targetNumber > posNumber
        }

        return targetNumber >= posNumber + max && targetNumber < posNumber
    }

    private fun getCastlingsTypeAvailable(side: ChessSide): RookType {
        val bigBox: Box
        val smallBox: Box
        val moves = getSideHistorical(side)
        val kingBox: Box

        if (side == ChessSide.WHITE) {
            bigBox = Box.A1
            smallBox = Box.H1
            kingBox = Box.E1
        } else {
            bigBox = Box.A8
            smallBox = Box.H8
            kingBox = Box.E8
        }

        //Check if pawns are in spots
        if (getPawn(kingBox) == null) return RookType.NONE
        if (getPawn(bigBox) == null && getPawn(smallBox) == null) return RookType.NONE

        //Check if pawns have moved
        val rooksMoves = moves.filter { it.from == bigBox || it.from == smallBox || it.from == kingBox }
        val bigHasMoved = rooksMoves.firstOrNull { it.from == bigBox }
        val smallHasMoved = rooksMoves.firstOrNull { it.from == smallBox }

        if (rooksMoves.firstOrNull { it.from == kingBox } != null) return RookType.NONE
        if (bigHasMoved != null && smallHasMoved != null) return RookType.NONE
        if (bigHasMoved != null) return RookType.SMALL
        if (smallHasMoved != null) return RookType.BIG

        return RookType.ALL
    }

    private fun undo(): Boolean {
        if (playsHistoric.isEmpty()) return false
        val lastMove = playsHistoric.last()
        movePawn(lastMove.to, lastMove.from)
        boxes[lastMove.to] = lastMove.pawnTaken
        playsHistoric.remove(lastMove)
        return true
    }

    fun getBox(pawn: Pawn): Box {
        return boxes.asIterable().first { it.value === pawn}.key
    }

    fun getBoxOrNull(pawn: Pawn): Box? {
        return boxes.asIterable().firstOrNull { it.value === pawn}?.key
    }

    fun getMovePossibilities(box: Box): List<Box>? {
        val pawn = getPawn(box) ?: return null
        return when (pawn.type) {
            PawnType.PAWN -> getPawnMovePossibilities(pawn ,box)
            PawnType.KNIGHT -> getKnightMovePossibilities(pawn, box)
            PawnType.QUEEN -> getQueenMovePossibilities(pawn, box)
            PawnType.ROOK -> getRookMovePossibilities(pawn, box)
            PawnType.BISHOP -> getBishopMovePossibilities(pawn, box)
            PawnType.KING -> getKingMovePossibilities(pawn, box)
        }
    }

    private fun checkPassantCapture(from: Box, to: Box) {
        val pawn = getPawn(from)
        val passant = passantCapture ?: return
        if (pawn == null || pawn.type != PawnType.PAWN || to != passant) return
        doubleMovePawn?.also {
            val capture = getBoxOrNull(it) ?: return
            boxes[capture] = null
            lastmoveIsPassantCapture = true
        }

    }

    private fun setDoubleMovePawn(from: Box, to: Box) {
        this.doubleMovePawn = if (checkPawnDoubleMove(from, to)) {
            getPawn(from)
        } else {
            null
        }
    }

    fun getPawnsWichCanMove(side: ChessSide): List<Pawn> {
        val pawns = getAllPawnsForSide(side).toMutableList()

        pawns.removeIf {
            val possibilities: List<Box>? = getMovePossibilities(getBox(it))
            possibilities == null || possibilities.isEmpty()
        }

        return pawns
    }

    private fun getPawnsWichCanMove(pawns: List<Pawn>): List<Pawn> {
        val list = pawns.toMutableList()
        list.removeIf {
            val possibilities = getMovePossibilities(getBox(it))
            possibilities == null || possibilities.isEmpty()
        }

        return list.toList()
    }

    private fun getAllPawnsForSide(side: ChessSide): List<Pawn> {
        val list: MutableList<Pawn> = mutableListOf()
        boxes.forEach { it.value?.also { p -> if (p.side == side) list.add(p) } }
        return list
    }

    private fun checkPawnDoubleMove(from: Box, to: Box): Boolean {
        val pawn = getPawn(from)
        if (pawn == null || pawn.type != PawnType.PAWN) return false
        val diff = if (from.number > to.number) {
            from.number - to.number
        } else {
            to.number - from.number
        }
        if (diff != 2) return false
        return  true
    }

    fun getChessboard(): List<Pair<Box, Pawn?>> {
        return boxes.toList()
    }

    override fun gradeMove(from: Box, to: Box): Int {
        var grade = Int.MIN_VALUE
        val middle = arrayOf(Box.D4, Box.D5, Box.E4, Box.E5)
        val pawn = getPawn(from) ?: return grade
        val pawns = getAllPawnsForSide(pawn.side)
        val targetPawn = getPawn(to)
        val pawnsPossibilities = getAllMovePossibilities(getPawnsWichCanMove(pawns))

        if (from == to) return grade

        movePawn(from, to) //set pawn to position
        if (getKingStatus(pawn.side) != KingStatus.FREE) {
            movePawn(to, from)
            boxes[to] = targetPawn
            return grade
        }
        val oppositePawns = getAllPawnsForSide(getOppositeSide(pawn.side))

        pawns.forEach { grade += it.type.points } // grade difference of number of pawns in board
        oppositePawns.forEach { grade -= it.type.points }
        middle.forEach { getPawn(it)?.also { p -> if (p.side == pawn.side) grade += 50 } } // grade middle control
        middle.forEach {  if (pawnsPossibilities.contains(it)) grade += 50 }
        val oppositeKing = getKingStatus(getOppositeSide(pawn.side))
        when (oppositeKing) {
            KingStatus.FREE -> {}
            KingStatus.CHECKED -> grade += 200
            KingStatus.MAT -> grade += 10000
        }

        movePawn(to, from)
        boxes[to] = targetPawn

        return grade
    }

    fun clone(): ChessBoard {
        val clone = ChessBoard()
        clone.boxes.clear()
        clone.boxes.putAll(this.boxes)
        clone.sidePlaying = this.sidePlaying
        clone.playCount = this.playCount
        clone.playsHistoric = ArrayList(this.playsHistoric)
        clone.lastmoveIsCastling = this.lastmoveIsCastling
        clone.lastmoveIsPassantCapture = this.lastmoveIsPassantCapture
        clone.doubleMovePawn = this.doubleMovePawn
        clone.passantCapture = this.passantCapture
        clone.castlingState = this.castlingState

        return clone
    }
}