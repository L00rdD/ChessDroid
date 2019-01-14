package com.esgi.davidlinhares.chess.utils

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.icu.util.ValueIterator
import android.view.View
import android.view.animation.*
import java.time.Duration


class Animator {
    companion object {
        fun fadeInAnimation(view: View, animationDuration: Long) {
            fadeValueAnimatorOfFloat(0F, 1F, AccelerateInterpolator(), view, animationDuration).start()
        }

        fun fadeOutAnimation(view: View, animationDuration: Long) {
            fadeValueAnimatorOfFloat(1F, 0F, DecelerateInterpolator(), view, animationDuration).start()
        }

        fun fadeInAndOutAnimation(view: View, animationDuration: Long) {
            val fadeIn = fadeValueAnimatorOfFloat(0F, 1F, AccelerateInterpolator(), view, animationDuration)
            val fadeOut = fadeValueAnimatorOfFloat(1F, 0F, DecelerateInterpolator(), view, animationDuration)
            val animatorSet = AnimatorSet()

            animatorSet.play(fadeIn).before(fadeOut)
            animatorSet.start()
        }

        fun rotateAnimation(): TranslateAnimation {
            val rotateAnimation = TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 1.1f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.0f)
            rotateAnimation.duration = 1200
            rotateAnimation.repeatCount = Animation.INFINITE

            return rotateAnimation
        }

        private fun fadeValueAnimatorOfFloat(from: Float, to: Float, interpolator: Interpolator, view: View, duration: Long): ValueAnimator {
            val valueAnimator = ValueAnimator.ofFloat(from, to)

            valueAnimator.addUpdateListener {
                val value = it.animatedValue as Float
                view.alpha = value
            }

            valueAnimator.interpolator = interpolator
            valueAnimator.duration = duration

            return valueAnimator
        }
    }
}