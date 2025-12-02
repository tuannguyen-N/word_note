package com.example.wordnote.utils

import android.annotation.SuppressLint
import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.wordnote.adapter.WordAdapter

//class SwipeHelper : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
//    override fun onMove(
//        rv: RecyclerView,
//        vh: RecyclerView.ViewHolder,
//        target: RecyclerView.ViewHolder
//    ) = false
//
//    override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) {
//    }
//
//    override fun onChildDraw(
//        c: Canvas,
//        rv: RecyclerView,
//        vh: RecyclerView.ViewHolder,
//        dX: Float,
//        dY: Float,
//        actionState: Int,
//        isCurrentlyActive: Boolean
//    ) {
//        val holder = vh as WordAdapter.WordViewHolder
//        val foreground = holder.binding.containerForeground
//        val background = holder.binding.containerActions
//
//        val maxReveal = background.width
//        val newDX = dX.coerceIn(-maxReveal.toFloat(), 0f)
//
//        getDefaultUIUtil().onDraw(
//            c, rv, foreground, newDX, dY,
//            actionState, isCurrentlyActive
//        )
//    }
//
//}
