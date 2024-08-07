package com.aglotest.algolist.presentation.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aglotest.algolist.data.entity.TaskEntity

class ItemSectionDecoration(
    private val context: Context,
    private val getItemList: () -> List<TaskEntity>
): RecyclerView.ItemDecoration() {
    private val dividerHeight = dipToPx(context, 1f)
    private val sectionItemWidth: Int by lazy {
        getScreenWidth(context)
    }
    private val sectionItemHeight: Int by lazy {
        dipToPx(context, 58f)
    }
    private val sectionViewCache = mutableMapOf<String, Bitmap>()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val list = getItemList()
        val position = parent.getChildAdapterPosition(view)
        if (list.isEmpty() || position == -1) {
            return
        }

        if (position == 0) {
            outRect.top = sectionItemHeight
            return
        }

        val currentItem = list[position]
        val prevItem = list[position - 1]

        if (currentItem.taskDate != prevItem.taskDate) { // Check if the task date is same
            outRect.top = sectionItemHeight
        } else {
            outRect.top = dividerHeight
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val list = getItemList()
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val childView = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(childView)
            if (position == -1) continue

            val itemModel = list[position]
            if (list.isNotEmpty() && (0 == position || itemModel.taskDate != list[position - 1].taskDate)) {
                val top = childView.top - sectionItemHeight
                drawSectionView(c, itemModel.taskDate, top)
            }
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val list = getItemList()
        if (list.isEmpty()) {
            return
        }
        val childCount = parent.childCount
        if (childCount <= 2) {
            return
        }

        val firstView = parent.getChildAt(0)
        val position = parent.getChildAdapterPosition(firstView)
        val itemModel = list[position]
        val text = itemModel.taskDate

        val condition = itemModel.taskDate != list[position + 1].taskDate

        drawSectionView(c, text, if (firstView.bottom <= sectionItemHeight && condition) {
            firstView.bottom - sectionItemHeight
        } else {
            0
        })
    }

    private fun drawSectionView(canvas: Canvas, text: String, top: Int) {
        val bitmap = sectionViewCache[text] ?: createSectionViewBitmap(text).also {
            sectionViewCache[text] = it
        }

        canvas.drawBitmap(bitmap, 0f, top.toFloat(), null)
    }

    private fun createSectionViewBitmap(text: String): Bitmap {
        val view = SectionViewHolder(context)
        view.setDate(text)

        val bitmap = Bitmap.createBitmap(sectionItemWidth, sectionItemHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.measure(
            View.MeasureSpec.makeMeasureSpec(sectionItemWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(sectionItemHeight, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, sectionItemWidth, sectionItemHeight)
        view.draw(canvas)
        return bitmap
    }

    private fun dipToPx(context: Context, dipValue: Float): Int {
        return (dipValue * context.resources.displayMetrics.density).toInt()
    }

    private fun getScreenWidth(context: Context): Int {
        val outMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = context.display
            display?.getRealMetrics(outMetrics)
        } else {
            val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            display.getMetrics(outMetrics)
        }
        return outMetrics.widthPixels
    }
}
