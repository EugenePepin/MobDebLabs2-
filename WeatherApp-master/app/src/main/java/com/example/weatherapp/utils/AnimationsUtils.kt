package com.example.weatherapp.utils
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


object AnimationUtils: ViewModel() {

    private var rotateAnimator: ObjectAnimator? = null
    fun startUpdateIconRotateAnimation(view: View) {

        if (rotateAnimator?.isRunning == true) return

        rotateAnimator = ObjectAnimator.ofFloat(view, View.ROTATION, view.rotation, view.rotation + 360f).apply {
            duration = 1000L
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }
    }

    fun stopUpdateIconRotateAnimation(view: View) {
        view.postDelayed({
            rotateAnimator?.cancel()
            rotateAnimator = null


            ObjectAnimator.ofFloat(view, View.ROTATION, view.rotation, 90f).apply {
                duration = 300L
                interpolator = DecelerateInterpolator()
                start()
            }
        }, 1000L)
    }

    fun smoothBackgroundChange(
        imageView: ImageView,
        overlayView: ImageView,
        newBackgroundRes: Int
    ) {
        overlayView.setImageResource(newBackgroundRes)
        overlayView.alpha = 0f
        overlayView.visibility = View.VISIBLE

        overlayView.animate()
            .alpha(1f)
            .setDuration(600)
            .withEndAction {
                imageView.setImageResource(newBackgroundRes)
                overlayView.visibility = View.GONE
            }
            .start()
    }
    private val isStartingWindowDone = MutableStateFlow(false)
    val isReady = isStartingWindowDone.asStateFlow()

    fun setStartingWindowDone() {
        isStartingWindowDone.value = true
    }
}


