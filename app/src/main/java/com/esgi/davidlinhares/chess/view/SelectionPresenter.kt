package com.esgi.davidlinhares.chess.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.animation.Animation
import android.widget.ImageView
import com.esgi.davidlinhares.chess.R
import com.esgi.davidlinhares.chess.model.GameType
import com.esgi.davidlinhares.chess.model.PawnType
import com.esgi.davidlinhares.chess.model.RookType
import com.esgi.davidlinhares.chess.utils.Animator

class SelectionPresenter(val context: SelectionActivity): ISelectionPresenter {
    var pawn = PawnType.KING

    override fun setImageRotation(imageView: ImageView) {
        val animation = Animator.rotateAnimation()
        animation.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {
                pawn = pawn.nextPawn()
                imageView.setImageDrawable(pawn.getDrawable())
            }

            override fun onAnimationEnd(p0: Animation?) {
            }

            override fun onAnimationStart(p0: Animation?) {
            }
        })
        imageView.animation = animation
        imageView.animation.start()
    }

    fun PawnType.getDrawable() : Drawable {
        return when (this) {
            PawnType.PAWN -> context.getDrawable(R.drawable.p_white)
            PawnType.KNIGHT -> context.getDrawable(R.drawable.n_white)
            PawnType.QUEEN -> context.getDrawable(R.drawable.q_white)
            PawnType.ROOK -> context.getDrawable(R.drawable.r_white)
            PawnType.BISHOP -> context.getDrawable(R.drawable.b_white)
            PawnType.KING -> context.getDrawable(R.drawable.k_white)
        }
    }

    fun PawnType.nextPawn(): PawnType {
        return when (this) {
            PawnType.PAWN -> PawnType.KNIGHT
            PawnType.KNIGHT -> PawnType.QUEEN
            PawnType.QUEEN -> PawnType.ROOK
            PawnType.ROOK -> PawnType.BISHOP
            PawnType.BISHOP -> PawnType.KING
            PawnType.KING -> PawnType.PAWN
        }
    }

    override fun onVersusButtonClicked() {
        val intent = Intent(this.context, ChessActivity::class.java)
        intent.putExtra(context.getString(R.string.MODE), GameType.VERSUS.name)
        context.startActivity(intent)
    }

    override fun onIaButtonClicked() {
        val intent = Intent(this.context, ChessActivity::class.java)
        intent.putExtra(context.getString(R.string.MODE), GameType.SINGLE_PLAYER.name)
        context.startActivity(intent)
    }
}