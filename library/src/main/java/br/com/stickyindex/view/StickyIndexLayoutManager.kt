package br.com.stickyindex.view

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.RelativeLayout
import android.widget.TextView
import br.com.stickyindex.R
import br.com.stickyindex.model.RowStyle
import java.lang.Math.abs

/**
 * Encapsulates the logic that correctly displays the letters in the list, including effects for
 * section transitions - i.e. fade in/out of headers
 */
class StickyIndexLayoutManager(
        container: RelativeLayout,
        private val contentList: RecyclerView
) {
    private val header: TextView = container.findViewById(R.id.sticky_index)
    private val layoutManager: LinearLayoutManager = contentList.layoutManager as LinearLayoutManager

    private var prevVisibleItemPosition = 0
    private var savedIsDown: Boolean = true

    fun synchronizeScrolls(rv: RecyclerView) {
        val firstVisibleView = rv.getChildAt(0)

        rv.getChildAt(0)?.let {
            layoutManager.scrollToPositionWithOffset(
                    rv.getChildAdapterPosition(firstVisibleView),
                    firstVisibleView.top)
        }
    }

    /**
     * Must be called every time the reference {@link RecyclerView} scrolls. This method redraws the
     * state of the sticky letters
     */
    fun update() {
        if ((contentList.adapter?.itemCount ?: 0) < 2) return

        val firstVisibleItemContainer = contentList.getChildAt(0)
        val firstVisibleItem = firstVisibleItemContainer.findViewById<TextView>(R.id.sticky_row_index)
        displayHeader(firstVisibleItem)
    }

    private fun displayHeader(firstItem: TextView) {
        header.text = firstItem.text[0].toString().toUpperCase()
        header.visibility = VISIBLE
        firstItem.alpha = 1f
    }

    private fun animateTransitionToNext(
            first: TextView,
            firstContainer: View,
            second: TextView
    ) {
        header.visibility = INVISIBLE
        first.visibility = VISIBLE
        first.alpha = computeAlpha(firstContainer, first)
        second.visibility = VISIBLE
    }

    /**
     * Applies a {@link RowStyle} in the sticky header
     */
    fun applyStyle(style: RowStyle) {
        if (style.size > 0f) header.setTextSize(COMPLEX_UNIT_PX, style.size)
        if (style.style != 0) header.setTypeface(null, style.style)
        header.setTextColor(style.color)
    }

    private fun isScrollingDown(): Boolean {
        val nextVisiblePosition = layoutManager.findFirstVisibleItemPosition()

        val isDown: Boolean
        if (nextVisiblePosition > prevVisibleItemPosition) {
            isDown = true
        } else if (nextVisiblePosition < prevVisibleItemPosition) {
            isDown = false
        } else {
            isDown = savedIsDown
        }

        prevVisibleItemPosition = nextVisiblePosition
        savedIsDown = isDown

        return isDown
    }

    private fun computeAlpha(row: View, idx: TextView) = 1 - abs(row.y) / idx.height
    private fun isHeader(prev: TextView, act: TextView) = prev.text[0].toLowerCase() != act.text[0].toLowerCase()
}