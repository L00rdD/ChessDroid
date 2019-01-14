package com.esgi.davidlinhares.chess.view

import android.widget.ImageView

interface ISelectionPresenter {
    fun onVersusButtonClicked()
    fun onIaButtonClicked()
    fun setImageRotation(imageView: ImageView)
}