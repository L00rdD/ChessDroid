package com.esgi.davidlinhares.chess.view

import Box
import ChessSide
import Pawn
import PawnType
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.esgi.davidlinhares.chess.R
import kotlinx.android.synthetic.main.chessboard_cell.view.*

class ChessboardRecyclerAdapter(val context: Context, val data: List<Pair<Box, Pawn?>>): RecyclerView.Adapter<ViewHolder>() {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            //Set chessboard box colors
            if (position < 8 || position in 16..23 || position in 32..39 || position in  48..55 ) {
                if (position % 2 == 1) holder.box.setBackgroundColor(Color.GRAY)
            } else {
                if (position % 2 == 0) holder.box.setBackgroundColor(Color.GRAY)
            }
            //set chessboard box pawn
            val pawn = data[position].second
            pawn?.also {
                holder.pawn.setImageDrawable(it.image())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                this.context
            ).inflate(R.layout.chessboard_cell, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return this.data.count()
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val box: View = view.box
        val pawn: ImageView = view.box_image
    }

    private fun Pawn.image(): Drawable {
        return when(this.type) {
            PawnType.PAWN -> if (side == ChessSide.WHITE) context.getDrawable(R.drawable.p_white) else context.getDrawable(
                R.drawable.p_black
            )
            PawnType.KNIGHT -> if (side == ChessSide.WHITE) context.getDrawable(R.drawable.n_white) else context.getDrawable(
                R.drawable.n_black
            )
            PawnType.QUEEN -> if (side == ChessSide.WHITE) context.getDrawable(R.drawable.q_white) else context.getDrawable(
                R.drawable.q_black
            )
            PawnType.ROOK -> if (side == ChessSide.WHITE) context.getDrawable(R.drawable.r_white) else context.getDrawable(
                R.drawable.r_black
            )
            PawnType.BISHOP -> if (side == ChessSide.WHITE) context.getDrawable(R.drawable.b_white) else context.getDrawable(
                R.drawable.b_black
            )
            PawnType.KING -> if (side == ChessSide.WHITE) context.getDrawable(R.drawable.k_white) else context.getDrawable(
                R.drawable.k_black
            )
        }
    }
}