package com.esgi.davidlinhares.chess.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import ChessBoard
import com.esgi.davidlinhares.chess.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val chessBoard = ChessBoard()
        var chessRecyclerView: RecyclerView = findViewById(R.id.chessRecyclerView)

        chessRecyclerView.adapter = ChessboardRecyclerAdapter(
            this,
            chessBoard.getChessboard()
        )
        chessRecyclerView.layoutManager = GridLayoutManager(this,  8)
    }
}
