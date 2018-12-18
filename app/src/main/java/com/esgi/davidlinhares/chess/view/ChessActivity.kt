package com.esgi.davidlinhares.chess.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import ChessBoard
import android.graphics.Color
import com.esgi.davidlinhares.chess.R
import com.esgi.davidlinhares.chess.game.Game
import com.esgi.davidlinhares.chess.model.Box
import com.esgi.davidlinhares.chess.model.GameType
import com.esgi.davidlinhares.chess.model.Pawn

class ChessActivity : AppCompatActivity(), ChessActivityListener {
    override fun onBoxSelectedRetrieved(boxes: List<Box>) {
        if (boxes.isEmpty()) return
        boxes.forEach { box ->
            val index = chessPresenter.getChessboardList().indexOfFirst { box == it.first }
            val view = chessRecyclerView.layoutManager?.findViewByPosition(index)
            view?.also { it.setBackgroundColor(Color.GREEN) }
        }
    }

    override fun onPawnMovementSucees() {
        val adapter = chessRecyclerView.adapter
        if (adapter is ChessRecyclerAdapter) adapter.data = chessPresenter.getChessboardList()
        adapter?.notifyDataSetChanged()
    }

    override fun onPawnMovementError() {
        chessRecyclerView.adapter?.notifyDataSetChanged()
    }

    private lateinit var chessRecyclerView: RecyclerView
    private var chessPresenter = ChessPresenter(Game(ChessBoard(), GameType.VERSUS)) //MUST BE MODIFIED ON IA IMPLEMENTATION

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        chessRecyclerView = findViewById(R.id.chessRecyclerView)

        chessPresenter.listener = this

        val adapter = ChessRecyclerAdapter(this, chessPresenter.getChessboardList())
        adapter.listener = this

        chessRecyclerView.adapter = adapter
        chessRecyclerView.layoutManager = GridLayoutManager(this,  8)
    }

    override fun onBoxViewClicked(box: Box, pawn: Pawn?) {
        chessPresenter.boxSelectedAction(box, pawn)
    }
}
