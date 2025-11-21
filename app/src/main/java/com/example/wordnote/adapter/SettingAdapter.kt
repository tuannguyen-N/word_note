package com.example.wordnote.adapter

import android.view.View
import com.example.wordnote.R
import com.example.wordnote.databinding.ItemSettingBinding
import com.example.wordnote.domain.model.item.SettingItem
import com.example.wordnote.ui.fragment.setting.SettingAction

class SettingAdapter(
    private val onClickAc: (SettingAction) -> Unit
) : BaseAdapter<SettingItem>() {
    override fun doGetViewType(position: Int): Int = R.layout.item_setting

    override fun doBindViewHolder(
        view: View,
        item: SettingItem,
        position: Int,
        holder: BaseViewHolder
    ) {
        ItemSettingBinding.bind(view).apply {
            tvTitle.setText(item.title)
            tvDetail.setText(item.detail)
            ivNotification.setImageResource(item.icon)
            root.setOnClickListener {
                onClickAc(item.action)
            }
        }
    }
}