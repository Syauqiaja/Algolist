package com.aglotest.algolist.utils

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.aglotest.algolist.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun formatDate(dateString: String): String {
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    val date: Date = dateFormatter.parse(dateString)

    val calendar = Calendar.getInstance()
    val today = calendar.time
    calendar.add(Calendar.DAY_OF_YEAR, 1)
    val tomorrow = calendar.time
    calendar.add(Calendar.DAY_OF_YEAR, -2)
    val yesterday = calendar.time

    val outputFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
    val formattedDate = when {
        isSameDay(date, yesterday) -> "Yesterday (${outputFormatter.format(date)})"
        isSameDay(date, today) -> "Today (${outputFormatter.format(date)})"
        isSameDay(date, tomorrow) -> "Tomorrow (${outputFormatter.format(date)})"
        else -> outputFormatter.format(date)
    }

    return formattedDate
}

fun isSameDay(date1: Date, date2: Date): Boolean {
    val calendar1 = Calendar.getInstance().apply { time = date1 }
    val calendar2 = Calendar.getInstance().apply { time = date2 }
    return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
            calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
}

fun <T> Fragment.getNavigationResult(key: String = "result") =
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)
fun <T> Fragment.clearNavigationResult(key: String = "result") = findNavController().currentBackStackEntry?.savedStateHandle?.remove<T>(key)
fun <T> Fragment.setNavigationResult(result: T, key: String = "result"){
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
}
fun NavController.safeNavigate(directions: NavDirections, useDefaultAnim: Boolean = true){
    val navAction = currentDestination?.getAction(directions.actionId)
    navAction?.run {
        navAction.navOptions?.let { oldNavOptions ->
            if(useDefaultAnim){
                val navOptions = NavOptions.Builder()
                    .setEnterAnim(R.anim.from_right_slide)
                    .setExitAnim(R.anim.to_left_fade)
                    .setPopEnterAnim(R.anim.from_left_fade)
                    .setPopExitAnim(R.anim.to_right_slide)
                if(oldNavOptions.shouldRestoreState()) navOptions.setLaunchSingleTop(true)
                if(oldNavOptions.popUpToRoute != null){
                    navOptions.setPopUpTo(oldNavOptions.popUpToId, oldNavOptions.isPopUpToInclusive(), oldNavOptions.shouldRestoreState())
                }
                navigate(directions, navOptions.build())
            }else{
                navigate(directions, oldNavOptions)
            }
        } ?: navigate(directions)
    }
}

fun View.scaleView(scale: Float, duration: Long) {
    val animator = ValueAnimator.ofFloat(scaleX, scale)
    animator.addUpdateListener { valueAnimator ->
        val animatedValue = valueAnimator.animatedValue as Float
        scaleX = animatedValue
        scaleY = animatedValue
    }
    animator.duration = duration // Duration of the animation in milliseconds
    animator.start()
}

fun View.scaleViewOneShot(scale: Float, duration: Long) {
    val animator = ValueAnimator.ofFloat(scaleX, scale)
    animator.addUpdateListener { valueAnimator ->
        val animatedValue = valueAnimator.animatedValue as Float
        scaleX = animatedValue
        scaleY = animatedValue
    }
    animator.duration = duration // Duration of the animation in milliseconds
    animator.repeatMode = ValueAnimator.REVERSE
    animator.repeatCount = 1
    animator.start()
}