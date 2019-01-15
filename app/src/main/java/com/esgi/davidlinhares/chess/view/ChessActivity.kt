package com.esgi.davidlinhares.chess.view

import android.graphics.Color
import android.media.MediaCodec.MetricsConstants.MODE
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.esgi.davidlinhares.chess.R
import com.esgi.davidlinhares.chess.game.ChessBoard
import com.esgi.davidlinhares.chess.game.Game
import com.esgi.davidlinhares.chess.model.Box
import com.esgi.davidlinhares.chess.model.GameType
import com.esgi.davidlinhares.chess.model.Pawn
import com.esgi.davidlinhares.chess.utils.Animator

class ChessActivity : AppCompatActivity(), ChessActivityListener {
    private var viewsHighlighted: MutableList<View> = mutableListOf()
    private lateinit var chessRecyclerView: RecyclerView
    private lateinit var chessKingStatusTextView: TextView
    private lateinit var undoButton: Button
    private lateinit var replayButton: Button
    private lateinit var menuButton: Button
    private lateinit var chessPresenter: ChessPresenter
    private lateinit var movesCount: TextView

    override fun onBackPressed() {
        return
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chessRecyclerView = findViewById(R.id.chessRecyclerView)
        chessKingStatusTextView = findViewById(R.id.chessKingStatusTextView)
        undoButton = findViewById(R.id.undo_button)
        movesCount = findViewById(R.id.moves_count_textview)
        menuButton = findViewById(R.id.menu_button)
        replayButton = findViewById(R.id.reset_button)

        val mode = intent.getStringExtra(getString(R.string.MODE))
        chessPresenter = if (mode == GameType.SINGLE_PLAYER.name)
            ChessPresenter(Game(ChessBoard(), GameType.SINGLE_PLAYER), this)
        else
            ChessPresenter(Game(ChessBoard(), GameType.VERSUS), this)

        undoButton.setOnClickListener { chessPresenter.undoButtonClicked() }
        menuButton.setOnClickListener { chessPresenter.menuButtonClicked() }
        replayButton.setOnClickListener { chessPresenter.replayButtonClicked() }

        chessPresenter.listener = this

        val adapter = ChessRecyclerAdapter(this, chessPresenter.getChessboardList())
        adapter.listener = this

        chessRecyclerView.adapter = adapter
        chessRecyclerView.layoutManager = GridLayoutManager(this,  8)
    }

    override fun onBoxViewClicked(box: Box, pawn: Pawn?) {
        chessPresenter.boxSelectedAction(box, pawn)
    }

    override fun onBoxSelectedRetrieved(boxes: List<Box>) {
        if (boxes.isEmpty()) return
        viewsHighlighted.forEach { it.setBackgroundColor(getColor(R.color.brown_board)) }
        boxes.forEach { box ->
            val index = chessPresenter.getChessboardList().indexOfFirst { box == it.first }
            val view = chessRecyclerView.layoutManager?.findViewByPosition(index)
            view?.also {
                it.setBackgroundColor(Color.GREEN)
                viewsHighlighted.add(it)
            }
        }
    }

    override fun onPawnMovementSuccess() {
        viewsHighlighted.clear()
        undoButton.visibility = View.VISIBLE
        val adapter = chessRecyclerView.adapter
        if (adapter is ChessRecyclerAdapter) adapter.data = chessPresenter.getChessboardList()
        movesCount.text = chessPresenter.moves.toString()
        adapter?.notifyDataSetChanged()
        if (chessPresenter.isKingChecked()) {
            chessKingStatusTextView.text = getString(R.string.king_checked)
            Animator.fadeInAndOutAnimation(chessKingStatusTextView, 750)
        } else if (chessPresenter.isKingMat()) {
            undoButton.visibility = View.GONE
            chessKingStatusTextView.text = getString(R.string.king_mat)
            Animator.fadeInAnimation(chessKingStatusTextView, 750)
            // wait then show replay menu
        }
    }

    override fun onPawnMovementError() {
        viewsHighlighted.clear()
        chessRecyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onUndoActionCompleted() {
        val adapter = chessRecyclerView.adapter
        if (adapter is ChessRecyclerAdapter) adapter.data = chessPresenter.getChessboardList()
        movesCount.text = chessPresenter.moves.toString()
        adapter?.notifyDataSetChanged()
        if (chessPresenter.moves < 1) undoButton.visibility = View.GONE
    }

    override fun onGameReseted() {
        onUndoActionCompleted()
    }
}
